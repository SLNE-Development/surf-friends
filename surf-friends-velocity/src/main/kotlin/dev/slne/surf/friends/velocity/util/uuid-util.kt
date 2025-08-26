package dev.slne.surf.friends.velocity.util

import com.velocitypowered.api.proxy.Player
import dev.slne.surf.friends.velocity.plugin
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.jvm.optionals.getOrNull

val timeFormatter: DateTimeFormatter = DateTimeFormatter
    .ofPattern("dd.MM.yyyy, HH:mm:ss", Locale.GERMANY)
    .withZone(ZoneId.of("Europe/Berlin"))

fun Long.format(): String {
    return timeFormatter.format(Instant.ofEpochMilli(this))
}

fun formatTime(millis: Long): String {
    return timeFormatter.format(Instant.ofEpochMilli(millis))
}

fun UUID.sendText(builder: SurfComponentBuilder.() -> Unit) {
    val player = plugin.proxy.getPlayer(this).getOrNull() ?: return

    player.sendMessage(Colors.PREFIX.append(SurfComponentBuilder(builder)))
}

suspend fun UUID.getUsernameAsync(): String {
    return PlayerLookupService.getUsername(this) ?: "Unknown"
}

fun UUID.getUsername(): String {
    return plugin.proxy.getPlayer(this).getOrNull()?.username ?: "Unknown"
}

fun UUID.toPlayer(): Player? {
    return plugin.proxy.getPlayer(this).getOrNull()
}