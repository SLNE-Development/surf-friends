package dev.slne.surf.friends.velocity.command.subcommand.toggle

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry

class FriendToggleCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission(FriendPermissionRegistry.COMMAND_FRIEND_TOGGLE)
        subcommand(FriendAnnouncementsToggleCommand("announcements"))
        subcommand(FriendSoundToggleCommand("sounds"))
    }
}