package dev.slne.surf.friends.api.result

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
sealed class FriendshipRemoveResult {
    @Serializable
    object Success : FriendshipRemoveResult()

    @Serializable
    data class NotFriends(
        val targetUuid: SerializableUUID
    ) : FriendshipRemoveResult()

}