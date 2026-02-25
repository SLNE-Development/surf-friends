package dev.slne.surf.friends.api.friend

import java.time.OffsetDateTime
import java.util.*

data class FriendRequest(
    val senderUuid: UUID,
    val receiverUuid: UUID,
    val sentAt: OffsetDateTime
)