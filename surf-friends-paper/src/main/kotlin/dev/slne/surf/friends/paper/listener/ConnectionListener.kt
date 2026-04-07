package dev.slne.surf.friends.paper.listener

import com.github.shynixn.mccoroutine.folia.launch
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.clickRunsCommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.paper.plugin
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ConnectionListener : Listener {
    @EventHandler
    fun onConnect(event: PlayerJoinEvent) {
        val player = event.player
        val friendPlayer = FriendsPlayer[player.uniqueId]

        val friendRequests = friendPlayer.receivedFriendRequests
        val onlineFriends = friendPlayer.onlineFriendUuids.mapNotNull {
            Bukkit.getPlayer(it)
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

            plugin.launch {
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

    @EventHandler
    fun onDisconnect(event: PlayerQuitEvent) {
        val player = event.player
        val friendsPlayer = FriendsPlayer[player.uniqueId]

        plugin.launch {
            val onlineFriends = friendsPlayer.onlineFriendUuids.mapNotNull {
                Bukkit.getPlayer(it)
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

