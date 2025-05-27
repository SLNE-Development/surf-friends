package dev.slne.surf.friends.velocity.util

import com.velocitypowered.api.proxy.Player
import dev.slne.surf.friends.velocity.plugin
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

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
    val optionalPlayer = plugin.proxy.getPlayer(this) ?: return
    val player = optionalPlayer.get()

    player.sendMessage(Colors.PREFIX.append(SurfComponentBuilder(builder)))
}

suspend fun UUID.getUsername(): String {
    return PlayerLookupService.getUsername(this) ?: "Unknown"
}

fun UUID.toPlayer(): Player? {
    return plugin.proxy.getPlayer(this).orElse(null)
}