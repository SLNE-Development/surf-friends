package dev.slne.surf.friends.backend.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.friends.backend.table.FriendRequestsTable
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import java.util.*

val friendRequestRepository = FriendRequestRepository()

class FriendRequestRepository {
    suspend fun loadSentRequests(uuid: UUID) = suspendTransaction {
        FriendRequestsTable.selectAll().where(FriendRequestsTable.senderUuid eq uuid)
            .map { createRequest(it) }
    }.toSet().toObjectSet()

    suspend fun loadReceivedRequests(uuid: UUID) = suspendTransaction {
        FriendRequestsTable.selectAll().where(FriendRequestsTable.receiverUuid eq uuid)
            .map { createRequest(it) }
    }.toSet().toObjectSet()

    suspend fun deleteRequest(request: FriendRequest) = suspendTransaction {
        FriendRequestsTable.deleteWhere {
            (FriendRequestsTable.senderUuid eq request.senderUuid) and
                    (FriendRequestsTable.receiverUuid eq request.receiverUuid)
        }
    }

    suspend fun saveRequest(request: FriendRequest) = suspendTransaction {
        FriendRequestsTable.insert {
            it[senderUuid] = request.senderUuid
            it[receiverUuid] = request.receiverUuid
            it[senderName] = request.senderName
            it[receiverName] = request.receiverName
            it[createdAt] = request.sentAt
        }
    }

    private fun createRequest(row: ResultRow) = FriendRequest(
        senderUuid = row[FriendRequestsTable.senderUuid],
        receiverUuid = row[FriendRequestsTable.receiverUuid],
        senderName = row[FriendRequestsTable.senderName],
        receiverName = row[FriendRequestsTable.receiverName],
        sentAt = row[FriendRequestsTable.createdAt]
    )
}