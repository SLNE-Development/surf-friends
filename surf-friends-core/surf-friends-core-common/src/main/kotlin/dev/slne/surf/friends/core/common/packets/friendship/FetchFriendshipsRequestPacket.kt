package dev.slne.surf.friends.core.common.packets.friendship

import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
class FetchFriendshipsRequestPacket : RabbitRequestPacket<FetchFriendshipsResponsePacket>()
