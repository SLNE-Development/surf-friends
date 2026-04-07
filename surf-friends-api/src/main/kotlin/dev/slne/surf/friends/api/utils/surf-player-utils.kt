package dev.slne.surf.friends.api.utils

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.core.api.common.SurfCoreApi
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.friends.api.player.FriendsPlayer
import java.util.*

suspend fun UUID.toSurfPlayer() = SurfCoreApi.getPlayer(this)
    ?: SurfCoreApi.getOfflinePlayer(this)

fun SurfPlayer?.displayName() = buildText {
    variableValue(this@displayName?.username ?: "unknown")
}

fun SurfPlayer.friendPlayer() = FriendsPlayer[uuid]