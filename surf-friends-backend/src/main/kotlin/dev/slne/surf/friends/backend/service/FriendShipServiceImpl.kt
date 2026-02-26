package dev.slne.surf.friends.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.friends.api.friend.Friendship
import dev.slne.surf.friends.backend.repository.friendShipRepository
import dev.slne.surf.friends.core.service.FriendShipService
import dev.slne.surf.friends.core.service.friendPlayerService
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import net.kyori.adventure.util.Services

@AutoService(FriendShipService::class)
class FriendShipServiceImpl : FriendShipService, Services.Fallback {
    override suspend fun saveFriendShip(friendShip: Friendship) {
        friendShipRepository.saveFriendship(friendShip)

        val cachedPlayers = friendPlayerService.players

        val requester = cachedPlayers.firstOrNull { it.uuid == friendShip.requestedBy }
        if (requester != null) {
            val updated = requester.copy(
                friends = (requester.friends + friendShip).toObjectSet()
            )
            friendPlayerService.cachePlayer(updated)
        }

        val accepter = cachedPlayers.firstOrNull { it.uuid == friendShip.acceptedBy }
        if (accepter != null) {
            val updated = accepter.copy(
                friends = (accepter.friends + friendShip).toObjectSet()
            )
            friendPlayerService.cachePlayer(updated)
        }
    }

    override suspend fun deleteFriendShip(friendShip: Friendship) {
        friendShipRepository.deleteFriendship(friendShip)

        val cachedPlayers = friendPlayerService.players

        val requester = cachedPlayers.firstOrNull { it.uuid == friendShip.requestedBy }
        if (requester != null) {
            val updated = requester.copy(
                friends = requester.friends
                    .filterNot { it == friendShip }
                    .toObjectSet()
            )
            friendPlayerService.cachePlayer(updated)
        }

        val accepter = cachedPlayers.firstOrNull { it.uuid == friendShip.acceptedBy }
        if (accepter != null) {
            val updated = accepter.copy(
                friends = accepter.friends
                    .filterNot { it == friendShip }
                    .toObjectSet()
            )
            friendPlayerService.cachePlayer(updated)
        }
    }
}