package dev.slne.surf.friends.velocity.redis.event

import dev.slne.redis.event.RedisEvent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class FriendRequestSendRedisEvent(
    val requester: @Contextual UUID,
    val requesterName: String,
    val target: @Contextual UUID,
    val announce: Boolean
) : RedisEvent()
