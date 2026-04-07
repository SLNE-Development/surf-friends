package dev.slne.surf.friends.core.service

import com.destroystokyo.paper.profile.PlayerProfile
import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

val friendPlayerService get() = requiredService<FriendPlayerService>()

interface FriendPlayerService {
    val players: ObjectSet<FriendPlayer>

    fun cachePlayer(friendPlayer: FriendPlayer)
    fun invalidatePlayer(uuid: UUID)

    suspend fun loadOrCreatePlayer(profile: PlayerProfile): FriendPlayer
    suspend fun findOrLoadPlayer(name: String): FriendPlayer?
    suspend fun findOrLoadPlayer(uuid: UUID): FriendPlayer?
    suspend fun savePlayer(friendPlayer: FriendPlayer)
}