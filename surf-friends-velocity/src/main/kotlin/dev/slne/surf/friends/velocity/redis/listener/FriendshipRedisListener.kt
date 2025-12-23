package dev.slne.surf.friends.velocity.redis.listener

import dev.slne.redis.event.OnRedisEvent
import dev.slne.surf.friends.velocity.plugin
import dev.slne.surf.friends.velocity.redis.event.FriendRemoveRedisEvent
import dev.slne.surf.friends.velocity.redis.event.FriendRequestAcceptRedisEvent
import dev.slne.surf.friends.velocity.redis.event.FriendRequestDenyRedisEvent
import dev.slne.surf.friends.velocity.redis.event.FriendRequestRevokeRedisEvent
import dev.slne.surf.friends.velocity.util.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlin.jvm.optionals.getOrNull

object FriendshipRedisListener {
    @OnRedisEvent
    fun onFriendRemove(event: FriendRemoveRedisEvent) {
        val player = plugin.proxy.getPlayer(event.target).getOrNull() ?: return

        player.sendText {
            appendPrefix()
            info("Die Freundschaft mit ")
            variableValue(event.removerName)
            info(" wurde beendet.")
        }
    }
}