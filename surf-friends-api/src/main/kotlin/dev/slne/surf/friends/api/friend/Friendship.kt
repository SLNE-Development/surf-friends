package dev.slne.surf.friends.api.friend

import dev.slne.surf.surfapi.core.api.serializer.java.datetime.datetime.offset.SerializableOffsetDateTime
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.util.*

fun friendship(
    requestedBy: UUID,
    acceptedBy: UUID,

    requesterName: String,
    acceptorName: String,
) = Friendship(
    requestedBy = requestedBy,
    acceptedBy = acceptedBy,
    requesterName = requesterName,
    acceptorName = acceptorName,
    createdAt = OffsetDateTime.now()
)

@Serializable
data class Friendship(
    val requestedBy: @Contextual UUID,
    val acceptedBy: @Contextual UUID,

    val requesterName: String,
    val acceptorName: String,

    val createdAt: SerializableOffsetDateTime
) {
    fun getOtherName(ownUuid: UUID) = if (requestedBy == ownUuid) acceptorName else requesterName
}