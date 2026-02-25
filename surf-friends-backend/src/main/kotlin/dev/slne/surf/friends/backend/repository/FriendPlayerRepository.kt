package dev.slne.surf.friends.backend.repository

import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.friends.api.friend.Friendship
import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.friends.backend.table.FriendPlayerTable
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.flow.firstOrNull
import org.bukkit.entity.Player

val friendPlayerRepository = FriendPlayerRepository()

class FriendPlayerRepository {
    suspend fun loadOrCreatePlayer(player: Player) = suspendTransaction {
        val uuid = player.uniqueId
        val row =
            FriendPlayerTable.selectAll().where(FriendPlayerTable.playerUuid eq uuid).firstOrNull()

        if (row == null) {
            FriendPlayerTable.insert {
                it[playerUuid] = uuid
                it[playerName] = player.name
                it[texture] =
                    player.playerProfile.properties.find { property -> property.name == "textures" }?.value
                        ?: ""
            }

            createPlayer(
                row = FriendPlayerTable.selectAll().where(FriendPlayerTable.playerUuid eq uuid)
                    .firstOrNull() ?: error("Failed to load created player with uuid $uuid"),
                sentRequests = friendRequestRepository.loadSentRequests(uuid),
                receivedRequests = friendRequestRepository.loadReceivedRequests(uuid),
                friendShips = friendShipRepository.loadFriendShips(uuid)
            )
        } else {
            createPlayer(
                row = row,
                sentRequests = friendRequestRepository.loadSentRequests(uuid),
                receivedRequests = friendRequestRepository.loadReceivedRequests(uuid),
                friendShips = friendShipRepository.loadFriendShips(uuid)
            )
        }
    }

    private fun createPlayer(
        row: ResultRow,
        sentRequests: ObjectSet<FriendRequest>,
        receivedRequests: ObjectSet<FriendRequest>,
        friendShips: ObjectSet<Friendship>
    ) =
        FriendPlayer(
            uuid = row[FriendPlayerTable.playerUuid],
            name = row[FriendPlayerTable.playerName],
            sentFriendRequests = sentRequests,
            receivedFriendRequests = receivedRequests,
            friends = friendShips
        )
}