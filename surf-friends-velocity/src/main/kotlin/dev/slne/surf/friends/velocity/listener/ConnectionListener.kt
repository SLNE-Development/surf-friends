package dev.slne.surf.friends.velocity.listener

import com.github.shynixn.mccoroutine.velocity.launch
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.player.PlayerChooseInitialServerEvent
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.clickRunsCommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.server
import kotlin.jvm.optionals.getOrNull

class ConnectionListener {
    @Subscribe
    fun onConnect(event: PlayerChooseInitialServerEvent) {
        val player = event.player
        val friendPlayer = FriendsPlayer[player.uniqueId]

        val friendRequests = friendPlayer.receivedFriendRequests
        val onlineFriends = friendPlayer.onlineFriendUuids.mapNotNull {
            server.getPlayer(it).getOrNull()
        }

        if (onlineFriends.isNotEmpty()) {
            player.sendText {
                appendInfoPrefix()
                info("Aktuell sind ")
                variableValue(onlineFriends.size)
                info(" deiner Freunde online. ")

                append {
                    clickRunsCommand("/friend list")
                    info("[Ansehen]".toSmallCaps())
                    hoverEvent(buildText {
                        info("Klicke hier, um deine Freunde anzusehen.")
                    })
                }
            }

            container.launch {
                onlineFriends.forEach { onlineFriend ->
                    val onlineFriendPlayer = FriendsPlayer[onlineFriend.uniqueId]

                    if (onlineFriendPlayer.notificationsEnabled) {
                        onlineFriend.sendText {
                            appendInfoPrefix()
                            append(friendPlayer.surfPlayer().displayName())
                            info(" ist nun online.")
                        }
                    }
                }
            }
        }

        if (friendRequests.isNotEmpty()) {
            player.sendText {
                appendInfoPrefix()
                info("Du hast noch ")
                variableValue(friendRequests.size)
                info(" Freundschaftsanfragen offen. ")

                append {
                    clickRunsCommand("/friend requests")
                    info("[Ansehen]".toSmallCaps())
                    hoverEvent(buildText {
                        info("Klicke hier, um deine Freundschaftsanfragen anzusehen.")
                    })
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
        val friendsPlayer = FriendsPlayer[player.uniqueId]

        container.launch {
            val onlineFriends = friendsPlayer.onlineFriendUuids.mapNotNull {
                server.getPlayer(it).getOrNull()
            }

            onlineFriends.forEach { onlineFriend ->
                val onlineFriendPlayer = FriendsPlayer[onlineFriend.uniqueId]

                if (onlineFriendPlayer.notificationsEnabled) {
                    onlineFriend.sendText {
                        appendInfoPrefix()
                        append(friendsPlayer.surfPlayer().displayName())
                        info(" ist nun offline.")
                    }
                }
            }
        }
    }
}