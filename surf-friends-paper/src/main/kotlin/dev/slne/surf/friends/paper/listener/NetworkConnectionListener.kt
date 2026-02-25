package dev.slne.surf.friends.paper.listener

import dev.slne.surf.core.api.common.event.SurfEventHandler
import dev.slne.surf.core.api.common.event.SurfPlayerConnectEvent
import dev.slne.surf.core.api.common.event.SurfPlayerDisconnectEvent

object NetworkConnectionListener {
    @SurfEventHandler
    fun onNetworkConnect(event: SurfPlayerConnectEvent) {

    }

    @SurfEventHandler
    fun onNetworkDisconnect(event: SurfPlayerDisconnectEvent) {

    }
}