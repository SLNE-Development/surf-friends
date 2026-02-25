package dev.slne.surf.friends.backend.table

import dev.slne.surf.database.columns.nativeUuid
import dev.slne.surf.database.table.AuditableLongIdTable

object FriendRequestsTable : AuditableLongIdTable("friend_requests") {
    val senderUuid = nativeUuid("sender_uuid").references(FriendPlayerTable.playerUuid)
    val receiverUuid = nativeUuid("receiver_uuid").references(FriendPlayerTable.playerUuid)
}