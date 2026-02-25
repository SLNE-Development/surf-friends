package dev.slne.surf.friends.api.player

import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.friends.api.friend.Friendship
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

data class FriendPlayer(
    val uuid: UUID,
    val name: String,

    val friends: ObjectSet<Friendship>,
    val sentFriendRequests: ObjectSet<FriendRequest>,
    val receivedFriendRequests: ObjectSet<FriendRequest>
)
