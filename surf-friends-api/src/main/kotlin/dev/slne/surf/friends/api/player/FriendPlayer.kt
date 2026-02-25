package dev.slne.surf.friends.api.player

import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.friends.api.friend.Friendship
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class FriendPlayer(
    val uuid: @Contextual UUID,
    val name: String,

    val friends: ObjectSet<Friendship>,
    val sentFriendRequests: ObjectSet<FriendRequest>,
    val receivedFriendRequests: ObjectSet<FriendRequest>
)
