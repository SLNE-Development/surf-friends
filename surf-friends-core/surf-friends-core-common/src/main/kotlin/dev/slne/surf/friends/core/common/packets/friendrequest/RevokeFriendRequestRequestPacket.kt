package dev.slne.surf.friends.core.common.packets.friendrequest

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
data class RevokeFriendRequestRequestPacket(
    val senderUuid: SerializableUUID,
    val receiverUuid: SerializableUUID,
) : RabbitRequestPacket<RevokeFriendRequestResponsePacket>()
