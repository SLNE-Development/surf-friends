package dev.slne.surf.friends.api.friend

import java.time.OffsetDateTime
import java.util.*

data class Friendship(
    val requestedBy: UUID,
    val acceptedBy: UUID,
    val createdAt: OffsetDateTime
)