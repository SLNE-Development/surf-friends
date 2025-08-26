package dev.slne.surf.friends.velocity.command.subcommand.toggle

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry

fun CommandAPICommand.friendToggleCommand() = subcommand("settings") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_TOGGLE)
    toggleAnnouncementsCommand()
    toggleSoundsCommand()
}