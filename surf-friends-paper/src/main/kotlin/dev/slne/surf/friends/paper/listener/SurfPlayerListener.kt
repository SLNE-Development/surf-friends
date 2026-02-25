package dev.slne.surf.friends.paper.listener

import dev.slne.surf.core.api.common.event.SurfEventHandler
import dev.slne.surf.core.api.common.event.SurfPlayerConnectEvent
import dev.slne.surf.core.api.common.event.SurfPlayerDisconnectEvent

object SurfPlayerListener {
    @SurfEventHandler
    fun onNetworkConnect(event: SurfPlayerConnectEvent) {
        // TODO: Broadcast message to friends
    }

    @SurfEventHandler
    fun onNetworkDisconnect(event: SurfPlayerDisconnectEvent) {
        // TODO: Broadcast message to friends
    }
}