package dev.slne.surf.friends.core.service

import dev.slne.surf.friends.api.player.FriendPlayer
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.*

interface FriendPlayerService {
    fun cachePlayer(friendPlayer: FriendPlayer)
    fun invalidatePlayer(uuid: UUID)
    fun getPlayers(): ObjectSet<FriendPlayer>

    suspend fun loadPlayer(uuid: UUID, name: String): FriendPlayer
    suspend fun savePlayer(friendPlayer: FriendPlayer)
}