package dev.slne.surf.friends.core.common.packets.friendrequest

import dev.slne.surf.friends.api.result.FriendRequestRemoveResult
import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class RevokeFriendRequestResponsePacket(
    val result: FriendRequestRemoveResult
) : RabbitResponsePacket()
