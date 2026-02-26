package dev.slne.surf.friends.core.loader

import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.redis.RedisApi
import dev.slne.surf.redis.sync.map.SyncMap
import java.util.*

val redisLoader = RedisLoader()
val redisApi get() = redisLoader.redisApi

class RedisLoader {
    val redisApi = RedisApi.create()
    val playerCache: SyncMap<UUID, FriendPlayer> =
        redisApi.createSyncMap("surf-friends:player-cache")


    fun load() {
    }

    fun withRequestResponseHandler(handler: Any) {
        redisApi.registerRequestHandler(handler)
    }

    fun withListener(listener: Any) {
        redisApi.subscribeToEvents(listener)
    }

    fun connect() {
        redisApi.freezeAndConnect()
    }

    fun disconnect() {
        redisApi.disconnect()
    }
}