package dev.slne.surf.friends.paper.command.subcommand.friend

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.command.args.awaiting
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.paper.command.argument.friend.friendArgument
import dev.slne.surf.friends.paper.plugin
import dev.slne.surf.friends.paper.util.FriendPermissionRegistry
import dev.slne.surf.friends.paper.util.formatComponent
import net.kyori.adventure.text.format.TextDecoration

fun CommandAPICommand.friendInfoCommand() = subcommand("info") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_INFO)

    friendArgument("target")

    playerExecutor { player, args ->
        plugin.launch {
            val target = args.awaiting<FriendsPlayer>("target")
            val playerFriendsPlayer = FriendsPlayer[player.uniqueId]
            val friendShip = playerFriendsPlayer.findFriendship(target)!!
            val targetSurfPlayer = target.surfPlayer()

            player.sendText {
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
    }
}
