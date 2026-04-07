package dev.slne.surf.friends.core.service

import dev.slne.surf.friends.api.friend.Friendship
import dev.slne.surf.surfapi.core.api.util.requiredService

val friendShipService = requiredService<FriendShipService>()

interface FriendShipService {
    suspend fun saveFriendShip(friendShip: Friendship)
    suspend fun deleteFriendShip(friendShip: Friendship)
}