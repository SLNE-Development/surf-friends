package dev.slne.surf.friends.microservice.rabbit.handler

import dev.slne.surf.friends.api.model.FriendRequest
import dev.slne.surf.friends.api.result.FriendRequestCreateResult
import dev.slne.surf.friends.core.common.packets.friendrequest.CreateFriendRequestRequestPacket
import dev.slne.surf.friends.core.common.packets.friendrequest.CreateFriendRequestResponsePacket
import dev.slne.surf.friends.microservice.db.repository.FriendRequestRepository
import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

object FriendRequestHandler {
    @RabbitHandler
    fun handleCreateFriendRequestPacket(
        request: CreateFriendRequestRequestPacket
    ) = request.launch {
        val senderUuid = request.senderUuid
        val targetUuid = request.targetUuid
        val createdAt = OffsetDateTime.now()

        val friendRequest = FriendRequest(
            senderUuid = senderUuid,
            targetUuid = targetUuid,
            createdAt = createdAt,
        )

        FriendRequestRepository.createFriendRequest(friendRequest)

        request.respond(
            CreateFriendRequestResponsePacket(
                result = FriendRequestCreateResult.Success(friendRequest)
            )
        )
    }
}