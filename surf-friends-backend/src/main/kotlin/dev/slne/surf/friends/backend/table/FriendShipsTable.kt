package dev.slne.surf.friends.backend.table

import dev.slne.surf.database.table.AuditableLongIdTable

object FriendShipsTable : AuditableLongIdTable("friend_ships") {
    val requesterId = long("requester_id").references(FriendPlayerTable.id)
    val acceptorId = long("acceptor_id").references(FriendPlayerTable.id)
}