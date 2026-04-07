package dev.slne.surf.friends.core.client.redis.event

import dev.slne.surf.api.core.serializer.java.uuid.SerializableUUID
import dev.slne.surf.redis.event.RedisEvent
import kotlinx.serialization.Serializable

@Serializable
data class FriendRequestDenyRedisEvent(
    val executorUuid: SerializableUUID,
    val targetUuid: SerializableUUID,
) : RedisEvent()
