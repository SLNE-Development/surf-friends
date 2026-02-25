package dev.slne.surf.friends.api.friend

import dev.slne.surf.surfapi.core.api.serializer.java.datetime.datetime.offset.SerializableOffsetDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Friendship(
    val requestedBy: @Contextual UUID,
    val acceptedBy: @Contextual UUID,
    val createdAt: SerializableOffsetDateTime
)