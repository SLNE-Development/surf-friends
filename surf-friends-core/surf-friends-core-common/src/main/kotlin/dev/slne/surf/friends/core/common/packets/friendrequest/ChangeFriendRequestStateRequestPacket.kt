package dev.slne.surf.friends.core.common.packets.friendrequest

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.rabbitmq.api.packet.RabbitRequestPacket
import kotlinx.serialization.Serializable

@Serializable
data class ChangeFriendRequestStateRequestPacket(
    val senderUuid: SerializableUUID,
    val targetUuid: SerializableUUID,
    val state: State
) : RabbitRequestPacket<ChangeFriendRequestStateResponsePacket>() {
    @Serializable
    enum class State {
        ACCEPT,
        DECLINE
    }
}
