package dev.slne.surf.friends.velocity.command.subcommand.request

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.velocity.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry

fun CommandAPICommand.friendRequestDeclineCommand() = subcommand("decline") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_REQUEST_DECLINE)
    
    surfOfflinePlayerArgument("target")

    playerExecutor { player, args ->
        val target: SurfPlayer by args

        val playerFriendsPlayer = FriendsPlayer[player.uniqueId]
        val targetFriendsPlayer = FriendsPlayer[target.uuid]

        container.launch {
            playerFriendsPlayer.declineFriendRequest(targetFriendsPlayer)
        }
    }
}