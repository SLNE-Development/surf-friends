package dev.slne.surf.friends.core.model

import dev.slne.surf.friends.api.model.FriendRequest
import java.util.UUID

class CoreFriendRequest(
    override val senderUuid: UUID,
    override val receiverUuid: UUID,
    override val sentAt: Long
) : FriendRequest {
}