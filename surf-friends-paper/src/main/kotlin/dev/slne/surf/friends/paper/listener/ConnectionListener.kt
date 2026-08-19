package dev.slne.surf.friends.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.friends.core.client.connection.FriendConnectionNotifier
import dev.slne.surf.friends.paper.plugin
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ConnectionListener : Listener {
    @EventHandler
    fun onConnect(event: PlayerJoinEvent) {
        val uuid = event.player.uniqueId
        val onlineFriends = FriendConnectionNotifier.greet(uuid)

        plugin.launch {
            FriendConnectionNotifier.announceOnline(uuid, onlineFriends)
        }
    }

    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        val uuid = event.player.uniqueId

        plugin.launch {
            FriendConnectionNotifier.announceOffline(uuid)
        }
    }
}
