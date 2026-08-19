package dev.slne.surf.friends.minestom.command

import dev.slne.minestom.lobby.api.command.commandapi.dsl.commandAPICommand
import dev.slne.surf.friends.core.client.permission.FriendPermissions
import dev.slne.surf.friends.minestom.command.subcommand.friend.friendListCommand
import dev.slne.surf.friends.minestom.command.subcommand.friend.friendInfoCommand
import dev.slne.surf.friends.minestom.command.subcommand.friend.friendRemoveCommand
import dev.slne.surf.friends.minestom.command.subcommand.request.*

fun friendCommand() = commandAPICommand("friend") {
    withPermission(FriendPermissions.COMMAND_FRIEND)
    withAliases("f")

    friendRemoveCommand()
    friendInfoCommand()

    friendRequestAcceptCommand()
    friendRequestDeclineCommand()
    friendRequestRevokeCommand()
    friendRequestListCommand()

    withSubcommand(friendListCommand("list"))
    withSubcommand(friendRequestSendCommand("add"))
}
