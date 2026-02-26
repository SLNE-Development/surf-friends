package dev.slne.surf.friends.api.friend

import dev.slne.surf.surfapi.core.api.serializer.java.datetime.datetime.offset.SerializableOffsetDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.*


fun friendRequest(
    senderUuid: UUID,
    receiverUuid: UUID,
    senderName: String,
    receiverName: String
) = FriendRequest(senderUuid, receiverUuid, senderName, receiverName, OffsetDateTime.now())

@Serializable
data class FriendRequest(
    val senderUuid: @Contextual UUID,
    val receiverUuid: @Contextual UUID,

    val senderName: String,
    val receiverName: String,

    val sentAt: SerializableOffsetDateTime
)