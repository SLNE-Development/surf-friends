package dev.slne.surf.friends.velocity.command

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendInfoCommand
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendJumpCommand
import dev.slne.surf.friends.velocity.command.subcommand.request.FriendRequestSendCommand
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendListCommand
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendRemoveCommand
import dev.slne.surf.friends.velocity.command.subcommand.request.FriendRequestAcceptCommand
import dev.slne.surf.friends.velocity.command.subcommand.request.FriendRequestDeclineCommand
import dev.slne.surf.friends.velocity.command.subcommand.request.FriendRequestListCommand
import dev.slne.surf.friends.velocity.command.subcommand.request.FriendRequestRevokeCommand
import dev.slne.surf.friends.velocity.command.subcommand.toggle.FriendToggleCommand
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry

class FriendCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission(FriendPermissionRegistry.COMMAND_FRIEND)
        withAliases("f")

        subcommand(FriendRemoveCommand("remove"))
        subcommand(FriendListCommand("list"))
        subcommand(FriendInfoCommand("info"))
        subcommand(FriendJumpCommand("jump"))

        subcommand(FriendRequestSendCommand("add"))
        subcommand(FriendRequestAcceptCommand("accept"))
        subcommand(FriendRequestDeclineCommand("decline"))
        subcommand(FriendRequestRevokeCommand("revoke"))
        subcommand(FriendRequestListCommand("requests"))

        subcommand(FriendToggleCommand("toggle"))
    }
}