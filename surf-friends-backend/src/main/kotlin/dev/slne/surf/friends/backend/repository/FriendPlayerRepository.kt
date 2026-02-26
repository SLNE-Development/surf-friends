package dev.slne.surf.friends.backend.repository

import com.destroystokyo.paper.profile.PlayerProfile
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.ResultRow
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.core.eq
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.insert
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.selectAll
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.friends.api.friend.Friendship
import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.friends.backend.table.FriendPlayerTable
import dev.slne.surf.surfapi.bukkit.api.command.util.idOrThrow
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.flow.firstOrNull

val friendPlayerRepository = FriendPlayerRepository()

class FriendPlayerRepository {
    suspend fun loadOrCreatePlayer(profile: PlayerProfile) = suspendTransaction {
        val uuid = profile.idOrThrow()
        val row =
            FriendPlayerTable.selectAll().where(FriendPlayerTable.playerUuid eq uuid).firstOrNull()

        if (row == null) {
            FriendPlayerTable.insert {
                it[playerUuid] = uuid
                it[playerName] = profile.name ?: error("Profile name is null for uuid $uuid")
                it[texture] =
                    profile.properties.find { property -> property.name == "textures" }?.value
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

    suspend fun loadPlayer(name: String) = suspendTransaction {
        FriendPlayerTable.selectAll().where(FriendPlayerTable.playerName eq name).firstOrNull()
            ?.let {
                val uuid = it[FriendPlayerTable.playerUuid]

                createPlayer(
                    row = it,
                    sentRequests = friendRequestRepository.loadSentRequests(uuid),
                    receivedRequests = friendRequestRepository.loadReceivedRequests(uuid),
                    friendShips = friendShipRepository.loadFriendShips(uuid)
                )
            }
    }

    suspend fun savePlayer(player: FriendPlayer) = suspendTransaction {
        FriendPlayerTable.insert {
            it[playerUuid] = player.uuid
            it[playerName] = player.name
            it[texture] = player.texture
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
            texture = row[FriendPlayerTable.texture],
            sentFriendRequests = sentRequests,
            receivedFriendRequests = receivedRequests,
            friends = friendShips
        )
}