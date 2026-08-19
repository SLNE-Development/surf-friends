package dev.slne.surf.friends.minestom

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.plugin.MinestomPlugin
import dev.slne.minestom.lobby.api.plugin.annotation.MinestomPluginMeta
import dev.slne.surf.friends.minestom.command.FriendCommandRegistrar
import dev.slne.surf.friends.minestom.listener.ConnectionListener

@AutoService(MinestomPlugin::class)
@MinestomPluginMeta(
    "surf-friends-minestom",
    dependsOn = [
        "surf-api-minestom",
        "surf-rabbitmq-minestom",
        "surf-redis-minestom",
        "surf-core-minestom",
        "surf-settings-minestom"
    ]
)
class FriendsMinestomPlugin : MinestomPlugin(FriendsMinestomEntrypoint::class.java) {
    override fun configurePlugin() {
        bindCommandRegistrar<FriendCommandRegistrar>()
        bindEventRegistrar<ConnectionListener>()
    }
}
