package dev.slne.surf.friends.velocity.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendListCommand
import dev.slne.surf.friends.velocity.command.subcommand.friend.friendInfoCommand
import dev.slne.surf.friends.velocity.command.subcommand.friend.friendJumpCommand
import dev.slne.surf.friends.velocity.command.subcommand.friend.friendRemoveCommand
import dev.slne.surf.friends.velocity.command.subcommand.request.*
import dev.slne.surf.friends.velocity.command.subcommand.toggle.friendToggleCommand
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry

fun friendCommand() = commandAPICommand("friend") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND)
    withAliases("f")

    friendRemoveCommand()
    friendInfoCommand()
    friendJumpCommand()

    friendRequestAcceptCommand()
    friendRequestDeclineCommand()
    friendRequestRevokeCommand()
    friendRequestListCommand()
    friendToggleCommand()

    subcommand(FriendListCommand("list"))
    subcommand(FriendRequestSendCommand("add"))
}