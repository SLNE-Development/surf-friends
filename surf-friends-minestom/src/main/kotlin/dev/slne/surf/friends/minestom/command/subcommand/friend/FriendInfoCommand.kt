package dev.slne.surf.friends.minestom.command.subcommand.friend

import dev.slne.surf.api.minestom.command.CommandAPICommand
import dev.slne.surf.api.minestom.command.dsl.playerExecutorSuspend
import dev.slne.surf.api.minestom.command.dsl.subcommand
import dev.slne.surf.friends.core.client.command.friendInfoComponent
import dev.slne.surf.friends.core.client.permission.FriendPermissions
import dev.slne.surf.friends.minestom.command.argument.friend.friendArgument
import dev.slne.surf.friends.minestom.command.resolveFriend

fun CommandAPICommand.friendInfoCommand(): CommandAPICommand = withSubcommand(
    subcommand("info") {
        withPermission(FriendPermissions.COMMAND_FRIEND_INFO)

        friendArgument("target")

        playerExecutorSuspend { player, args ->
            val target = args.resolveFriend("target")

            player.sendMessage(friendInfoComponent(player.uuid, target))
        }
    }
)
