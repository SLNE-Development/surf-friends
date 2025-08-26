package dev.slne.surf.friends.api.model

import java.util.UUID

/**
 * Represents a friend request between two users.
 */
interface FriendRequest {
    /**
     * The UUID of the user who sent the friend request.
     */
    val senderUuid: UUID

    /**
     * The UUID of the user who received the friend request.
     */
    val receiverUuid: UUID

    /**
     * The timestamp (in milliseconds since epoch) when the friend request was sent.
     */
    val sentAt: Long
}