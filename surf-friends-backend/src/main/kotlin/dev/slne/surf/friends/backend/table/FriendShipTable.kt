package dev.slne.surf.friends.backend.table

import org.jetbrains.exposed.sql.Table
import java.util.*

object FriendShipTable : Table("friend_ships") {
    val id = integer("id").autoIncrement()
    val userUuid = varchar("user_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val friendUuid =
        varchar("friend_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val created_at = long("created_at")

    override val primaryKey = PrimaryKey(id)
}