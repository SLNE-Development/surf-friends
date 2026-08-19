package dev.slne.surf.friends.minestom.command.subcommand.request

import dev.slne.minestom.lobby.api.command.commandapi.CommandAPICommand
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutorSuspend
import dev.slne.minestom.lobby.api.command.commandapi.dsl.subcommand
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.core.client.permission.FriendPermissions
import dev.slne.surf.friends.minestom.command.argument.request.sentFriendRequestArgument
import dev.slne.surf.friends.minestom.command.resolveSentFriendRequestTarget

fun CommandAPICommand.friendRequestRevokeCommand(): CommandAPICommand = withSubcommand(
    subcommand("revoke") {
        withPermission(FriendPermissions.COMMAND_FRIEND_REQUEST_REVOKE)

        sentFriendRequestArgument("target")

        playerExecutorSuspend { player, args ->
            val target = args.resolveSentFriendRequestTarget("target")

            FriendsPlayer[player.uuid].revokeFriendRequest(target)
        }
    }
)
