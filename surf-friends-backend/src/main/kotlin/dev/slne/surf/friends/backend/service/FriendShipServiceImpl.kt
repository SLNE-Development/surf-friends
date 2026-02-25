package dev.slne.surf.friends.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.friends.api.friend.Friendship
import dev.slne.surf.friends.backend.repository.friendShipRepository
import dev.slne.surf.friends.core.service.FriendShipService
import net.kyori.adventure.util.Services

@AutoService(FriendShipService::class)
class FriendShipServiceImpl : FriendShipService, Services.Fallback {
    override suspend fun saveFriendShip(friendShip: Friendship) {
        friendShipRepository.saveFriendship(friendShip)
    }
}