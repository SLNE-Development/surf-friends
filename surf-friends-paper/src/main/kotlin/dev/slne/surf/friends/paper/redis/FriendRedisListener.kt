package dev.slne.surf.friends.paper.redis

import dev.slne.surf.friends.paper.hook.SettingsHook
import dev.slne.surf.friends.paper.redis.event.FriendNotifyRedisEvent
import dev.slne.surf.friends.paper.redis.event.FriendRequestNotifyRedisEvent
import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

object FriendRedisListener {
    @OnRedisEvent
    fun onFriendNotifyMessage(event: FriendNotifyRedisEvent) {
        val player = Bukkit.getPlayer(event.playerUuid) ?: return

        if (!SettingsHook.hasFriendNotifyEnabled(player.uniqueId)) {
            return
        }

        player.sendText {
            append(event.message)
        }
    }

    @OnRedisEvent
    fun onFriendRequestNotify(event: FriendRequestNotifyRedisEvent) {
        val player = Bukkit.getPlayer(event.playerUuid) ?: return

        if (!SettingsHook.hasFriendRequestsEnabled(player.uniqueId)) {
            return
        }

        player.sendText {
            append(event.message)
        }
    }
}