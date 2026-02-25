package dev.slne.surf.friends.backend.service

import com.github.benmanes.caffeine.cache.Caffeine
import com.google.auto.service.AutoService
import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.friends.backend.repository.friendPlayerRepository
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
        playerCache.put(friendPlayer.uuid, friendPlayer)
    }

    override fun invalidatePlayer(uuid: UUID) {
        playerCache.invalidate(uuid)
    }

    override suspend fun loadOrCreatePlayer(player: Player): FriendPlayer {
        val cachedPlayer = playerCache.getIfPresent(player.uniqueId)
        if (cachedPlayer != null) {
            return cachedPlayer
        }

        val loadedPlayer = friendPlayerRepository.loadOrCreatePlayer(player)
        cachePlayer(loadedPlayer)
        return loadedPlayer
    }

    override suspend fun savePlayer(friendPlayer: FriendPlayer) {
        friendPlayerRepository.savePlayer(friendPlayer)
    }
}