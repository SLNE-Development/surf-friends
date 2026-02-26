package dev.slne.surf.friends.backend.service

import com.destroystokyo.paper.profile.PlayerProfile
import com.google.auto.service.AutoService
import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.friends.backend.repository.friendPlayerRepository
import dev.slne.surf.friends.core.loader.redisLoader
import dev.slne.surf.friends.core.service.FriendPlayerService
import dev.slne.surf.surfapi.bukkit.api.command.util.idOrThrow
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(FriendPlayerService::class)
class FriendPlayerServiceImpl : FriendPlayerService, Services.Fallback {
    override val players: ObjectSet<FriendPlayer>
        get() = redisLoader.playerCache.snapshot().values.toObjectSet()

    override fun cachePlayer(friendPlayer: FriendPlayer) {
        redisLoader.playerCache.put(friendPlayer.uuid, friendPlayer)
    }

    override fun invalidatePlayer(uuid: UUID) {
        redisLoader.playerCache.remove(uuid)
    }

    override suspend fun loadOrCreatePlayer(profile: PlayerProfile): FriendPlayer {
        val cachedPlayer = players.firstOrNull { it.uuid == profile.idOrThrow() }
        if (cachedPlayer != null) {
            return cachedPlayer
        }

        val loadedPlayer = friendPlayerRepository.loadOrCreatePlayer(profile)
        cachePlayer(loadedPlayer)
        return loadedPlayer
    }

    override suspend fun findOrLoadPlayer(name: String): FriendPlayer? {
        val cachedPlayer = players.find { it.name == name }
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