package dev.slne.surf.friends.paper.redis.event

import dev.slne.surf.redis.event.RedisEvent
import dev.slne.surf.surfapi.core.api.serializer.adventure.component.SerializableComponent
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class FriendNotifyRedisEvent(
    val playerUuid: @Contextual UUID,
    val message: SerializableComponent
) : RedisEvent()
