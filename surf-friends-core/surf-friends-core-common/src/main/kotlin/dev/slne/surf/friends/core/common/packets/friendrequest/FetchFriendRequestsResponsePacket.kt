package dev.slne.surf.friends.core.common.packets.friendrequest

import dev.slne.surf.friends.api.model.FriendRequest
import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class FetchFriendRequestsResponsePacket(
    val friendRequests: List<FriendRequest>
) : RabbitResponsePacket()
