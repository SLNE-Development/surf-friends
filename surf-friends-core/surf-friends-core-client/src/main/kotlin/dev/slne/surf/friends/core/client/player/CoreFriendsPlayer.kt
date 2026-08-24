package dev.slne.surf.friends.core.client.player

import dev.slne.surf.api.core.util.freeze
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.friends.api.model.FriendRequest
import dev.slne.surf.friends.api.model.Friendship
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.result.FriendRequestCreateResult
import dev.slne.surf.friends.api.result.FriendRequestRemoveResult
import dev.slne.surf.friends.api.result.FriendRequestStateResult
import dev.slne.surf.friends.api.result.FriendshipRemoveResult
import dev.slne.surf.friends.api.utils.toSurfPlayer
import dev.slne.surf.friends.core.client.FriendsClientInstance
import dev.slne.surf.friends.core.client.rabbitApi
import dev.slne.surf.friends.core.client.redis.event.*
import dev.slne.surf.friends.core.client.redisApi
import dev.slne.surf.friends.core.common.packets.friendrequest.ChangeFriendRequestStateRequestPacket
import dev.slne.surf.friends.core.common.packets.friendrequest.CreateFriendRequestRequestPacket
import dev.slne.surf.friends.core.common.packets.friendrequest.RevokeFriendRequestRequestPacket
import dev.slne.surf.friends.core.common.packets.friendship.RemoveFriendshipRequestPacket
import dev.slne.surf.settings.api.SurfSettingsApi
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.jetbrains.annotations.UnmodifiableView
import java.time.OffsetDateTime
import java.util.*

class CoreFriendsPlayer(
    override val uuid: UUID
) : FriendsPlayer {
    override suspend fun surfPlayer(): SurfPlayer? {
        return uuid.toSurfPlayer()
    }

    override val receivedFriendRequests: @UnmodifiableView ObjectSet<FriendRequest>
        get() {
            val snapshot = friendRequestSnapshot()
            val requests = ObjectOpenHashSet<FriendRequest>()

            for (index in snapshot.indices) {
                val request = snapshot[index]
                if (request.targetUuid == uuid) requests.add(request)
            }

            return requests.freeze()
        }

    override val sentFriendRequests: @UnmodifiableView ObjectSet<FriendRequest>
        get() {
            val snapshot = friendRequestSnapshot()
            val requests = ObjectOpenHashSet<FriendRequest>()

            for (index in snapshot.indices) {
                val request = snapshot[index]
                if (request.senderUuid == uuid) requests.add(request)
            }

            return requests.freeze()
        }

    override val friendships: @UnmodifiableView ObjectSet<Friendship>
        get() {
            val snapshot = friendshipSnapshot()
            val friendships = ObjectOpenHashSet<Friendship>()

            for (index in snapshot.indices) {
                val friendship = snapshot[index]
                if (friendship.playerUuid == uuid) friendships.add(friendship)
            }

            return friendships.freeze()
        }

    override val onlineFriendUuids: @UnmodifiableView ObjectSet<UUID>
        get() {
            val snapshot = friendshipSnapshot()
            val online = ObjectOpenHashSet<UUID>()

            for (index in snapshot.indices) {
                val friendship = snapshot[index]
                if (friendship.playerUuid != uuid) continue

                val friend = SurfCoreApi.getPlayer(friendship.friendUuid) ?: continue
                online.add(friend.uuid)
            }

            return online.freeze()
        }

    override fun hasReceivedFriendRequest(target: FriendsPlayer): Boolean {
        val senderUuid = target.uuid
        val snapshot = friendRequestSnapshot()

        for (index in snapshot.indices) {
            val request = snapshot[index]
            if (request.targetUuid == uuid && request.senderUuid == senderUuid) return true
        }

        return false
    }

    override fun hasSentFriendRequest(target: FriendsPlayer): Boolean {
        val targetUuid = target.uuid
        val snapshot = friendRequestSnapshot()

        for (index in snapshot.indices) {
            val request = snapshot[index]
            if (request.senderUuid == uuid && request.targetUuid == targetUuid) return true
        }

        return false
    }

    override fun hasFriendship(target: FriendsPlayer): Boolean {
        return findFriendship(target) != null
    }

    override fun findFriendship(target: FriendsPlayer): Friendship? {
        val friendUuid = target.uuid
        val snapshot = friendshipSnapshot()

        for (index in snapshot.indices) {
            val friendship = snapshot[index]
            if (friendship.playerUuid == uuid && friendship.friendUuid == friendUuid) {
                return friendship
            }
        }

        return null
    }

    override suspend fun sendFriendRequest(target: FriendsPlayer): FriendRequestCreateResult {
        val targetUuid = target.uuid

        val snapshot = friendRequestSnapshot()
        var alreadySent = false
        var alreadyReceived = false

        for (index in snapshot.indices) {
            val request = snapshot[index]

            if (request.senderUuid == uuid && request.targetUuid == targetUuid) {
                alreadySent = true
                break
            }

            if (request.senderUuid == targetUuid && request.targetUuid == uuid) {
                alreadyReceived = true
            }
        }

        if (alreadySent) {
            return FriendRequestCreateResult.AlreadySentFriendRequest(targetUuid)
        }

        if (alreadyReceived) {
            return FriendRequestCreateResult.AlreadyReceivedFriendRequest(targetUuid)
        }

        if (hasFriendship(target)) {
            return FriendRequestCreateResult.AlreadyFriends(targetUuid)
        }

        val result = rabbitApi.sendRequest(
            CreateFriendRequestRequestPacket(
                senderUuid = uuid,
                targetUuid = targetUuid
            )
        ).result

        if (result !is FriendRequestCreateResult.Success) {
            return result
        }

        FriendsClientInstance.INSTANCE.friendRequests.add(result.friendRequest)

        redisApi.publishEvent(
            FriendRequestSendRedisEvent(
                uuid, targetUuid, target.friendRequestNotificationsEnabled
            )
        ).await()

        return result
    }

    override suspend fun revokeFriendRequest(target: FriendsPlayer): FriendRequestRemoveResult {
        if (!hasSentFriendRequest(target)) {
            return FriendRequestRemoveResult.NotSentFriendRequest(target.uuid)
        }

        val targetUuid = target.uuid

        val result = rabbitApi.sendRequest(
            RevokeFriendRequestRequestPacket(
                senderUuid = uuid,
                targetUuid = targetUuid
            )
        ).result

        if (result !is FriendRequestRemoveResult.Success) {
            return result
        }

        FriendsClientInstance.INSTANCE.friendRequests.removeIf {
            it.senderUuid == uuid && it.targetUuid == targetUuid
        }

        redisApi.publishEvent(
            FriendRequestRevokeRedisEvent(
                uuid, targetUuid, true
            )
        ).await()

        return result
    }

    override suspend fun acceptFriendRequest(target: FriendsPlayer): FriendRequestStateResult {
        if (!hasReceivedFriendRequest(target)) {
            return FriendRequestStateResult.NoFriendRequest(target.uuid)
        }

        val targetUuid = target.uuid

        val result = rabbitApi.sendRequest(
            ChangeFriendRequestStateRequestPacket(
                senderUuid = targetUuid,
                targetUuid = uuid,
                state = ChangeFriendRequestStateRequestPacket.State.ACCEPT
            )
        ).result

        if (result !is FriendRequestStateResult.Success) {
            return result
        }

        FriendsClientInstance.INSTANCE.friendRequests.removeIf {
            it.senderUuid == targetUuid && it.targetUuid == uuid
        }

        val now = OffsetDateTime.now()

        FriendsClientInstance.INSTANCE.friendships.add(
            Friendship(
                playerUuid = uuid,
                friendUuid = targetUuid,
                createdAt = now
            )
        )

        FriendsClientInstance.INSTANCE.friendships.add(
            Friendship(
                playerUuid = targetUuid,
                friendUuid = uuid,
                createdAt = now
            )
        )

        redisApi.publishEvent(
            FriendRequestAcceptRedisEvent(
                uuid, targetUuid
            )
        ).await()

        return result
    }

    override suspend fun declineFriendRequest(target: FriendsPlayer): FriendRequestStateResult {
        if (!hasReceivedFriendRequest(target)) {
            return FriendRequestStateResult.NoFriendRequest(target.uuid)
        }

        val targetUuid = target.uuid

        val result = rabbitApi.sendRequest(
            ChangeFriendRequestStateRequestPacket(
                senderUuid = targetUuid,
                targetUuid = uuid,
                state = ChangeFriendRequestStateRequestPacket.State.DECLINE
            )
        ).result

        if (result !is FriendRequestStateResult.Success) {
            return result
        }

        FriendsClientInstance.INSTANCE.friendRequests.removeIf {
            it.senderUuid == targetUuid && it.targetUuid == uuid
        }

        redisApi.publishEvent(
            FriendRequestDenyRedisEvent(
                uuid, targetUuid
            )
        ).await()

        return result
    }

    override suspend fun removeFriendship(target: FriendsPlayer): FriendshipRemoveResult {
        if (!hasFriendship(target)) {
            return FriendshipRemoveResult.NotFriends(target.uuid)
        }

        val targetUuid = target.uuid

        val result = rabbitApi.sendRequest(
            RemoveFriendshipRequestPacket(
                senderUuid = uuid,
                targetUuid = targetUuid
            )
        ).result

        if (result !is FriendshipRemoveResult.Success) {
            return result
        }

        redisApi.publishEvent(
            FriendRemoveRedisEvent(
                uuid, targetUuid
            )
        ).await()

        FriendsClientInstance.INSTANCE.friendships.removeIf {
            (it.playerUuid == uuid && it.friendUuid == targetUuid) ||
                    (it.playerUuid == targetUuid && it.friendUuid == uuid)
        }

        return result
    }

    override val notificationsEnabled: Boolean
        get() = SurfSettingsApi.getPlayerSetting(
            uuid,
            FriendsClientInstance.SETTINGS_NOTIFICATIONS_ENABLED_KEY
        )?.getBoolean() ?: false

    override suspend fun setNotificationsEnabled(value: Boolean) {
        SurfSettingsApi.saveSetting(
            uuid,
            FriendsClientInstance.SETTINGS_NOTIFICATIONS_ENABLED_KEY,
            value.toString()
        )
    }

    override val soundsEnabled: Boolean
        get() = SurfSettingsApi.getPlayerSetting(
            uuid,
            FriendsClientInstance.SETTINGS_SOUNDS_ENABLED_KEY
        )?.getBoolean() ?: false

    override suspend fun setSoundsEnabled(value: Boolean) {
        SurfSettingsApi.saveSetting(
            uuid,
            FriendsClientInstance.SETTINGS_SOUNDS_ENABLED_KEY,
            value.toString()
        )
    }

    override val friendRequestNotificationsEnabled: Boolean
        get() = SurfSettingsApi.getPlayerSetting(
            uuid,
            FriendsClientInstance.SETTINGS_FRIEND_REQUEST_NOTIFICATIONS_ENABLED_KEY
        )?.getBoolean() ?: false

    override suspend fun setFriendRequestNotificationsEnabled(value: Boolean) {
        SurfSettingsApi.saveSetting(
            uuid,
            FriendsClientInstance.SETTINGS_FRIEND_REQUEST_NOTIFICATIONS_ENABLED_KEY,
            value.toString()
        )
    }

    private fun friendRequestSnapshot(): ObjectArrayList<FriendRequest> =
        FriendsClientInstance.INSTANCE.friendRequests.snapshot()

    private fun friendshipSnapshot(): ObjectArrayList<Friendship> =
        FriendsClientInstance.INSTANCE.friendships.snapshot()
}
