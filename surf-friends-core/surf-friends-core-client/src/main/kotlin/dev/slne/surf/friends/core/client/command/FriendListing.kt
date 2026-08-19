package dev.slne.surf.friends.core.client.command

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.pagination.Pagination
import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.api.utils.toSurfPlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import java.util.*

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
            }
        )
    }
}

private data class LocalFriendEntry(
    val friendName: String,
    val friendDisplayName: Component,
    val isOnline: Boolean,
    val onlineServer: String
)

/**
 * The friend list of [player] as it is shown to them.
 */
suspend fun friendListComponent(player: UUID): Component {
    val friendList = FriendsPlayer[player].friendships

    if (friendList.isEmpty()) {
        return buildText {
            appendInfoPrefix()
            info("Du hast keine Freunde.")
        }
    }

    val friendEntries = friendList
        .map {
            it to it.friendUuid.toSurfPlayer()
        }
        .sortedByDescending { (_, surfPlayer) ->
            surfPlayer?.isOnline() ?: false
        }.mapNotNull { (friendship, surfPlayer) ->
            LocalFriendEntry(
                friendName = PlayerLookupService.getUsername(friendship.friendUuid)
                    ?: return@mapNotNull null,
                friendDisplayName = surfPlayer.displayName(),
                isOnline = surfPlayer?.isOnline() ?: false,
                onlineServer = surfPlayer?.currentServer?.displayName ?: "Unbekannt"
            )
        }

    return buildText {
        appendNewline()
        append(pagination.renderComponent(friendEntries))
    }
}
