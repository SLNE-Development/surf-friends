package dev.slne.surf.friends.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer
import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.PluginContainer
import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.friends.core.service.databaseService
import dev.slne.surf.friends.velocity.command.friendCommand
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendListCommand
import dev.slne.surf.friends.velocity.command.subcommand.request.FriendRequestSendCommand
import dev.slne.surf.friends.velocity.listener.ConnectionListener
import dev.slne.surf.friends.velocity.redis.redisLoader
import java.nio.file.Path

class SurfFriendsPlugin
@Inject
constructor(
    val proxy: ProxyServer,
    val container: PluginContainer,
    @param:DataDirectory val dataDirectory: Path,
    suspendingPluginContainer: SuspendingPluginContainer
) {
    init {
        suspendingPluginContainer.initialize(this)
        INSTANCE = this
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        proxy.eventManager.register(this, ConnectionListener())

        databaseService.connect(dataDirectory)
        redisLoader.connect()

        friendCommand()
        FriendRequestSendCommand("fa").register()
        FriendListCommand("fl").register()
    }

    @Subscribe
    fun onProxyShutdown(event: ProxyInitializeEvent) {
        redisLoader.disconnect()
    }

    companion object {
        lateinit var INSTANCE: SurfFriendsPlugin
    }
}

val container get() = SurfFriendsPlugin.INSTANCE.container
val plugin get() = SurfFriendsPlugin.INSTANCE