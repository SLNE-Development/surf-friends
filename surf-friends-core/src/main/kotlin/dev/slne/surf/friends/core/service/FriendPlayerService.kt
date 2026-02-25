package dev.slne.surf.friends.core.service

import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.surfapi.core.api.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

val friendPlayerService get() = requiredService<FriendPlayerService>()

interface FriendPlayerService {
    fun cachePlayer(friendPlayer: FriendPlayer)
    fun invalidatePlayer(uuid: UUID)

    val players: ObjectSet<FriendPlayer>

    suspend fun loadPlayer(uuid: UUID): FriendPlayer
    suspend fun savePlayer(friendPlayer: FriendPlayer)
}