package dev.slne.surf.friends.minestom

import com.google.inject.Inject
import com.google.inject.Singleton
import dev.slne.minestom.lobby.api.plugin.MinestomPluginEntrypoint
import dev.slne.minestom.lobby.api.plugin.annotation.DataDirectory
import dev.slne.surf.friends.core.client.FriendsClientInstance
import java.nio.file.Path

@Singleton
class FriendsMinestomEntrypoint @Inject constructor(
    @DataDirectory path: Path
) : MinestomPluginEntrypoint {

    init {
        dataPath = path
    }

    override suspend fun start() {
        val instance = FriendsClientInstance.INSTANCE

        instance.withEvents()
        instance.onLoad()
        instance.onEnable()
    }

    override suspend fun stop() {
        FriendsClientInstance.INSTANCE.onDisable()
    }

    companion object {
        lateinit var dataPath: Path
    }
}
