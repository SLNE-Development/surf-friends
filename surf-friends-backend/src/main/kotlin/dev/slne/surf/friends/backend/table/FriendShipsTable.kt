package dev.slne.surf.friends.backend.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.table.AuditableLongIdTable

object FriendShipsTable : AuditableLongIdTable("friend_ships") {
    val requesterUuid = nativeUuid("requester_uuid")
    val acceptorUuid = nativeUuid("acceptor_uuid")

    val requesterName = varchar("requester_name", 16)
    val acceptorName = varchar("acceptor_name", 16)
}