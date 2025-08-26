package dev.slne.surf.friends.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.database.DatabaseManager
import dev.slne.surf.friends.api.model.FriendRequest
import dev.slne.surf.friends.api.model.Friendship
import dev.slne.surf.friends.core.model.CoreFriendRequest
import dev.slne.surf.friends.core.model.CoreFriendship
import dev.slne.surf.friends.core.pair.CoreFriendSettings
import dev.slne.surf.friends.core.service.DatabaseService
import dev.slne.surf.friends.fallback.table.FriendRequestTable
import dev.slne.surf.friends.fallback.table.FriendSettingsTable
import dev.slne.surf.friends.fallback.table.FriendShipTable
import dev.slne.surf.surfapi.core.api.util.toObjectSet
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import net.kyori.adventure.util.Services
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Path
import java.util.*

@AutoService(DatabaseService::class)
class FallbackDatabaseService : DatabaseService, Services.Fallback {
    override fun connect(path: Path) {
        DatabaseManager(path, path).databaseProvider.connect()

        transaction {
            SchemaUtils.create(
                FriendShipTable,
                FriendRequestTable,
                FriendSettingsTable
            )
        }
    }

    override suspend fun getFriends(
        uuid: UUID
    ): ObjectSet<Friendship> = newSuspendedTransaction(Dispatchers.IO) {
        FriendShipTable.selectAll().where(FriendShipTable.userUuid eq uuid)
            .map {
                CoreFriendship(
                    userUuid = it[FriendShipTable.userUuid],
                    friendUuid = it[FriendShipTable.friendUuid],
                    createdAt = it[FriendShipTable.created_at]
                )
            }
            .toObjectSet()
    }

    override suspend fun getFriendship(
        playerA: UUID,
        playerB: UUID
    ): Friendship? = newSuspendedTransaction(Dispatchers.IO) {
        FriendShipTable.selectAll().where(
            (FriendShipTable.userUuid eq playerA) and (FriendShipTable.friendUuid eq playerB) or (
                    (FriendShipTable.userUuid eq playerB) and (FriendShipTable.friendUuid eq playerA)
                    )
        )
            .map {
                CoreFriendship(
                    userUuid = it[FriendShipTable.userUuid],
                    friendUuid = it[FriendShipTable.friendUuid],
                    createdAt = it[FriendShipTable.created_at]
                )
            }
            .firstOrNull()
    }

    override suspend fun getFriendRequest(
        sender: UUID,
        target: UUID
    ): FriendRequest? = newSuspendedTransaction(Dispatchers.IO) {
        FriendRequestTable.selectAll().where(
            (FriendRequestTable.senderUuid eq sender) and (FriendRequestTable.receiverUuid eq target)
        )
            .map {
                CoreFriendRequest(
                    senderUuid = it[FriendRequestTable.senderUuid],
                    receiverUuid = it[FriendRequestTable.receiverUuid],
                    sentAt = it[FriendRequestTable.send_at]
                )
            }
            .firstOrNull()
    }

    override suspend fun getSentFriendRequests(
        uuid: UUID
    ): ObjectSet<FriendRequest> = newSuspendedTransaction(Dispatchers.IO) {
        FriendRequestTable.selectAll().where(FriendRequestTable.senderUuid eq uuid)
            .map {
                CoreFriendRequest(
                    senderUuid = it[FriendRequestTable.senderUuid],
                    receiverUuid = it[FriendRequestTable.receiverUuid],
                    sentAt = it[FriendRequestTable.send_at]
                )
            }
            .toObjectSet()
    }

    override suspend fun getReceivedFriendRequests(
        uuid: UUID
    ): ObjectSet<FriendRequest> = newSuspendedTransaction(Dispatchers.IO) {
        FriendRequestTable.selectAll().where(FriendRequestTable.receiverUuid eq uuid)
            .map {
                CoreFriendRequest(
                    senderUuid = it[FriendRequestTable.senderUuid],
                    receiverUuid = it[FriendRequestTable.receiverUuid],
                    sentAt = it[FriendRequestTable.send_at]
                )
            }
            .toObjectSet()
    }

    override suspend fun getFriendSettings(
        uuid: UUID
    ): CoreFriendSettings = newSuspendedTransaction(Dispatchers.IO) {
        FriendSettingsTable.selectAll().where(FriendSettingsTable.userUuid eq uuid)
            .map { it.toFriendSettings() }
            .firstOrNull() ?: CoreFriendSettings()
    }

    override suspend fun addFriendship(
        uuid: UUID,
        friend: UUID
    ): Friendship = newSuspendedTransaction(Dispatchers.IO) {
        val current: Long = System.currentTimeMillis()

        FriendShipTable.insert {
            it[FriendShipTable.userUuid] = uuid
            it[FriendShipTable.friendUuid] = friend
            it[FriendShipTable.created_at] = System.currentTimeMillis()
        }

        CoreFriendship(
            userUuid = uuid,
            friendUuid = friend,
            createdAt = current
        )
    }

    override suspend fun removeFriendship(
        uuid: UUID,
        friend: UUID
    ) = newSuspendedTransaction(Dispatchers.IO) {
        FriendShipTable.deleteWhere {
            (FriendShipTable.userUuid eq uuid) and (FriendShipTable.friendUuid eq friend)
        }
        return@newSuspendedTransaction
    }

    override suspend fun addFriendRequest(
        sender: UUID,
        receiver: UUID
    ): FriendRequest = newSuspendedTransaction(Dispatchers.IO) {
        val current: Long = System.currentTimeMillis()

        FriendRequestTable.insert {
            it[FriendRequestTable.senderUuid] = sender
            it[FriendRequestTable.receiverUuid] = receiver
            it[FriendRequestTable.send_at] = current
        }

        CoreFriendRequest(
            senderUuid = sender,
            receiverUuid = receiver,
            sentAt = current
        )
    }

    override suspend fun removeFriendRequest(
        sender: UUID,
        receiver: UUID
    ) = newSuspendedTransaction(Dispatchers.IO) {
        FriendRequestTable.deleteWhere {
            (FriendRequestTable.senderUuid eq sender) and (FriendRequestTable.receiverUuid eq receiver)
        }
        return@newSuspendedTransaction
    }

    override suspend fun updateFriendSettings(
        uuid: UUID,
        pair: CoreFriendSettings
    ): CoreFriendSettings = newSuspendedTransaction(Dispatchers.IO) {
        FriendSettingsTable.upsert {
            it[FriendSettingsTable.userUuid] = uuid
            it[FriendSettingsTable.announcementsEnabled] = pair.announcementsEnabled
            it[FriendSettingsTable.soundsEnabled] = pair.soundsEnabled
        }

        pair
    }

    fun ResultRow.toFriendSettings(): CoreFriendSettings {
        return CoreFriendSettings(
            announcementsEnabled = this[FriendSettingsTable.announcementsEnabled],
            soundsEnabled = this[FriendSettingsTable.soundsEnabled]
        )
    }
}