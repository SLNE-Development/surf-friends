package dev.slne.surf.friends.minestom.platform

import com.google.auto.service.AutoService
import dev.slne.minestom.lobby.api.extension.ConnectionManager
import dev.slne.surf.friends.core.client.platform.FriendsPlatform
import net.kyori.adventure.text.Component
import java.util.*

@AutoService(FriendsPlatform::class)
class MinestomFriendsPlatform : FriendsPlatform {
    override fun isOnline(uuid: UUID) = ConnectionManager.getOnlinePlayerByUuid(uuid) != null

    override fun sendMessage(uuid: UUID, message: Component) {
        ConnectionManager.getOnlinePlayerByUuid(uuid)?.sendMessage(message)
    }
}
