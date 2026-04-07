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
                    playerUuid = it[FriendshipsTable.playerUuid],
                    friendUuid = it[FriendshipsTable.friendUuid],
                    createdAt = it[FriendshipsTable.createdAt],
                )
            }
            .toList()
    }

    suspend fun createFriendship(
        playerA: UUID,
        playerB: UUID,
        createdAt: OffsetDateTime
    ) = suspendTransaction {
        FriendshipsTable.insert {
            it[playerUuid] = playerA
            it[friendUuid] = playerB
            it[this.createdAt] = createdAt
        }

        FriendshipsTable.insert {
            it[playerUuid] = playerB
            it[friendUuid] = playerA
            it[this.createdAt] = createdAt
        }

        Unit
    }

    suspend fun deleteFriendship(
        playerA: UUID,
        playerB: UUID
    ) = suspendTransaction {
        FriendshipsTable.deleteWhere {
            ((FriendshipsTable.playerUuid eq playerA) and
                    (FriendshipsTable.friendUuid eq playerB)) or
                    ((FriendshipsTable.playerUuid eq playerB) and
                            (FriendshipsTable.friendUuid eq playerA))
        }

        Unit
    }
}