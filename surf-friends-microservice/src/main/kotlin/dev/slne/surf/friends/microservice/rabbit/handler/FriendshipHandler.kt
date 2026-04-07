package dev.slne.surf.friends.microservice.rabbit.handler

import dev.slne.surf.friends.api.result.FriendshipRemoveResult
import dev.slne.surf.friends.core.common.packets.friendship.FetchFriendshipsRequestPacket
import dev.slne.surf.friends.core.common.packets.friendship.FetchFriendshipsResponsePacket
import dev.slne.surf.friends.core.common.packets.friendship.RemoveFriendshipRequestPacket
import dev.slne.surf.friends.core.common.packets.friendship.RemoveFriendshipResponsePacket
import dev.slne.surf.friends.microservice.db.repository.FriendshipsRepository
import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import kotlinx.coroutines.launch

object FriendshipHandler {
    @RabbitHandler
    fun handleFetchFriendshipsRequest(
        request: FetchFriendshipsRequestPacket
    ) = request.launch {
        request.respond(FetchFriendshipsResponsePacket(FriendshipsRepository.fetchFriendships()))
    }

    @RabbitHandler
    fun handleRemoveFriendshipRequest(
        request: RemoveFriendshipRequestPacket
    ) = request.launch {
        FriendshipsRepository.deleteFriendship(request.senderUuid, request.targetUuid)

        request.respond(RemoveFriendshipResponsePacket(FriendshipRemoveResult.Success))
    }
}