package dev.slne.surf.friends.minestom.command.subcommand.request

import dev.slne.minestom.lobby.api.command.commandapi.CommandAPICommand
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutorSuspend
import dev.slne.minestom.lobby.api.command.commandapi.dsl.subcommand
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.core.client.permission.FriendPermissions
import dev.slne.surf.friends.minestom.command.argument.request.receivedFriendRequestArgument
import dev.slne.surf.friends.minestom.command.resolveReceivedFriendRequestSender

fun CommandAPICommand.friendRequestAcceptCommand(): CommandAPICommand = withSubcommand(
    subcommand("accept") {
        withPermission(FriendPermissions.COMMAND_FRIEND_REQUEST_ACCEPT)

        receivedFriendRequestArgument("target")

        playerExecutorSuspend { player, args ->
            val target = args.resolveReceivedFriendRequestSender("target")

            FriendsPlayer[player.uuid].acceptFriendRequest(target)
        }
    }
)
