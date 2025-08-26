package dev.slne.surf.friends.api.model

import java.util.UUID

/**
 * Represents a friendship between two users.
 */
interface Friendship {
    /**
     * The UUID of the user who is part of the friendship.
     */
    val userUuid: UUID

    /**
     * The UUID of the friend in the friendship.
     */
    val friendUuid: UUID

    /**
     * The timestamp (in milliseconds since epoch) when the friendship was created.
     */
    val createdAt: Long
}