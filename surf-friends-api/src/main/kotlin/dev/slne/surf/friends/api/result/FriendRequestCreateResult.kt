package dev.slne.surf.friends.api.result

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.friends.api.model.FriendRequest
import kotlinx.serialization.Serializable

@Serializable
sealed class FriendRequestCreateResult {
    @Serializable
    data class Success(
        val friendRequest: FriendRequest
    ) : FriendRequestCreateResult()

    @Serializable
    data class AlreadySentFriendRequest(
        val targetUuid: SerializableUUID
    ) : FriendRequestCreateResult()

    @Serializable
    data class AlreadyFriends(
        val targetUuid: SerializableUUID
    ) : FriendRequestCreateResult()

    @Serializable
    data class AlreadyReceivedFriendRequest(
        val targetUuid: SerializableUUID
    ) : FriendRequestCreateResult()
}