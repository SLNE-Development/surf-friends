package dev.slne.surf.friends.microservice.rabbit.handler

import dev.slne.surf.friends.api.model.FriendRequest
import dev.slne.surf.friends.api.result.FriendRequestCreateResult
import dev.slne.surf.friends.api.result.FriendRequestRemoveResult
import dev.slne.surf.friends.api.result.FriendRequestStateResult
import dev.slne.surf.friends.core.common.packets.friendrequest.*
import dev.slne.surf.friends.microservice.db.repository.FriendRequestRepository
import dev.slne.surf.friends.microservice.db.repository.FriendshipsRepository
import dev.slne.surf.rabbitmq.api.handler.RabbitHandler
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

object FriendRequestHandler {
    @RabbitHandler
    fun handleFetchFriendRequestsPacket(
        request: FetchFriendRequestsRequestPacket
    ) = request.launch {
        request.respond(
            FetchFriendRequestsResponsePacket(FriendRequestRepository.fetchFriendRequests())
        )
    }

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

    @RabbitHandler
    fun handleRevokeFriendRequestPacket(
        request: RevokeFriendRequestRequestPacket
    ) = request.launch {
        FriendRequestRepository.deleteFriendRequest(request.senderUuid, request.receiverUuid)

        request.respond(
            RevokeFriendRequestResponsePacket(FriendRequestRemoveResult.Success)
        )
    }

    @RabbitHandler
    fun handleChangeFriendRequestStatePacket(
        request: ChangeFriendRequestStateRequestPacket
    ) = request.launch {
        FriendRequestRepository.deleteFriendRequest(request.senderUuid, request.targetUuid)

        if (request.state == ChangeFriendRequestStateRequestPacket.State.ACCEPT) {
            FriendshipsRepository.createFriendship(
                request.senderUuid,
                request.targetUuid,
                OffsetDateTime.now()
            )
        }

        request.respond(
            ChangeFriendRequestStateResponsePacket(FriendRequestStateResult.Success)
        )
    }
}