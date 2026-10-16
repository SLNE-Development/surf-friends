package dev.slne.surf.friends.minestom.listener

import dev.slne.surf.api.minestom.coroutine.minestomAsyncScope
import dev.slne.surf.api.minestom.event.EventRegistrar
import dev.slne.surf.api.minestom.extension.addListener
import dev.slne.surf.friends.core.client.connection.FriendConnectionNotifier
import kotlinx.coroutines.launch
import net.minestom.server.event.Event
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerDisconnectEvent
import net.minestom.server.event.player.PlayerSpawnEvent

object ConnectionListener : EventRegistrar {
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
