package dev.slne.surf.friends.core.common.packets.friendship

import dev.slne.surf.friends.api.result.FriendshipRemoveResult
import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class RemoveFriendshipResponsePacket(
    val result: FriendshipRemoveResult
) : RabbitResponsePacket()
