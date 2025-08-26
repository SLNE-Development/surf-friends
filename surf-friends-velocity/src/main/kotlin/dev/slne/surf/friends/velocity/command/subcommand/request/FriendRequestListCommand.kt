package dev.slne.surf.friends.velocity.command.subcommand.request

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.integerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.friends.core.service.friendService
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry
import dev.slne.surf.friends.velocity.util.format
import dev.slne.surf.friends.velocity.util.getUsernameAsync
import dev.slne.surf.friends.velocity.util.sendText
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.CommonComponents
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickRunsCommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.friendRequestListCommand() = subcommand("requests") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_REQUEST_LIST)
    integerArgument("page", 1, optional = true)
    playerExecutor { player, args ->
        container.launch {
            val friendRequests = friendService.getReceivedFriendRequests(player.uniqueId)
                .sortedByDescending { it.sentAt }
            val page = args.getOrDefaultUnchecked("page", 1)

            if (friendRequests.isEmpty()) {
                player.uniqueId.sendText {
                    error("Du hast keine Freundschaftsanfragen offen.")
                }
                return@launch
            }

            val requesterEntries = friendRequests.map {
                async {
                    it to it.senderUuid.getUsernameAsync()
                }
            }.awaitAll().map {
                LocalFriendRequest(
                    it.second,
                    it.first.sentAt
                )
            }

            val pagination = Pagination<LocalFriendRequest> {
                title { primary("Offene Freundschaftsanfragen".toSmallCaps(), TextDecoration.BOLD) }

                rowRenderer { row, _ ->
                    listOf(
                        buildText {
                            append(CommonComponents.EM_DASH)
                            appendSpace()
                            variableKey(row.requesterName)
                            spacer(" (${row.sentAt.format()})")
                            hoverEvent(buildText {
                                info("Klicke hier, um die Freundschaftsanfrage von ")
                                variableValue(row.requesterName)
                                info(" anzunehmen.")
                            })
                            clickRunsCommand("/friend accept ${row.requesterName}")
                        }
                    )
                }
            }

            player.sendText {
                append(pagination.renderComponent(requesterEntries, page))
            }
        }
    }
}

private data class LocalFriendRequest(
    val requesterName: String,
    val sentAt: Long
)