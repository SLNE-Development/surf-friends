package dev.slne.surf.friends.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.core.api.common.event.SurfEventHandler
import dev.slne.surf.core.api.common.event.SurfPlayerConnectEvent
import dev.slne.surf.core.api.common.event.SurfPlayerDisconnectEvent
import dev.slne.surf.friends.core.service.friendPlayerService
import dev.slne.surf.friends.paper.hook.SettingsHook
import dev.slne.surf.friends.paper.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit

object SurfPlayerListener {
    @SurfEventHandler
    fun onNetworkConnect(event: SurfPlayerConnectEvent) {
        plugin.launch {
            val friendPlayer =
                friendPlayerService.findOrLoadPlayer(event.player.uuid) ?: return@launch

            friendPlayer.friends
                .mapNotNull { Bukkit.getPlayer(it.getOtherUuid(friendPlayer.uuid)) }
                .filter { SettingsHook.hasFriendNotifyEnabled(it.uniqueId) }
                .forEach {
                    it.sendText {
                        appendInfoPrefix()
                        info("Dein Freund ")
                        variableValue(friendPlayer.name)
                        info(" ist nun online.")
                    }
                }
        }
    }

    @SurfEventHandler
    fun onNetworkDisconnect(event: SurfPlayerDisconnectEvent) {
        plugin.launch {
            val friendPlayer =
                friendPlayerService.findOrLoadPlayer(event.player.uuid) ?: return@launch

            friendPlayer.friends
                .mapNotNull { Bukkit.getPlayer(it.getOtherUuid(friendPlayer.uuid)) }
                .filter { SettingsHook.hasFriendNotifyEnabled(it.uniqueId) }
                .forEach {
                    it.sendText {
                        appendInfoPrefix()
                        info("Dein Freund ")
                        variableValue(friendPlayer.name)
                        info(" ist nun offline.")
                    }
                }
        }
    }
}