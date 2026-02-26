package dev.slne.surf.friends.backend.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.and
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.or
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.deleteWhere
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.friends.api.friend.Friendship
import dev.slne.surf.friends.backend.table.FriendShipsTable
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toSet
import java.util.*

val friendShipRepository = FriendShipRepository()

class FriendShipRepository {
    suspend fun loadFriendShips(uuid: UUID) = suspendTransaction {
        FriendShipsTable.selectAll().where {
            (FriendShipsTable.requesterUuid eq uuid) or (FriendShipsTable.acceptorUuid eq uuid)
        }.map { createFriendShip(it) }
    }.toSet().toObjectSet()

    suspend fun saveFriendship(friendship: Friendship) = suspendTransaction {
        FriendShipsTable.insert {
            it[requesterUuid] = friendship.requestedBy
            it[acceptorUuid] = friendship.acceptedBy
            it[requesterName] = friendship.requesterName
            it[acceptorName] = friendship.acceptorName
            it[createdAt] = friendship.createdAt
        }
    }

    suspend fun deleteFriendship(friendship: Friendship) = suspendTransaction {
        FriendShipsTable.deleteWhere {
            (FriendShipsTable.requesterUuid eq friendship.requestedBy) and
                    (FriendShipsTable.acceptorUuid eq friendship.acceptedBy)
        }
    }

    private fun createFriendShip(row: ResultRow) = Friendship(
        requestedBy = row[FriendShipsTable.requesterUuid],
        acceptedBy = row[FriendShipsTable.acceptorUuid],
        requesterName = row[FriendShipsTable.requesterName],
        acceptorName = row[FriendShipsTable.acceptorName],
        createdAt = row[FriendShipsTable.createdAt]
    )
}