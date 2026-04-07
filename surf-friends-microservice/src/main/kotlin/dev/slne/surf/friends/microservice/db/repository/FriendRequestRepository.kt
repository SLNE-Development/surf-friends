package dev.slne.surf.friends.microservice.db.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.friends.api.model.FriendRequest
import dev.slne.surf.friends.microservice.db.table.FriendRequestsTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.util.*

object FriendRequestRepository {
    suspend fun fetchFriendRequests(): List<FriendRequest> = suspendTransaction {
        FriendRequestsTable.selectAll().map {
            FriendRequest(
                senderUuid = it[FriendRequestsTable.senderUuid],
                targetUuid = it[FriendRequestsTable.targetUuid],
                createdAt = it[FriendRequestsTable.createdAt]
            )
        }.toList()
    }

    suspend fun createFriendRequest(
        friendRequest: FriendRequest
    ) = suspendTransaction {
        FriendRequestsTable.insert {
            it[this.senderUuid] = friendRequest.senderUuid
            it[this.targetUuid] = friendRequest.targetUuid
            it[this.createdAt] = friendRequest.createdAt
        }

        Unit
    }

    suspend fun deleteFriendRequest(
        senderUuid: UUID,
        targetUuid: UUID
    ) = suspendTransaction {
        FriendRequestsTable.deleteWhere {
            (FriendRequestsTable.senderUuid eq senderUuid) and
                    (FriendRequestsTable.targetUuid eq targetUuid)
        }

        Unit
    }
}