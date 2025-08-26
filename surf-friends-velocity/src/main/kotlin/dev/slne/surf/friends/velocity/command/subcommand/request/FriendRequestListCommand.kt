package dev.slne.surf.friends.velocity.command.subcommand.request

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.friends.core.service.friendService
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry
import dev.slne.surf.friends.velocity.util.PageableMessageBuilder
import dev.slne.surf.friends.velocity.util.format
import dev.slne.surf.friends.velocity.util.getUsernameAsync
import dev.slne.surf.friends.velocity.util.sendText
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickRunsCommand
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import net.kyori.adventure.text.format.TextDecoration
import kotlin.to

class FriendRequestListCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission(FriendPermissionRegistry.COMMAND_FRIEND_REQUEST_LIST)
        integerArgument("page", 1, optional = true)
        playerExecutor { player, args ->
            container.launch {
                val friendRequests = friendService.getReceivedFriendRequests(player.uniqueId).sortedByDescending { it.sentAt }
                val page = args.getOrDefaultUnchecked("page", 1)

                if(friendRequests.isEmpty()) {
                    player.uniqueId.sendText {
                        error("Du hast keine Freundschaftsanfragen offen.")
                    }
                    return@launch
                }

                val senderNames = friendRequests.map {
                    async {
                        it to it.senderUuid.getUsernameAsync()
                    }
                }.awaitAll()

                PageableMessageBuilder {
                    pageCommand = "/friend requests %page%"

                    title {
                        info("Offene Freundschaftsanfragen".toSmallCaps())
                    }

                    senderNames.forEach {
                        line {
                            append {
                                info("| ")
                                decorate(TextDecoration.BOLD)
                            }
                            variableValue(it.second)
                            spacer(" (${it.first.sentAt.format()})")
                            hoverEvent(buildText {
                                info("Klicke hier, um die Freundschaftsanfrage von ")
                                variableValue(it.second)
                                info(" anzunehmen.")
                            })
                            clickRunsCommand("/friend accept ${it.second}")
                        }
                    }
                }.send(player, page)
            }
        }
    }
}