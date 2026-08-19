package dev.slne.surf.friends.core.client.platform

import dev.slne.surf.api.core.util.requiredService
import net.kyori.adventure.text.Component
import java.util.*

/**
 * The platform-specific operations the shared friends logic relies on.
 *
 * Every platform contributes exactly one implementation through `ServiceLoader`.
 */
interface FriendsPlatform {

    /**
     * Whether the player identified by [uuid] is connected to this server.
     */
    fun isOnline(uuid: UUID): Boolean

    /**
     * Sends [message] to the player identified by [uuid], if they are connected to this server.
     */
    fun sendMessage(uuid: UUID, message: Component)

    companion object {
        val INSTANCE get() = platform
    }
}

private val platform = requiredService<FriendsPlatform>()
