package dev.slne.surf.friends.core.client.command

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.pagination.Pagination
import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.core.client.util.format
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import java.time.OffsetDateTime
import java.util.*

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

private data class LocalFriendRequest(
    val requesterName: String,
    val createdAt: OffsetDateTime
)

/**
 * The open friend requests of [player] as they are shown to them.
 */
suspend fun friendRequestListComponent(player: UUID): Component {
    val friendRequests = FriendsPlayer[player].receivedFriendRequests
        .sortedByDescending { it.createdAt }

    if (friendRequests.isEmpty()) {
        return buildText {
            appendInfoPrefix()
            info("Du hast keine offenen Freundschaftsanfragen.")
        }
    }

    val requesterEntries = friendRequests.mapNotNull {
        LocalFriendRequest(
            requesterName = PlayerLookupService.getUsername(it.senderUuid)
                ?: return@mapNotNull null,
            createdAt = it.createdAt
        )
    }

    return buildText {
        appendNewline()
        append(pagination.renderComponent(requesterEntries))
    }
}
