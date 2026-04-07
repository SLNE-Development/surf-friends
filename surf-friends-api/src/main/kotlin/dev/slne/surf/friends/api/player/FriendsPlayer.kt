package dev.slne.surf.friends.api.player

import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.friends.api.model.FriendRequest
import dev.slne.surf.friends.api.model.Friendship
import dev.slne.surf.friends.api.result.FriendRequestCreateResult
import dev.slne.surf.friends.api.result.FriendRequestRemoveResult
import dev.slne.surf.friends.api.result.FriendRequestStateResult
import dev.slne.surf.friends.api.result.FriendshipRemoveResult
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.jetbrains.annotations.UnmodifiableView
import java.util.*

interface FriendsPlayer {
    val uuid: UUID

    suspend fun surfPlayer(): SurfPlayer?

    val receivedFriendRequests: @UnmodifiableView ObjectSet<FriendRequest>
    val sentFriendRequests: @UnmodifiableView ObjectSet<FriendRequest>

    val friendships: @UnmodifiableView ObjectSet<Friendship>
    val onlineFriendUuids: @UnmodifiableView ObjectSet<UUID>

    suspend fun sendFriendRequest(target: FriendsPlayer): FriendRequestCreateResult
    suspend fun revokeFriendRequest(target: FriendsPlayer): FriendRequestRemoveResult

    suspend fun acceptFriendRequest(target: FriendsPlayer): FriendRequestStateResult
    suspend fun declineFriendRequest(target: FriendsPlayer): FriendRequestStateResult

    fun hasSentFriendRequest(target: FriendsPlayer): Boolean
    fun hasReceivedFriendRequest(target: FriendsPlayer): Boolean

    fun findFriendship(target: FriendsPlayer): Friendship?
    fun hasFriendship(target: FriendsPlayer): Boolean

    suspend fun removeFriendship(target: FriendsPlayer): FriendshipRemoveResult

    val notificationsEnabled: Boolean
    suspend fun setNotificationsEnabled(value: Boolean)

    val soundsEnabled: Boolean
    suspend fun setSoundsEnabled(value: Boolean)

    val friendRequestNotificationsEnabled: Boolean
    suspend fun setFriendRequestNotificationsEnabled(value: Boolean)

    companion object {
        operator fun get(uuid: UUID): FriendsPlayer = FriendsPlayerManager.findPlayer(uuid)
    }
}