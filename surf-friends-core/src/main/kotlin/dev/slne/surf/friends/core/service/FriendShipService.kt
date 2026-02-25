package dev.slne.surf.friends.core.service

import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.surfapi.core.api.util.requiredService

val friendShipService = requiredService<FriendShipService>()

interface FriendShipService {
    suspend fun saveFriendRequest(friendRequest: FriendRequest)
}