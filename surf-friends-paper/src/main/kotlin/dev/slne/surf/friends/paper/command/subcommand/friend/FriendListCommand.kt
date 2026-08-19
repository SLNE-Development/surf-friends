package dev.slne.surf.friends.paper.command.subcommand.friend

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.friends.core.client.command.friendListComponent
import dev.slne.surf.friends.core.client.permission.FriendPermissions
import dev.slne.surf.friends.paper.plugin

class FriendListCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(FriendPermissions.COMMAND_FRIEND_LIST)

        playerExecutor { player, _ ->
            plugin.launch {
                player.sendMessage(friendListComponent(player.uniqueId))
            }
        }
    }
}
