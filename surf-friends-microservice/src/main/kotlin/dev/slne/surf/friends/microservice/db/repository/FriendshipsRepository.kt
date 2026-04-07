package dev.slne.surf.friends.microservice.db.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.or
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.friends.api.model.Friendship
import dev.slne.surf.friends.microservice.db.table.FriendshipsTable
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import java.time.OffsetDateTime
import java.util.*

object FriendshipsRepository {
    suspend fun fetchFriendships() = suspendTransaction {
        FriendshipsTable.selectAll()
            .map {
                Friendship(
                    senderUuid = it[FriendshipsTable.senderUuid],
                    targetUuid = it[FriendshipsTable.targetUuid],
                    createdAt = it[FriendshipsTable.createdAt],
                )
            }
            .toList()
    }

    suspend fun createFriendship(
        senderUuid: UUID,
        targetUuid: UUID,
        createdAt: OffsetDateTime
    ) = suspendTransaction {
        FriendshipsTable.insert {
            it[this.senderUuid] = senderUuid
            it[this.targetUuid] = targetUuid
            it[this.createdAt] = createdAt
        }

        Unit
    }

    suspend fun deleteFriendship(
        senderUuid: UUID,
        targetUuid: UUID
    ) = suspendTransaction {
        FriendshipsTable.deleteWhere {
            ((FriendshipsTable.senderUuid eq senderUuid) and
                    (FriendshipsTable.targetUuid eq targetUuid)) or
                    ((FriendshipsTable.senderUuid eq targetUuid) and
                            (FriendshipsTable.targetUuid eq senderUuid))
        }

        Unit
    }
}