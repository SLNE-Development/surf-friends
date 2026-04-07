package dev.slne.surf.friends.api

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.friends.api.player.FriendsPlayer
import java.util.*

interface SurfFriendsApi {
    suspend fun getFriendsPlayer(uuid: UUID): FriendsPlayer
    fun invalidateFriendsPlayer(uuid: UUID)

    companion object : SurfFriendsApi by api {
        val INSTANCE get() = api
    }
}

private val api = requiredService<SurfFriendsApi>()