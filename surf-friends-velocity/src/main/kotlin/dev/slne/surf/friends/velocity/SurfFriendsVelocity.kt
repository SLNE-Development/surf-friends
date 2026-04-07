package dev.slne.surf.friends.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.friends.core.client.FriendsClientInstance
import dev.slne.surf.friends.velocity.command.friendCommand
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendListCommand
import dev.slne.surf.friends.velocity.command.subcommand.request.FriendRequestSendCommand
import dev.slne.surf.friends.velocity.listener.ConnectionListener
import java.nio.file.Path

class SurfFriendsVelocity @Inject constructor(
    val proxy: ProxyServer,
    val container: PluginContainer,
    @param:DataDirectory val dataPath: Path,
    suspendingPluginContainer: SuspendingPluginContainer
) {
    init {
        suspendingPluginContainer.initialize(this)
        INSTANCE = this
    }

    @Subscribe
    suspend fun onProxyInitialization(event: ProxyInitializeEvent) {
        val instance = FriendsClientInstance.INSTANCE

        instance.withEvents()
        instance.onLoad()
        instance.onEnable()

        proxy.eventManager.register(this, ConnectionListener())

        friendCommand()
        FriendRequestSendCommand("fa").register()
        FriendListCommand("fl").register()
    }

    @Subscribe
    suspend fun onProxyShutdown(event: ProxyShutdownEvent) {
        FriendsClientInstance.INSTANCE.onDisable()
    }

    companion object {
        lateinit var INSTANCE: SurfFriendsVelocity
    }
}

val container get() = SurfFriendsVelocity.INSTANCE.container
val plugin get() = SurfFriendsVelocity.INSTANCE
val server get() = SurfFriendsVelocity.INSTANCE.proxy
