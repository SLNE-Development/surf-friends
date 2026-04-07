package dev.slne.surf.friends.velocity.command.subcommand.friend

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.messages.pagination.Pagination
import dev.slne.surf.friends.api.model.Friendship
import dev.slne.surf.friends.core.service.friendService
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.util.*
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.clickRunsCommand
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import net.kyori.adventure.text.format.TextDecoration
import kotlin.jvm.optionals.getOrNull

private val pagination = Pagination<Friendship> {
    title {
        primary("Freundesliste")
    }

    rowRenderer { friendship, i ->
        listOf(buildText {
            append(CommonComponents.EM_DASH)
            appendSpace()
            appendAsync {
                append()
            }
            appendSpace()
            if (row.isOnline) {
                appendSpace()
                success("(Online auf ${row.onlineServer})")
            } else {
                appendSpace()
                error("(Offline)")
            }
            hoverEvent(buildText {
                info("Klicke hier, um ")
                variableValue(row.friendName)
                info(" hinterher zuspringen.")
            })
            clickRunsCommand("/friend jump ${row.friendName}")
        })
    }
}

class FriendListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(FriendPermissionRegistry.COMMAND_FRIEND_LIST)
        integerArgument("page", 1, optional = true)
        playerExecutor { player, args ->
            container.launch {
                val friendList = friendService.getFriendships(player.uniqueId)
                val page = args.getOrDefaultUnchecked("page", 1)

                if (friendList.isEmpty()) {
                    player.uniqueId.sendText {
                        error("Du hast keine Freunde.")
                    }
                    return@launch
                }

                val friendEntries = friendList.sortedByDescending {
                    it.friendUuid.isOnline()
                }.map {
                    async {
                        it to it.friendUuid.getUsernameAsync()
                    }
                }.awaitAll().map {
                    LocalFriendEntry(
                        it.second,
                        it.first.friendUuid.isOnline(),
                        it.first.friendUuid.toPlayer()?.currentServer?.getOrNull()?.serverInfo?.name
                            ?: "Unbekannt"
                    )
                }

                val pagination = Pagination<LocalFriendEntry> {
                    title { primary("Freundesliste".toSmallCaps(), TextDecoration.BOLD) }

                    rowRenderer { row, _ ->
                        listOf(
                            buildText {

                            }
                        )
                    }
                }


                player.sendText {
                    append(pagination.renderComponent(friendEntries, page))
                }
            }
        }
    }
}

private data class LocalFriendEntry(
    val friendName: String,
    val isOnline: Boolean,
    val onlineServer: String
)