package dev.slne.surf.friends.core.service

import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.surfapi.core.api.util.requiredService

val friendRequestService = requiredService<FriendRequestService>()

interface FriendRequestService {
    suspend fun saveFriendRequest(friendRequest: FriendRequest)
    suspend fun deleteFriendRequest(friendRequest: FriendRequest)
}