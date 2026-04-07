package dev.slne.surf.friends.core.common.packets.friendship

import dev.slne.surf.friends.api.model.Friendship
import dev.slne.surf.rabbitmq.api.packet.RabbitResponsePacket
import kotlinx.serialization.Serializable

@Serializable
data class FetchFriendshipsResponsePacket(
    val friendships: List<Friendship>
) : RabbitResponsePacket()
