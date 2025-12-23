package dev.slne.surf.friends.velocity.redis

import dev.slne.redis.RedisApi
import dev.slne.surf.friends.velocity.plugin
import dev.slne.surf.friends.velocity.redis.listener.FriendRequestRedisListener
import dev.slne.surf.friends.velocity.redis.listener.FriendshipRedisListener

val redisLoader = RedisLoader()

class RedisLoader {
    lateinit var redisApi: RedisApi
    fun connect() {
        redisApi = RedisApi.create(plugin.dataDirectory)

        registerListeners()
        redisApi.freezeAndConnect()
    }

    fun disconnect() {
        redisApi.disconnect()
    }

    private fun registerListeners() {
        redisApi.registerRequestHandler(FriendshipRedisListener)
        redisApi.registerRequestHandler(FriendRequestRedisListener)
    }
}