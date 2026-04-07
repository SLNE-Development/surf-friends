package dev.slne.surf.friends.api.result

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
sealed class FriendRequestRemoveResult {
    object Success : FriendRequestRemoveResult()

    @Serializable
    data class NotSentFriendRequest(
        val targetUuid: SerializableUUID
    ) : FriendRequestRemoveResult()
}