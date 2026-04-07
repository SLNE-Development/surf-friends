package dev.slne.surf.friends.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.friends.backend.repository.friendRequestRepository
import dev.slne.surf.friends.core.service.FriendRequestService
import dev.slne.surf.friends.core.service.friendPlayerService
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import net.kyori.adventure.util.Services

@AutoService(FriendRequestService::class)
class FriendRequestServiceImpl : FriendRequestService, Services.Fallback {
    override suspend fun saveFriendRequest(friendRequest: FriendRequest) {
        friendRequestRepository.saveRequest(friendRequest)

        val sender = friendPlayerService.players
            .firstOrNull { it.uuid == friendRequest.senderUuid }

        if (sender != null) {
            val updatedSender = sender.copy(
                sentFriendRequests = (sender.sentFriendRequests + friendRequest).toObjectSet()
            )
            friendPlayerService.cachePlayer(updatedSender)
        }

        val receiver = friendPlayerService.players
            .firstOrNull { it.uuid == friendRequest.receiverUuid }

        if (receiver != null) {
            val updatedReceiver = receiver.copy(
                receivedFriendRequests = (receiver.receivedFriendRequests + friendRequest).toObjectSet()
            )
            friendPlayerService.cachePlayer(updatedReceiver)
        }
    }

    override suspend fun deleteFriendRequest(friendRequest: FriendRequest) {
        friendRequestRepository.deleteRequest(friendRequest)

        val sender = friendPlayerService.players
            .firstOrNull { it.uuid == friendRequest.senderUuid }

        if (sender != null) {
            val updatedSender = sender.copy(
                sentFriendRequests = sender.sentFriendRequests
                    .filterNot { it == friendRequest }
                    .toObjectSet()
            )
            friendPlayerService.cachePlayer(updatedSender)
        }

        val receiver = friendPlayerService.players
            .firstOrNull { it.uuid == friendRequest.receiverUuid }

        if (receiver != null) {
            val updatedReceiver = receiver.copy(
                receivedFriendRequests = receiver.receivedFriendRequests
                    .filterNot { it == friendRequest }
                    .toObjectSet()
            )
            friendPlayerService.cachePlayer(updatedReceiver)
        }
    }
}