package dev.slne.surf.friends.paper.command.subcommand.request

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.friends.core.client.command.friendRequestListComponent
import dev.slne.surf.friends.core.client.permission.FriendPermissions
import dev.slne.surf.friends.paper.plugin

fun CommandAPICommand.friendRequestListCommand() = subcommand("requests") {
    withPermission(FriendPermissions.COMMAND_FRIEND_REQUEST_LIST)

    playerExecutor { player, _ ->
        plugin.launch {
            player.sendMessage(friendRequestListComponent(player.uniqueId))
        }
    }
}
