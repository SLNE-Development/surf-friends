package dev.slne.surf.friends.backend.service

import com.destroystokyo.paper.profile.PlayerProfile
import com.google.auto.service.AutoService
import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.friends.backend.repository.friendPlayerRepository
import dev.slne.surf.friends.core.loader.redisApi
import dev.slne.surf.friends.core.service.FriendPlayerService
import dev.slne.surf.surfapi.bukkit.api.command.util.idOrThrow
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(FriendPlayerService::class)
class FriendPlayerServiceImpl : FriendPlayerService, Services.Fallback {
    val playerCache = redisApi.createSyncMap<UUID, FriendPlayer>("surf-friends:player-cache")

    override val players: ObjectSet<FriendPlayer>
        get() = playerCache.snapshot().values.toObjectSet()

    override fun cachePlayer(friendPlayer: FriendPlayer) {
        playerCache.put(friendPlayer.uuid, friendPlayer)
    }

    override fun invalidatePlayer(uuid: UUID) {
        playerCache.invalidate(uuid)
    }

    override fun init() {}

    override suspend fun loadOrCreatePlayer(profile: PlayerProfile): FriendPlayer {
        val cachedPlayer = playerCache.getIfPresent(profile.idOrThrow())
        if (cachedPlayer != null) {
            return cachedPlayer
        }

        val loadedPlayer = friendPlayerRepository.loadOrCreatePlayer(profile)
        cachePlayer(loadedPlayer)
        return loadedPlayer
    }

    override suspend fun findOrLoadPlayer(name: String): FriendPlayer? {
        val cachedPlayer = playerCache.asMap().values.find { it.name == name }
        if (cachedPlayer != null) {
            return cachedPlayer
        }

        val loadedPlayer = friendPlayerRepository.loadPlayer(name)

        loadedPlayer?.let { cachePlayer(it) }
        return loadedPlayer
    }

    override suspend fun savePlayer(friendPlayer: FriendPlayer) {
        friendPlayerRepository.savePlayer(friendPlayer)
    }
}