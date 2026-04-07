package dev.slne.surf.friends.velocity

import com.github.shynixn.mccoroutine.velocity.launch
import com.github.shynixn.mccoroutine.velocity.velocityDispatcher
import com.google.auto.service.AutoService
import dev.slne.surf.friends.core.client.FriendsClientInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext

@AutoService(FriendsClientInstance::class)
class VelocityFriendsClientInstance : FriendsClientInstance() {
    override val dataPath: Path = plugin.dataPath

    override val mainDispatcher: CoroutineContext
        get() = container.velocityDispatcher

    override fun launch(
        context: CoroutineContext,
        start: CoroutineStart,
        block: suspend CoroutineScope.() -> Unit
    ) = container.launch(
        context = context,
        start = start,
        block = block
    )
}