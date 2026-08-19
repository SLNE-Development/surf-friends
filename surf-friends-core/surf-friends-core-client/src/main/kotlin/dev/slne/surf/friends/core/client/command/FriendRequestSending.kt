package dev.slne.surf.friends.core.client.command

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.clickRunsCommand
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import net.kyori.adventure.text.Component
import java.util.*

/**
 * Sends a friend request from [sender] to [target].
 *
 * Returns the message to show [sender] when no request was sent, and `null` once one was.
 */
suspend fun sendFriendRequest(sender: UUID, target: SurfPlayer?): Component? {
    if (target == null) {
        return buildText {
            appendErrorPrefix()
            error("Der angegebene Spieler wurde nicht gefunden.")
        }
    }

    if (sender == target.uuid) {
        return buildText {
            appendErrorPrefix()
            error("Du kannst dir keine Freundschaftsanfrage selbst senden.")
        }
    }

    val playerFriendsPlayer = FriendsPlayer[sender]
    val targetFriendsPlayer = FriendsPlayer[target.uuid]

    if (playerFriendsPlayer.hasFriendship(targetFriendsPlayer)) {
        return buildText {
            appendErrorPrefix()
            error("Du bist bereits mit ")
            append(target.displayName())
            error(" befreundet.")
        }
    }

    if (playerFriendsPlayer.hasSentFriendRequest(targetFriendsPlayer)) {
        return buildText {
            appendErrorPrefix()
            error("Du hast bereits eine Freundschaftsanfrage an ")
            append(target.displayName())
            error(" gesendet.")
        }
    }

    if (playerFriendsPlayer.hasReceivedFriendRequest(targetFriendsPlayer)) {
        return buildText {
            appendErrorPrefix()
            error("Du hast bereits eine Freundschaftsanfrage von ")
            append(target.displayName())
            error(" erhalten. Möchtest du diese annehmen?")
            append {
                clickRunsCommand("/friend accept ${target.username}")
                spacer(" [")
                info("Akzeptieren".toSmallCaps())
                spacer("]")
            }
        }
    }

    playerFriendsPlayer.sendFriendRequest(targetFriendsPlayer)

    return null
}
