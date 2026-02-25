package dev.slne.surf.friends.core.service

import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.friends.api.friend.Friendship
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

/**
 * Service interface for managing friendships and related actions.
 */
interface FriendService {
    suspend fun createFriendship(uuid: UUID, friend: UUID): Friendship
    suspend fun removeFriendship(uuid: UUID, friend: UUID)
    suspend fun getFriendship(playerA: UUID, playerB: UUID): Friendship?
    suspend fun areFriends(uuid: UUID, friend: UUID): Friendship?
    suspend fun getFriendships(uuid: UUID): ObjectSet<Friendship>
    suspend fun sendFriendRequest(sender: UUID, receiver: UUID): FriendRequest
    suspend fun acceptFriendRequest(sender: UUID, receiver: UUID)
    suspend fun declineFriendRequest(sender: UUID, receiver: UUID)
    suspend fun revokeFriendRequest(sender: UUID, receiver: UUID)
    suspend fun getSentFriendRequests(uuid: UUID): ObjectSet<FriendRequest>
    suspend fun getReceivedFriendRequests(uuid: UUID): ObjectSet<FriendRequest>
    suspend fun getFriendRequest(sender: UUID, target: UUID): FriendRequest?

    companion object {
        val INSTANCE = requiredService<FriendService>()
    }
}

/**
 * Extension function to get the friend service instance.
 */
val friendService get() = FriendService.INSTANCE