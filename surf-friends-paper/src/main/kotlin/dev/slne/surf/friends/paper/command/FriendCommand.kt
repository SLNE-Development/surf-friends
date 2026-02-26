package dev.slne.surf.friends.paper.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.friends.paper.command.argument.friend.offlineFriendArgument
import dev.slne.surf.friends.paper.permission.PermissionRegistry
import dev.slne.surf.friends.paper.util.friendPlayer
import dev.slne.surf.surfapi.bukkit.api.command.executors.playerExecutorSuspend
import dev.slne.surf.surfapi.core.api.command.args.awaiting
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun friendCommand() = commandTree("friend") {
    withPermission(PermissionRegistry.PREFIX_COMMAND)

    literalArgument("add") {
        surfOfflinePlayerArgument("target") { // TODO: Exclude self and online friends
            playerExecutorSuspend { player, args ->
                val target = args.awaiting<SurfPlayer?>("target")

                if (target == null) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Der Spieler wurde nicht gefunden.")
                    }
                    return@playerExecutorSuspend
                }

                val friendPlayer = player.friendPlayer

                if(friendPlayer.friends)
            }
        }
    }

    literalArgument("remove") {
        offlineFriendArgument("friend") {

        }
    }

    literalArgument("list") {

    }

    literalArgument("requests") {

    }

    literalArgument("accept") {

    }

    literalArgument("decline") {

    }

    literalArgument("revoke") {

    }
}