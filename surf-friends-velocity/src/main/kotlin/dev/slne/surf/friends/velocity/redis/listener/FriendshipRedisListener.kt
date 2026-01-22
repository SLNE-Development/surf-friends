package dev.slne.surf.friends.velocity.redis.listener

import dev.slne.surf.friends.velocity.plugin
import dev.slne.surf.friends.velocity.redis.event.FriendRemoveRedisEvent
import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlin.jvm.optionals.getOrNull

object FriendshipRedisListener {
    @OnRedisEvent
    fun onFriendRemove(event: FriendRemoveRedisEvent) {
        val player = plugin.proxy.getPlayer(event.target).getOrNull() ?: return

        player.sendText {
            appendInfoPrefix()
            info("Die Freundschaft mit ")
            variableValue(event.removerName)
            info(" wurde beendet.")
        }
    }
}