package dev.slne.surf.friends.paper.command.subcommand.friend

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.api.utils.friendPlayer
import dev.slne.surf.friends.paper.util.FriendPermissionRegistry
import dev.slne.surf.friends.paper.util.formatComponent
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.friendInfoCommand() = subcommand("info") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_INFO)

    surfOfflinePlayerArgument("target")

    playerExecutor { player, args ->
        val target: SurfPlayer by args

        val playerFriendsPlayer = FriendsPlayer[player.uniqueId]
        val targetFriendPlayer = target.friendPlayer()


        if (player.uniqueId == target.uuid) {
            player.sendText {
                appendErrorPrefix()

                error("Du kannst keine Freundschaftsinformationen über dich selber abrufen.")
            }

            return@playerExecutor
        }

        val friendShip = playerFriendsPlayer.findFriendship(targetFriendPlayer)

        if (friendShip == null) {
            player.sendText {
                appendErrorPrefix()

                error("Du bist nicht mit ")
                append(target.displayName())
                error(" befreundet.")
            }

            return@playerExecutor
        }

        player.sendText {
            info("Freundschaftsinformationen".toSmallCaps())
            appendNewline()

            append {
                info("| ")
                decorate(TextDecoration.BOLD)
            }
            variableKey("Freund: ".toSmallCaps())
            append(target.displayName())
            appendNewline()

            append {
                info("| ")
                decorate(TextDecoration.BOLD)
            }
            variableKey("Befreundet seit: ".toSmallCaps())
            append(friendShip.createdAt.formatComponent())
        }
    }
}

