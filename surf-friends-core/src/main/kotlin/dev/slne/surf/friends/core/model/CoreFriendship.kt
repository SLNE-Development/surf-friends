package dev.slne.surf.friends.core.model

import dev.slne.surf.friends.api.model.Friendship
import java.util.UUID

class CoreFriendship(
    override val userUuid: UUID,
    override val friendUuid: UUID,
    override val createdAt: Long
) : Friendship {
}