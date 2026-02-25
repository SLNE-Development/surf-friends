package dev.slne.surf.friends.backend.table

import org.jetbrains.exposed.sql.Table
import java.util.*

object FriendRequestTable : Table("friend_requests") {
    val id = integer("id").autoIncrement()
    val senderUuid =
        varchar("sender_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val receiverUuid =
        varchar("receiver_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
    val send_at = long("created_at")

    override val primaryKey = PrimaryKey(id)
}