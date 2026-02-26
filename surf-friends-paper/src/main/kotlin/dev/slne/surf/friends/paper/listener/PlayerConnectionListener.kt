package dev.slne.surf.friends.paper.listener

import dev.slne.surf.friends.core.service.friendPlayerService
import dev.slne.surf.friends.paper.util.friendPlayer
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import io.papermc.paper.event.connection.configuration.AsyncPlayerConnectionConfigureEvent
import kotlinx.coroutines.runBlocking
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("UnstableApiUsage")
object PlayerConnectionListener : Listener {
    @EventHandler
    fun onConfigure(event: AsyncPlayerConnectionConfigureEvent) {
        runBlocking {
            friendPlayerService.cachePlayer(friendPlayerService.loadOrCreatePlayer(event.connection.profile))
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val friendPlayer = event.player.friendPlayer

        if (friendPlayer.receivedFriendRequests.isNotEmpty()) {
            event.player.sendText {
                appendInfoPrefix()
                info("Du hast noch ")
                variableValue(friendPlayer.receivedFriendRequests.size)
                info(" offene Freundschaftsanfragen.")
            } // TODO: Only sent if enabled in player settings
        }

        if (friendPlayer.getOnlineFriendCount() > 0) {
            event.player.sendText {
                appendInfoPrefix()
                info("Derzeit sind ")
                variableValue(friendPlayer.getOnlineFriendCount())
                info(" Freunde online.")
            }
        } // TODO: Only sent if enabled in player settings
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        friendPlayerService.invalidatePlayer(event.player.uniqueId)
    }
}