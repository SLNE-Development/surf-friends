package dev.slne.surf.friends.velocity.redis.event

import dev.slne.redis.event.RedisEvent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class FriendRequestRevokeRedisEvent(
    val requester: @Contextual UUID,
    val requesterName: String,
    val target: @Contextual UUID
) : RedisEvent()
