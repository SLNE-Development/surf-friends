package dev.slne.surf.friends.velocity.command.subcommand.request

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.clickRunsCommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.messages.pagination.Pagination
import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry
import dev.slne.surf.friends.velocity.util.format
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import java.time.OffsetDateTime

private val pagination = Pagination<LocalFriendRequest> {
    title { primary("Offene Freundschaftsanfragen".toSmallCaps(), TextDecoration.BOLD) }

    rowRenderer { row, _ ->
        listOf(
            buildText {
                append(CommonComponents.EM_DASH)
                appendSpace()
                variableKey(row.requesterName)
                spacer(" (${row.createdAt.format()})")
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

fun CommandAPICommand.friendRequestListCommand() = subcommand("requests") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_REQUEST_LIST)

    playerExecutor { player, args ->
        val friendsPlayer = FriendsPlayer[player.uniqueId]
        val friendRequests = friendsPlayer.receivedFriendRequests
            .sortedByDescending { it.createdAt }

        if (friendRequests.isEmpty()) {
            player.sendText {
                appendInfoPrefix()
                info("Du hast keine offenen Freundschaftsanfragen.")
            }

            return@playerExecutor
        }

        container.launch {
            val requesterEntries = friendRequests.mapNotNull {
                LocalFriendRequest(
                    requesterName = PlayerLookupService.getUsername(it.senderUuid)
                        ?: return@mapNotNull null,
                    requesterDisplayName = it.sender.surfPlayer().displayName(),
                    createdAt = it.createdAt
                )
            }

            player.sendText {
                append(pagination.renderComponent(requesterEntries))
            }
        }
    }
}

private data class LocalFriendRequest(
    val requesterName: String,
    val requesterDisplayName: Component,
    val createdAt: OffsetDateTime
)