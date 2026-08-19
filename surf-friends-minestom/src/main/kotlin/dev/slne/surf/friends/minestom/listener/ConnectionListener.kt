package dev.slne.surf.friends.minestom.listener

import com.google.inject.Inject
import dev.slne.minestom.lobby.api.coroutine.minestomAsyncScope
import dev.slne.minestom.lobby.api.event.EventRegistrar
import dev.slne.minestom.lobby.api.extension.addListener
import dev.slne.surf.friends.core.client.connection.FriendConnectionNotifier
import kotlinx.coroutines.launch
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent

class ConnectionListener @Inject constructor() : EventRegistrar {
    override fun register(node: EventNode<Event>) {
        node.addListener<PlayerSpawnEvent> { event ->
            if (!event.isFirstSpawn) return@addListener

            val uuid = event.player.uuid
            val onlineFriends = FriendConnectionNotifier.greet(uuid)

            minestomAsyncScope.launch {
                FriendConnectionNotifier.announceOnline(uuid, onlineFriends)
            }
        }

        node.addListener<PlayerDisconnectEvent> { event ->
            val uuid = event.player.uuid

            minestomAsyncScope.launch {
                FriendConnectionNotifier.announceOffline(uuid)
            }
        }
    }
}
