package dev.slne.surf.friends.velocity.redis.event

import dev.slne.redis.event.RedisEvent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class FriendRequestAcceptRedisEvent(
    val requester: @Contextual UUID,
    val target: @Contextual UUID,
    val targetName: String
) : RedisEvent()
