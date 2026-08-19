package dev.slne.surf.friends.core.client.command

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.core.client.util.formatComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import java.util.*

/**
 * The details of the friendship between [player] and [target] as they are shown to [player].
 */
suspend fun friendInfoComponent(player: UUID, target: FriendsPlayer): Component {
    val friendShip = FriendsPlayer[player].findFriendship(target)
        ?: error("No friendship found between $player and ${target.uuid}")
    val targetSurfPlayer = target.surfPlayer()

    return buildText {
        appendNewline()
        info("Freundschaftsinformationen".toSmallCaps())
        appendNewline()

        append {
            info("| ")
            decorate(TextDecoration.BOLD)
        }
        variableKey("Freund: ".toSmallCaps())
        append(targetSurfPlayer.displayName())
        appendNewline()

        append {
            info("| ")
            decorate(TextDecoration.BOLD)
        }
        variableKey("Befreundet seit: ".toSmallCaps())
        append(friendShip.createdAt.formatComponent())
    }
}
