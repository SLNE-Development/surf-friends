package dev.slne.surf.friends.velocity.command.subcommand.friend

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.friends.core.service.friendService
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry
import dev.slne.surf.friends.velocity.util.PageableMessageBuilder
import dev.slne.surf.friends.velocity.util.getUsernameAsync
import dev.slne.surf.friends.velocity.util.isOnline
import dev.slne.surf.friends.velocity.util.sendText
import dev.slne.surf.friends.velocity.util.toPlayer
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickRunsCommand
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import net.kyori.adventure.text.format.TextDecoration
import kotlin.jvm.optionals.getOrNull
import kotlin.to

class FriendListCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission(FriendPermissionRegistry.COMMAND_FRIEND_LIST)
        integerArgument("page", 1, optional = true)
        playerExecutor { player, args ->
            container.launch {
                val friendList = friendService.getFriendships(player.uniqueId)
                val page = args.getOrDefaultUnchecked("page", 1)

                if(friendList.isEmpty()) {
                    player.uniqueId.sendText {
                        error("Du hast keine Freunde.")
                    }
                    return@launch
                }

                val friendNames = friendList.sortedByDescending {
                    it.friendUuid.isOnline()
                }.map {
                    async {
                        it to it.friendUuid.getUsernameAsync()
                    }
                }.awaitAll()

                PageableMessageBuilder {
                    pageCommand = "/friend list %page%"

                    title {
                        info("Freundesliste".toSmallCaps())
                    }

                    friendNames.forEach {
                        line {
                            append {
                                info("| ")
                                decorate(TextDecoration.BOLD)
                            }
                            variableValue(it.second)
                            if(it.first.friendUuid.isOnline()) {
                                spacer(" (")
                                success("Online")
                                spacer(" auf ")
                                variableValue(it.first.friendUuid.toPlayer()?.currentServer?.getOrNull()?.serverInfo?.name ?: "Unbekannt")
                                spacer(")")
                            } else {
                                spacer(" (")
                                error("Offline")
                                spacer(")")
                            }

                            hoverEvent(buildText {
                                info("Klicke hier, um ")
                                variableValue(it.second)
                                info(" hinterher zuspringen.")
                            })
                            clickRunsCommand("/friend jump ${it.second}")
                        }
                    }
                }.send(player, page)
            }
        }
    }
}