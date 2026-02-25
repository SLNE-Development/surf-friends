package dev.slne.surf.friends.backend.table

import org.jetbrains.exposed.sql.Table
import java.util.*

object FriendSettingsTable : Table("friend_settings") {
    val userUuid =
        varchar("user_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() }).uniqueIndex()
    var announcementsEnabled = bool("announcements_enabled").default(true)
    var soundsEnabled = bool("sounds_enabled").default(true)

    override val primaryKey = PrimaryKey(userUuid)
}