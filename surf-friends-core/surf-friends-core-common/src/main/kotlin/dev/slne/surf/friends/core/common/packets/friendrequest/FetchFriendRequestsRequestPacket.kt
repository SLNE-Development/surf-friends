package dev.slne.surf.friends.core.common.packets.friendrequest

import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
class FetchFriendRequestsRequestPacket : RabbitRequestPacket<FetchFriendRequestsResponsePacket>()
