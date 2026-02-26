package dev.slne.surf.friends.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.friends.backend.repository.friendRequestRepository
import dev.slne.surf.friends.core.service.FriendRequestService
import net.kyori.adventure.util.Services

@AutoService(FriendRequestService::class)
class FriendRequestServiceImpl : FriendRequestService, Services.Fallback {
    override suspend fun saveFriendRequest(friendRequest: FriendRequest) {
        friendRequestRepository.saveRequest(friendRequest)
    }

    override suspend fun deleteFriendRequest(friendRequest: FriendRequest) {
        friendRequestRepository.deleteRequest(friendRequest)
    }
}