package dev.slne.surf.friends.api.result

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import kotlinx.serialization.Serializable

@Serializable
sealed class FriendRequestStateResult {
    @Serializable
    object Success : FriendRequestStateResult()

    @Serializable
    data class NoFriendRequest(
        val targetUuid: SerializableUUID
    ) : FriendRequestStateResult()
}