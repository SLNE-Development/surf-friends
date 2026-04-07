package dev.slne.surf.friends.paper.command.subcommand.request

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.command.args.awaiting
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.paper.command.argument.request.receivedFriendRequestArgument
import dev.slne.surf.friends.paper.plugin
import dev.slne.surf.friends.paper.util.FriendPermissionRegistry

fun CommandAPICommand.friendRequestAcceptCommand() = subcommand("accept") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_REQUEST_ACCEPT)

    receivedFriendRequestArgument("target")

    playerExecutor { player, args ->
        plugin.launch {
            val target = args.awaiting<FriendsPlayer>("target")
            val playerFriendsPlayer = FriendsPlayer[player.uniqueId]

            playerFriendsPlayer.acceptFriendRequest(target)
        }
    }
}
