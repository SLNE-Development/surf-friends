package dev.slne.surf.friends.paper.listener

import dev.slne.surf.friends.core.service.friendPlayerService
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("UnstableApiUsage")
object PlayerConnectionListener : Listener {
    @EventHandler
    fun onJoin(event: AsyncPlayerConnectionConfigureEvent) {
        runBlocking {
            friendPlayerService.cachePlayer(friendPlayerService.loadOrCreatePlayer(event.connection.profile))
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        friendPlayerService.invalidatePlayer(event.player.uniqueId)
    }
}