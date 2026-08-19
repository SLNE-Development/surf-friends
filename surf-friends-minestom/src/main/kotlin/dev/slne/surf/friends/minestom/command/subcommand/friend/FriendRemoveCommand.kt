package dev.slne.surf.friends.minestom.command.subcommand.friend

import dev.slne.minestom.lobby.api.command.commandapi.CommandAPICommand
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutorSuspend
import dev.slne.minestom.lobby.api.command.commandapi.dsl.subcommand
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.core.client.permission.FriendPermissions
import dev.slne.surf.friends.minestom.command.argument.friend.friendArgument
import dev.slne.surf.friends.minestom.command.resolveFriend

fun CommandAPICommand.friendRemoveCommand(): CommandAPICommand = withSubcommand(
    subcommand("remove") {
        withPermission(FriendPermissions.COMMAND_FRIEND_REMOVE)

        friendArgument("target")

        playerExecutorSuspend { player, args ->
            val target = args.resolveFriend("target")

            FriendsPlayer[player.uuid].removeFriendship(target)
        }
    }
)
