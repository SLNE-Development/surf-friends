package dev.slne.surf.friends.api.player

import dev.slne.surf.api.core.util.requiredService
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.jetbrains.annotations.UnmodifiableView
import java.util.*

interface FriendsPlayerManager {
    val players: @UnmodifiableView ObjectSet<FriendsPlayer>

    fun findPlayer(uuid: UUID): FriendsPlayer
    fun invalidatePlayer(uuid: UUID)

    companion object : FriendsPlayerManager by manager {
        val INSTANCE get() = manager
    }
}

private val manager = requiredService<FriendsPlayerManager>()