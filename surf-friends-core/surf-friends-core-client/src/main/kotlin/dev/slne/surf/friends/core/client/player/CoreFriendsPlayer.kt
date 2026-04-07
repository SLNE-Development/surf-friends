package dev.slne.surf.friends.core.client.player

import dev.slne.surf.api.core.util.toObjectSet
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
import dev.slne.surf.friends.core.client.redis.event.FriendRemoveRedisEvent
import dev.slne.surf.friends.core.client.redis.event.FriendRequestAcceptRedisEvent
import dev.slne.surf.friends.core.client.redis.event.FriendRequestDenyRedisEvent
import dev.slne.surf.friends.core.client.redis.event.FriendRequestRevokeRedisEvent
import dev.slne.surf.friends.core.client.redis.event.FriendRequestSendRedisEvent
import dev.slne.surf.friends.core.client.redisApi
import dev.slne.surf.friends.core.common.packets.friendrequest.ChangeFriendRequestStateRequestPacket
import dev.slne.surf.friends.core.common.packets.friendrequest.CreateFriendRequestRequestPacket
import dev.slne.surf.friends.core.common.packets.friendrequest.RevokeFriendRequestRequestPacket
import dev.slne.surf.friends.core.common.packets.friendship.RemoveFriendshipRequestPacket
import dev.slne.surf.settings.api.SurfSettingsApi
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
        get() = FriendsClientInstance.INSTANCE.friendRequests.snapshot().filter {
            it.targetUuid == uuid
        }.toObjectSet()

    override val sentFriendRequests: @UnmodifiableView ObjectSet<FriendRequest>
        get() = FriendsClientInstance.INSTANCE.friendRequests.snapshot().filter {
            it.senderUuid == uuid
        }.toObjectSet()

    override val friendships: @UnmodifiableView ObjectSet<Friendship>
        get() = FriendsClientInstance.INSTANCE.friendships.snapshot().filter {
            it.targetUuid == uuid
        }.toObjectSet()

    override val onlineFriendUuids: @UnmodifiableView ObjectSet<UUID>
        get() = friendships.mapNotNull {
            val sender = SurfCoreApi.getPlayer(it.senderUuid) ?: return@mapNotNull null
            val receiver = SurfCoreApi.getPlayer(it.targetUuid) ?: return@mapNotNull null

            sender to receiver
        }.map {
            if (it.first.uuid == uuid) {
                it.second.uuid
            } else {
                it.first.uuid
            }
        }.toObjectSet()

    override fun hasReceivedFriendRequest(target: FriendsPlayer): Boolean {
        return receivedFriendRequests.firstOrNull { it.senderUuid == target.uuid && it.targetUuid == uuid } != null
    }

    override fun hasSentFriendRequest(target: FriendsPlayer): Boolean {
        return sentFriendRequests.firstOrNull { it.senderUuid == uuid && it.targetUuid == target.uuid } != null
    }

    override fun hasFriendship(target: FriendsPlayer): Boolean {
        return findFriendship(target) != null
    }

    override fun findFriendship(target: FriendsPlayer): Friendship? {
        return friendships.firstOrNull {
            (it.senderUuid == uuid && it.targetUuid == target.uuid) ||
                    (it.senderUuid == target.uuid && it.targetUuid == uuid)
        }
    }

    override suspend fun sendFriendRequest(target: FriendsPlayer): FriendRequestCreateResult {
        if (hasSentFriendRequest(target)) {
            return FriendRequestCreateResult.AlreadySentFriendRequest(target.uuid)
        }

        if (hasReceivedFriendRequest(target)) {
            return FriendRequestCreateResult.AlreadyReceivedFriendRequest(target.uuid)
        }

        if (hasFriendship(target)) {
            return FriendRequestCreateResult.AlreadyFriends(target.uuid)
        }

        val result = rabbitApi.sendRequest(
            CreateFriendRequestRequestPacket(
                senderUuid = uuid,
                targetUuid = target.uuid
            )
        ).result

        if (result !is FriendRequestCreateResult.Success) {
            return result
        }

        FriendsClientInstance.INSTANCE.friendRequests.add(result.friendRequest)

        redisApi.publishEvent(
            FriendRequestSendRedisEvent(
                uuid, target.uuid, target.friendRequestNotificationsEnabled
            )
        )

        return result
    }

    override suspend fun revokeFriendRequest(target: FriendsPlayer): FriendRequestRemoveResult {
        if (!hasSentFriendRequest(target)) {
            return FriendRequestRemoveResult.NotSentFriendRequest(target.uuid)
        }

        val result = rabbitApi.sendRequest(
            RevokeFriendRequestRequestPacket(
                senderUuid = uuid,
                receiverUuid = target.uuid
            )
        ).result

        if (result !is FriendRequestRemoveResult.Success) {
            return result
        }

        FriendsClientInstance.INSTANCE.friendRequests.removeIf {
            (it.senderUuid == uuid && it.targetUuid == target.uuid)
        }

        redisApi.publishEvent(
            FriendRequestRevokeRedisEvent(
                uuid, target.uuid, true
            )
        )

        return result
    }

    override suspend fun acceptFriendRequest(target: FriendsPlayer): FriendRequestStateResult {
        if (!hasReceivedFriendRequest(target)) {
            return FriendRequestStateResult.NoFriendRequest(target.uuid)
        }

        val result = rabbitApi.sendRequest(
            ChangeFriendRequestStateRequestPacket(
                senderUuid = target.uuid,
                targetUuid = uuid,
                state = ChangeFriendRequestStateRequestPacket.State.ACCEPT
            )
        ).result

        if (result !is FriendRequestStateResult.Success) {
            return result
        }

        FriendsClientInstance.INSTANCE.friendRequests.removeIf {
            (it.senderUuid == target.uuid && it.targetUuid == target.uuid)
                    || (it.senderUuid == target.uuid && it.targetUuid == target.uuid)
        }

        FriendsClientInstance.INSTANCE.friendships.add(
            Friendship(
                senderUuid = uuid,
                targetUuid = target.uuid,
                createdAt = OffsetDateTime.now()
            )
        )

        redisApi.publishEvent(
            FriendRequestAcceptRedisEvent(
                uuid, target.uuid
            )
        )

        return result
    }

    override suspend fun declineFriendRequest(target: FriendsPlayer): FriendRequestStateResult {
        if (!hasReceivedFriendRequest(target)) {
            return FriendRequestStateResult.NoFriendRequest(target.uuid)
        }

        val result = rabbitApi.sendRequest(
            ChangeFriendRequestStateRequestPacket(
                senderUuid = target.uuid,
                targetUuid = uuid,
                state = ChangeFriendRequestStateRequestPacket.State.DECLINE
            )
        ).result

        if (result !is FriendRequestStateResult.Success) {
            return result
        }

        FriendsClientInstance.INSTANCE.friendRequests.removeIf {
            (it.senderUuid == target.uuid && it.targetUuid == target.uuid)
                    || (it.senderUuid == target.uuid && it.targetUuid == target.uuid)
        }

        redisApi.publishEvent(
            FriendRequestDenyRedisEvent(
                uuid, target.uuid
            )
        )

        return result
    }

    override suspend fun removeFriendship(target: FriendsPlayer): FriendshipRemoveResult {
        if (!hasFriendship(target)) {
            return FriendshipRemoveResult.NotFriends(target.uuid)
        }

        val result = rabbitApi.sendRequest(
            RemoveFriendshipRequestPacket(
                senderUuid = uuid,
                targetUuid = target.uuid
            )
        ).result

        if (result !is FriendshipRemoveResult.Success) {
            return result
        }

        redisApi.publishEvent(
            FriendRemoveRedisEvent(
                uuid, target.uuid
            )
        )

        FriendsClientInstance.INSTANCE.friendships.removeIf {
            (it.senderUuid == uuid && it.targetUuid == target.uuid) ||
                    (it.senderUuid == target.uuid && it.targetUuid == uuid)
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
}