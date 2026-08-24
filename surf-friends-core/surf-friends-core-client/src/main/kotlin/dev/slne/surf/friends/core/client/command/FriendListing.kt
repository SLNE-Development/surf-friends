package dev.slne.surf.friends.core.client.command

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.CommonComponents
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.pagination.Pagination
import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import java.util.*

private const val UNKNOWN_SERVER = "Unbekannt"

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

    val online = ObjectArrayList<LocalFriendEntry>()
    val offline = ObjectArrayList<LocalFriendEntry>()

    for ((_, friendUuid) in friendList) {
        val onlinePlayer = SurfCoreApi.getPlayer(friendUuid)
        val surfPlayer = onlinePlayer ?: SurfCoreApi.getOfflinePlayer(friendUuid)
        val friendName = PlayerLookupService.getUsername(friendUuid) ?: continue

        if (onlinePlayer != null) {
            online.add(
                LocalFriendEntry(
                    friendName = friendName,
                    friendDisplayName = surfPlayer.displayName(),
                    isOnline = true,
                    onlineServer = onlinePlayer.currentServer?.displayName ?: UNKNOWN_SERVER
                )
            )
        } else {
            offline.add(
                LocalFriendEntry(
                    friendName = friendName,
                    friendDisplayName = surfPlayer.displayName(),
                    isOnline = false,
                    onlineServer = UNKNOWN_SERVER
                )
            )
        }
    }

    online.addAll(offline)

    return buildText {
        appendNewline()
        append(pagination.renderComponent(online))
    }
}
