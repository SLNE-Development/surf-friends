package dev.slne.surf.friends.microservice.db.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.columns.time.offsetDateTime
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object FriendshipsTable : LongIdTable("friendships") {
    val playerUuid = nativeUuid("player_uuid")
    val friendUuid = nativeUuid("friend_uuid")
    val createdAt = offsetDateTime("created_at")

    init {
        uniqueIndex(playerUuid, friendUuid)
    }
}