package dev.slne.surf.friends.core.client.player

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import dev.slne.surf.api.core.util.toObjectSet
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.player.FriendsPlayerManager
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.jetbrains.annotations.UnmodifiableView
import java.util.*

@AutoService(FriendsPlayerManager::class)
class ClientFriendsPlayerManager : FriendsPlayerManager {
    private val _players = Caffeine.newBuilder()
        .maximumSize(10_000)
        .build<UUID, FriendsPlayer> { uuid ->
            CoreFriendsPlayer(uuid)
        }

    override val players: @UnmodifiableView ObjectSet<FriendsPlayer>
        get() = _players.asMap().values.toObjectSet()

    override fun findPlayer(uuid: UUID) = _players.get(uuid)

    override fun invalidatePlayer(uuid: UUID) {
        _players.invalidate(uuid)
    }
}

