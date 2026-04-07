package dev.slne.surf.friends.velocity.util

import dev.slne.surf.api.core.messages.adventure.buildText
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")

fun OffsetDateTime.format(): String = format(DATE_TIME_FORMATTER)
fun OffsetDateTime.formatComponent() = buildText {
    variableValue(format())
}