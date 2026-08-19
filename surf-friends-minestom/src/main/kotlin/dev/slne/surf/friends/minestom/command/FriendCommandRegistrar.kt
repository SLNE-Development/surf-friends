package dev.slne.surf.friends.minestom.command

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.command.CommandRegistrar
import dev.slne.surf.friends.minestom.command.subcommand.friend.friendListCommand
import dev.slne.surf.friends.minestom.command.subcommand.request.friendRequestSendCommand

/**
 * Registers the friend commands of this plugin.
 */
class FriendCommandRegistrar @Inject constructor() : CommandRegistrar {
    override fun register() {
        friendCommand()

        friendRequestSendCommand("fa").register()
        friendListCommand("fl").register()
    }
}
