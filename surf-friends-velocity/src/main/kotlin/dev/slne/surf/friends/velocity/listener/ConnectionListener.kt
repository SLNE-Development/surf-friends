package dev.slne.surf.friends.velocity.listener

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import dev.slne.surf.friends.core.service.databaseService
import dev.slne.surf.friends.core.service.friendService
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.util.getOnlineFriends
import dev.slne.surf.friends.velocity.util.sendText
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickRunsCommand

class ConnectionListener {
    @Subscribe
    fun onConnect(event: PlayerChooseInitialServerEvent) {
        val player = event.player

        container.launch {
            val friendRequests = friendService.getReceivedFriendRequests(player.uniqueId)
            val onlineFriends = friendService.getOnlineFriends(player.uniqueId)

            if (onlineFriends.isNotEmpty()) {
                player.uniqueId.sendText {
                    info("Aktuell sind ")
                    variableValue(onlineFriends.size)
                    info(" deiner Freunde online. ")

                    append {
                        clickRunsCommand("/friend list")
                        info("[Ansehen]".toSmallCaps())
                        hoverEvent(buildText {
                            info("Klicke hier, um die Freunde anzusehen.")
                        })
                    }
                }

                onlineFriends.forEach {
                    val playerSettings = databaseService.getFriendSettings(it)

                    if (playerSettings.announcementsEnabled) {
                        it.sendText {
                            variableValue(player.username)
                            info(" ist nun online.")
                        }
                    }
                }
            }

            if (friendRequests.isNotEmpty()) {
                player.uniqueId.sendText {
                    info("Du hast noch ")
                    variableValue(friendRequests.size)
                    info(" Freundschaftsanfragen offen. ")

                    append {
                        clickRunsCommand("/friend requests")
                        info("[Ansehen]".toSmallCaps())
                        hoverEvent(buildText {
                            info("Klicke hier, um die Freundschaftsanfragen anzusehen.")
                        })
                    }
                }
            }
        }
    }

    @Subscribe
    fun onDisconnect(event: DisconnectEvent) {
        if (event.loginStatus != DisconnectEvent.LoginStatus.SUCCESSFUL_LOGIN) {
            return
        }

        val player = event.player

        container.launch {
            val onlineFriends = friendService.getOnlineFriends(player.uniqueId)

            onlineFriends.forEach {
                val playerSettings = databaseService.getFriendSettings(it)

                if (playerSettings.announcementsEnabled) {
                    it.sendText {
                        variableValue(player.username)
                        info(" ist nun offline.")
                    }
                }
            }
        }
    }
}