package dev.slne.surf.friends.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.friends.paper.listener.PlayerConnectionListener
import dev.slne.surf.friends.paper.listener.SurfPlayerListener
import dev.slne.surf.surfapi.bukkit.api.event.register
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(PaperMain::class.java)

class PaperMain : SuspendingJavaPlugin() {
    override suspend fun onEnableAsync() {
        PlayerConnectionListener.register()
        surfCoreApi.registerListener(SurfPlayerListener)
    }
}