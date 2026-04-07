package dev.slne.surf.friends.paper.util

import dev.slne.surf.friends.core.service.friendPlayerService
import org.bukkit.entity.Player

val Player.friendPlayer
    get() = friendPlayerService.players.find { it.uuid == this.uniqueId }
        ?: error("Player ${this.name} is not cached as a FriendPlayer")