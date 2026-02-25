package dev.slne.surf.friends.api.friend

import java.time.OffsetDateTime
import java.util.*

data class Friendship(
    val userUuid: UUID,
    val friendUuid: UUID,
    val createdAt: OffsetDateTime
)