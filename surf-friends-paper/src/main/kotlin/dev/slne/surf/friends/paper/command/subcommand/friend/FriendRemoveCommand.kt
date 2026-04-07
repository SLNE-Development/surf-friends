package dev.slne.surf.friends.paper.command.subcommand.friend

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.command.args.awaiting
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.paper.command.argument.friend.friendArgument
import dev.slne.surf.friends.paper.plugin
import dev.slne.surf.friends.paper.util.FriendPermissionRegistry

fun CommandAPICommand.friendRemoveCommand() = subcommand("remove") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_REMOVE)

    friendArgument("target")

    playerExecutor { player, args ->
        plugin.launch {
            val target = args.awaiting<FriendsPlayer>("target")
            val playerFriendsPlayer = FriendsPlayer[player.uniqueId]

            playerFriendsPlayer.removeFriendship(target)
        }
    }
}
