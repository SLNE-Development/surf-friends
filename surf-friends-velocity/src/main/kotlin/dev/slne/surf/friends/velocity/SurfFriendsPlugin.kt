package dev.slne.surf.friends.velocity

import com.github.shynixn.mccoroutine.velocity.SuspendingPluginContainer

import com.google.inject.Inject
import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent
import com.velocitypowered.api.plugin.Dependency

import com.velocitypowered.api.plugin.annotation.DataDirectory
import com.velocitypowered.api.proxy.ProxyServer
import dev.slne.surf.friends.core.service.databaseService
import dev.slne.surf.friends.velocity.command.FriendCommand
import dev.slne.surf.friends.velocity.command.subcommand.friend.FriendListCommand
import dev.slne.surf.friends.velocity.command.subcommand.request.FriendRequestSendCommand
import dev.slne.surf.friends.velocity.listener.ConnectionListener

import java.nio.file.Path
import kotlin.jvm.optionals.getOrNull

class SurfFriendsPlugin
@Inject
constructor (
    val proxy: ProxyServer,
    @DataDirectory val dataDirectory: Path,
    suspendingPluginContainer: SuspendingPluginContainer
) {
    init {
        suspendingPluginContainer.initialize(this)
        INSTANCE = this
    }

    @Subscribe
    fun onProxyInitialization(event: ProxyInitializeEvent) {
        val eventManager = proxy.eventManager
        eventManager.register(this, ConnectionListener())

        databaseService.connect(dataDirectory)

        FriendCommand("friend").register()
        FriendRequestSendCommand("fa").register()
        FriendListCommand("fl").register()
    }

    companion object {
        lateinit var INSTANCE: SurfFriendsPlugin
    }
}

val container get() = plugin.proxy.pluginManager.getPlugin("surf-friends-velocity").getOrNull() ?: throw IllegalArgumentException("The providing plugin container is not available. Got the plugin ID changed?")
val plugin get() = SurfFriendsPlugin.INSTANCE