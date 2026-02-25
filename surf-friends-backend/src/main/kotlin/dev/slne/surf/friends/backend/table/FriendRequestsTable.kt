package dev.slne.surf.friends.backend.table

import dev.slne.surf.database.table.AuditableLongIdTable

object FriendRequestsTable : AuditableLongIdTable("friend_requests") {
    val senderId = long("sender_id").references(FriendPlayerTable.id)
    val receiverId = long("receiver_id").references(FriendPlayerTable.id)
}