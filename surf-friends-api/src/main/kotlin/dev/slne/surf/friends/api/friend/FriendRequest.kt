package dev.slne.surf.friends.api.friend

import dev.slne.surf.surfapi.core.api.serializer.java.datetime.datetime.offset.SerializableOffsetDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class FriendRequest(
    val senderUuid: @Contextual UUID,
    val receiverUuid: @Contextual UUID,
    val sentAt: SerializableOffsetDateTime
)