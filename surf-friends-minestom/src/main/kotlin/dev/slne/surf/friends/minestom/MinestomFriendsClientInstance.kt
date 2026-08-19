package dev.slne.surf.friends.minestom

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.coroutine.MinestomDispatchers
import dev.slne.minestom.lobby.api.coroutine.minestomScope
import dev.slne.surf.friends.core.client.FriendsClientInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext

@AutoService(FriendsClientInstance::class)
class MinestomFriendsClientInstance : FriendsClientInstance() {
    override val dataPath: Path get() = FriendsMinestomEntrypoint.dataPath

    override val mainDispatcher: CoroutineContext
        get() = MinestomDispatchers.Main

    override fun launch(
        context: CoroutineContext,
        start: CoroutineStart,
        block: suspend CoroutineScope.() -> Unit
    ) = minestomScope.launch(
        context = context,
        start = start,
        block = block
    )
}
