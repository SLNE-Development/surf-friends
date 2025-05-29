package dev.slne.surf.friends.fallback.service

import com.google.auto.service.AutoService

import dev.slne.surf.database.DatabaseProvider
import dev.slne.surf.friends.api.model.FriendRequest
import dev.slne.surf.friends.api.model.Friendship
import dev.slne.surf.friends.api.util.FriendSettingsPair
import dev.slne.surf.friends.core.model.CoreFriendRequest
import dev.slne.surf.friends.core.model.CoreFriendship
import dev.slne.surf.friends.core.pair.CoreFriendSettingsPair
import dev.slne.surf.friends.core.service.DatabaseService
import dev.slne.surf.surfapi.core.api.util.toObjectSet

import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.kyori.adventure.util.Services

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

import java.nio.file.Path
import java.util.UUID

@AutoService(DatabaseService::class)
class FallbackDatabaseService : DatabaseService, Services.Fallback {
    object FriendShips: Table("friend_ships") {
        val id = integer("id").autoIncrement()
        val userUuid = varchar("user_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val friendUuid = varchar("friend_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val created_at = long("created_at")

        override val primaryKey = PrimaryKey(id)
    }

    object FriendRequests: Table("friend_requests") {
        val id = integer("id").autoIncrement()
        val senderUuid = varchar("sender_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val receiverUuid = varchar("receiver_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() })
        val send_at = long("created_at")

        override val primaryKey = PrimaryKey(id)
    }

    object FriendSettings: Table("friend_settings") {
        val userUuid = varchar("user_uuid", 36).transform({ UUID.fromString(it) }, { it.toString() }).uniqueIndex()
        var announcementsEnabled = bool("announcements_enabled").default(true)
        var soundsEnabled = bool("sounds_enabled").default(true)

        override val primaryKey = PrimaryKey(userUuid)
    }

    override fun connect(path: Path) {
        DatabaseProvider(path, path).connect()

        transaction {
            SchemaUtils.create(
                FriendShips,
                FriendRequests,
                FriendSettings
            )
        }
    }

    override suspend fun getFriends(
        uuid: UUID
    ) : ObjectSet<Friendship> {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                FriendShips.selectAll().where (FriendShips.userUuid eq uuid)
                    .map {
                        CoreFriendship (
                            userUuid = it[FriendShips.userUuid],
                            friendUuid = it[FriendShips.friendUuid],
                            createdAt = it[FriendShips.created_at]
                        )

                    }
                    .toObjectSet()
            }
        }
    }

    override suspend fun getFriendship(
        playerA: UUID,
        playerB: UUID
    ): Friendship? {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                FriendShips.selectAll().where (
                    (FriendShips.userUuid eq playerA) and (FriendShips.friendUuid eq playerB) or (
                        (FriendShips.userUuid eq playerB) and (FriendShips.friendUuid eq playerA)
                    )
                )
                    .map {
                        CoreFriendship (
                            userUuid = it[FriendShips.userUuid],
                            friendUuid = it[FriendShips.friendUuid],
                            createdAt = it[FriendShips.created_at]
                        )
                    }
                    .firstOrNull()
            }
        }
    }

    override suspend fun getFriendRequest(
        sender: UUID,
        target: UUID
    ): FriendRequest? {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                FriendRequests.selectAll().where (
                    (FriendRequests.senderUuid eq sender) and (FriendRequests.receiverUuid eq target)
                )
                    .map {
                        CoreFriendRequest(
                            senderUuid = it[FriendRequests.senderUuid],
                            receiverUuid = it[FriendRequests.receiverUuid],
                            sentAt = it[FriendRequests.send_at]
                        )
                    }
                    .firstOrNull()
            }
        }
    }

    override suspend fun getSentFriendRequests(
        uuid: UUID
    ) : ObjectSet<FriendRequest> {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                FriendRequests.selectAll().where (FriendRequests.senderUuid eq uuid)
                    .map {
                        CoreFriendRequest(
                            senderUuid = it[FriendRequests.senderUuid],
                            receiverUuid = it[FriendRequests.receiverUuid],
                            sentAt = it[FriendRequests.send_at]
                        )
                    }
                    .toObjectSet()
            }
        }
    }

    override suspend fun getReceivedFriendRequests(
        uuid: UUID
    ) : ObjectSet<FriendRequest> {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                FriendRequests.selectAll().where (FriendRequests.receiverUuid eq uuid)
                    .map {
                        CoreFriendRequest(
                            senderUuid = it[FriendRequests.senderUuid],
                            receiverUuid = it[FriendRequests.receiverUuid],
                            sentAt = it[FriendRequests.send_at]
                        )
                    }
                    .toObjectSet()
            }
        }
    }

    override suspend fun getFriendSettings(
        uuid: UUID
    ) : CoreFriendSettingsPair {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                FriendSettings.selectAll().where (FriendSettings.userUuid eq uuid)
                    .map { it.toFriendSettings() }
                    .firstOrNull() ?: CoreFriendSettingsPair()
            }
        }
    }

    override suspend fun addFriendship(
        uuid: UUID,
        friend: UUID
    ) : Friendship {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                val current: Long = System.currentTimeMillis()

                FriendShips.insert {
                    it[FriendShips.userUuid] = uuid
                    it[FriendShips.friendUuid] = friend
                    it[FriendShips.created_at] = System.currentTimeMillis()
                }

                return@newSuspendedTransaction CoreFriendship(
                    userUuid = uuid,
                    friendUuid = friend,
                    createdAt = current
                )
            }
        }
    }

    override suspend fun removeFriendship(
        uuid: UUID,
        friend: UUID
    ) {
        withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                FriendShips.deleteWhere {
                    (FriendShips.userUuid eq uuid) and (FriendShips.friendUuid eq friend)
                }

                FriendShips.deleteWhere {
                    (FriendShips.userUuid eq friend) and (FriendShips.friendUuid eq uuid)
                }
            }
        }
    }

    override suspend fun addFriendRequest(
        sender: UUID,
        receiver: UUID
    ) : FriendRequest {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                val current: Long = System.currentTimeMillis()

                FriendRequests.insert {
                    it[FriendRequests.senderUuid] = sender
                    it[FriendRequests.receiverUuid] = receiver
                    it[FriendRequests.send_at] = current
                }

                return@newSuspendedTransaction  CoreFriendRequest(
                    senderUuid = sender,
                    receiverUuid = receiver,
                    sentAt = current
                )
            }
        }
    }

    override suspend fun removeFriendRequest(
        sender: UUID,
        receiver: UUID
    ) {
        withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                FriendRequests.deleteWhere {
                    (FriendRequests.senderUuid eq sender) and (FriendRequests.receiverUuid eq receiver)
                }
            }
        }
    }

    override suspend fun updateFriendSettings (
        uuid: UUID,
        pair: CoreFriendSettingsPair
    ) : CoreFriendSettingsPair {
        return withContext(Dispatchers.IO) {
            newSuspendedTransaction {
                if(FriendSettings.selectAll().where(FriendSettings.userUuid eq uuid).firstOrNull() == null) {
                    FriendSettings.insert {
                        it[FriendSettings.userUuid] = uuid
                        it[FriendSettings.announcementsEnabled] = pair.announcementsEnabled
                        it[FriendSettings.soundsEnabled] = pair.soundsEnabled
                    }
                } else {
                    FriendSettings.update({ FriendSettings.userUuid eq uuid }) {
                        it[FriendSettings.announcementsEnabled] = pair.announcementsEnabled
                        it[FriendSettings.soundsEnabled] = pair.soundsEnabled
                    }
                }

                pair
            }
        }
    }

    fun ResultRow.toFriendSettings() : CoreFriendSettingsPair {
        return CoreFriendSettingsPair(
            announcementsEnabled = this[FriendSettings.announcementsEnabled],
            soundsEnabled = this[FriendSettings.soundsEnabled]
        )
    }
}