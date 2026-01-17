package dev.slne.surf.friends.velocity.redis.event

import dev.slne.surf.redis.event.RedisEvent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class FriendRequestRevokeRedisEvent(
    val requester: @Contextual UUID,
    val requesterName: String,
    val target: @Contextual UUID
) : RedisEvent()
