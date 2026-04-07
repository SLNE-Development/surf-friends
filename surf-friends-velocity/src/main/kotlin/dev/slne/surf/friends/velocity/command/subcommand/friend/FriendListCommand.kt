package dev.slne.surf.friends.velocity.command.subcommand.friend

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.clickRunsCommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.messages.pagination.Pagination
import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.api.utils.toSurfPlayer
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration

private val pagination = Pagination<LocalFriendEntry> {
    title { primary("Freundesliste".toSmallCaps(), TextDecoration.BOLD) }

    rowRenderer { row, _ ->
        listOf(
            buildText {
                append(CommonComponents.EM_DASH)
                appendSpace()
                append(row.friendDisplayName)
                appendSpace()
                if (row.isOnline) {
                    success("(Online auf ${row.onlineServer})")
                } else {
                    error("(Offline)")
                }
                hoverEvent(buildText {
                    info("Klicke hier, um ")
                    variableValue(row.friendName)
                    info(" hinterher zuspringen.")
                })
                clickRunsCommand("/friend jump ${row.friendName}")
            }
        )
    }
}

class FriendListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(FriendPermissionRegistry.COMMAND_FRIEND_LIST)

        playerExecutor { player, _ ->
            val friendsPlayer = FriendsPlayer[player.uniqueId]
            val friendList = friendsPlayer.friendships

            if (friendList.isEmpty()) {
                player.sendText {
                    appendInfoPrefix()
                    info("Du hast keine Freunde.")
                }

                return@playerExecutor
            }

            container.launch {
                val friendEntries = friendList
                    .map {
                        it to it.targetUuid.toSurfPlayer()
                    }
                    .sortedByDescending { (_, surfPlayer) ->
                        surfPlayer?.isOnline() ?: false
                    }.mapNotNull { (friendship, surfPlayer) ->
                        val friendUuid = friendship.targetUuid

                        LocalFriendEntry(
                            friendName = PlayerLookupService.getUsername(friendUuid)
                                ?: return@mapNotNull null,
                            friendDisplayName = surfPlayer.displayName(),
                            isOnline = surfPlayer?.isOnline() ?: false,
                            onlineServer = surfPlayer?.currentServer?.displayName ?: "Unbekannt"
                        )
                    }

                player.sendText {
                    append(pagination.renderComponent(friendEntries))
                }
            }
        }
    }
}

private data class LocalFriendEntry(
    val friendName: String,
    val friendDisplayName: Component,
    val isOnline: Boolean,
    val onlineServer: String
)