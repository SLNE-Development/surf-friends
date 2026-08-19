package dev.slne.surf.friends.paper.command.subcommand.friend

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.command.args.awaiting
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.core.client.command.friendInfoComponent
import dev.slne.surf.friends.core.client.permission.FriendPermissions
import dev.slne.surf.friends.paper.command.argument.friend.friendArgument

fun CommandAPICommand.friendInfoCommand() = subcommand("info") {
    withPermission(FriendPermissions.COMMAND_FRIEND_INFO)

    friendArgument("target")

    playerExecutorSuspend { player, args ->
        val target = args.awaiting<FriendsPlayer>("target")

        player.sendMessage(friendInfoComponent(player.uniqueId, target))
    }
}
