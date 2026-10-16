package dev.slne.surf.friends.minestom

import dev.slne.surf.api.minestom.plugin.SurfMinestomPlugin
import dev.slne.surf.friends.core.client.FriendsClientInstance
import dev.slne.surf.friends.minestom.command.friendCommand
import dev.slne.surf.friends.minestom.command.subcommand.friend.friendListCommand
import dev.slne.surf.friends.minestom.command.subcommand.request.friendRequestSendCommand
import dev.slne.surf.friends.minestom.listener.ConnectionListener

object FriendsMinestomPlugin : SurfMinestomPlugin() {
    override suspend fun onLoad() {
        val instance = FriendsClientInstance.INSTANCE

        instance.withEvents()
        instance.onLoad()
    }

    override suspend fun onEnable() {
        FriendsClientInstance.INSTANCE.onEnable()
        friendCommand()

        friendRequestSendCommand("fa").register()
        friendListCommand("fl").register()

        ConnectionListener.register(eventNode)
    }

    override suspend fun onDisable() {
        FriendsClientInstance.INSTANCE.onDisable()
    }
}
