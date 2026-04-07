package dev.slne.surf.friends.api.model

import dev.slne.surf.api.core.serializer.java.datetime.datetime.offset.SerializableOffsetDateTime
import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val senderUuid: SerializableUUID,
    val targetUuid: SerializableUUID,
    val createdAt: SerializableOffsetDateTime
)