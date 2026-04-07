package dev.slne.surf.friends.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.friends.core.client.FriendsClientInstance
import dev.slne.surf.friends.paper.command.friendCommand
import dev.slne.surf.friends.paper.command.subcommand.friend.FriendListCommand
import dev.slne.surf.friends.paper.command.subcommand.request.FriendRequestSendCommand
import dev.slne.surf.friends.paper.listener.ConnectionListener
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(SurfFriendsPaper::class.java)

class SurfFriendsPaper : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        val instance = FriendsClientInstance.INSTANCE

        instance.withEvents()
        instance.onLoad()
    }

    override suspend fun onEnableAsync() {
        FriendsClientInstance.INSTANCE.onEnable()

        server.pluginManager.registerEvents(ConnectionListener(), this)

        friendCommand()
        FriendRequestSendCommand("fa").register()
        FriendListCommand("fl").register()
    }

    override suspend fun onDisableAsync() {
        FriendsClientInstance.INSTANCE.onDisable()
    }
}

