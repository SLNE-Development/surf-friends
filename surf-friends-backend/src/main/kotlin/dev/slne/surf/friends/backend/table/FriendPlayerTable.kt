package dev.slne.surf.friends.backend.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object FriendPlayerTable : LongIdTable("friend_players") {
    val playerUuid = nativeUuid("player_uuid").uniqueIndex()
    val playerName = varchar("player_name", 16)
    val texture = largeText("texture")
}