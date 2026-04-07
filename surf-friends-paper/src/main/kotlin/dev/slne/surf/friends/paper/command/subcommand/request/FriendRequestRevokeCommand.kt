package dev.slne.surf.friends.paper.command.subcommand.request

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.paper.plugin
import dev.slne.surf.friends.paper.util.FriendPermissionRegistry

fun CommandAPICommand.friendRequestRevokeCommand() = subcommand("revoke") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_REQUEST_REVOKE)

    surfOfflinePlayerArgument("target")

    playerExecutor { player, args ->
        val target: SurfPlayer by args

        val playerFriendsPlayer = FriendsPlayer[player.uniqueId]
        val targetFriendsPlayer = FriendsPlayer[target.uuid]

        plugin.launch {
            playerFriendsPlayer.revokeFriendRequest(targetFriendsPlayer)
        }
    }
}

