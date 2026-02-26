package dev.slne.surf.friends.core.loader

import dev.slne.surf.redis.RedisApi

val redisLoader = RedisLoader()
val redisApi get() = redisLoader.redisApi

class RedisLoader {
    val redisApi = RedisApi.create()


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