package dev.slne.surf.friends.paper.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.friends.paper.command.subcommand.friend.FriendListCommand
import dev.slne.surf.friends.paper.command.subcommand.friend.friendInfoCommand
import dev.slne.surf.friends.paper.command.subcommand.friend.friendRemoveCommand
import dev.slne.surf.friends.paper.command.subcommand.request.*
import dev.slne.surf.friends.paper.util.FriendPermissionRegistry

fun friendCommand() = commandAPICommand("friend") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND)
    withAliases("f")

    friendRemoveCommand()
    friendInfoCommand()

    friendRequestAcceptCommand()
    friendRequestDeclineCommand()
    friendRequestRevokeCommand()
    friendRequestListCommand()

    subcommand(FriendListCommand("list"))
    subcommand(FriendRequestSendCommand("add"))
}

