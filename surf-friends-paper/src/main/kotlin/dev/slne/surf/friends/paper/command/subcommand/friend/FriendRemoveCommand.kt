package dev.slne.surf.friends.paper.command.subcommand.friend

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.paper.plugin
import dev.slne.surf.friends.paper.util.FriendPermissionRegistry

fun CommandAPICommand.friendRemoveCommand() = subcommand("remove") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_REMOVE)
    
    surfOfflinePlayerArgument("target")

    playerExecutor { player, args ->
        val target: SurfPlayer by args

        if (player.uniqueId == target.uuid) {
            player.sendText {
                error("Du kannst dich nicht selbst als Freund entfernen.")
            }
            return@playerExecutor
        }

        val playerFriendsPlayer = FriendsPlayer[player.uniqueId]
        val targetFriendsPlayer = FriendsPlayer[target.uuid]

        val friendShip = playerFriendsPlayer.findFriendship(targetFriendsPlayer)

        if (friendShip == null) {
            player.sendText {
                error("Du bist nicht mit ")
                append(target.displayName())
                error(" befreundet.")
            }
            return@playerExecutor
        }

        plugin.launch {
            playerFriendsPlayer.removeFriendship(targetFriendsPlayer)
        }
    }
}

