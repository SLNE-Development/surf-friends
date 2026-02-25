package dev.slne.surf.friends.backend.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.friends.core.service.FriendPlayerService
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import org.bukkit.entity.Player
import java.util.*

@AutoService(FriendPlayerService::class)
class FriendPlayerServiceImpl : FriendPlayerService, Services.Fallback {
    val playerCache = Caffeine.newBuilder().build<UUID, FriendPlayer>()

    override val players: ObjectSet<FriendPlayer>
        get() = playerCache.asMap().values.toObjectSet()

    override fun cachePlayer(friendPlayer: FriendPlayer) {
        TODO("Not yet implemented")
    }

    override fun invalidatePlayer(uuid: UUID) {
        TODO("Not yet implemented")
    }

    override suspend fun loadOrCreatePlayer(player: Player): FriendPlayer {
        TODO("Not yet implemented")
    }

    override suspend fun savePlayer(friendPlayer: FriendPlayer) {
        TODO("Not yet implemented")
    }
}