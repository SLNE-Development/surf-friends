package dev.slne.surf.friends.microservice

import com.google.auto.service.AutoService
import dev.slne.surf.database.DatabaseApi
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.SchemaUtils
import dev.slne.surf.database.libs.org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import dev.slne.surf.friends.microservice.db.table.FriendRequestsTable
import dev.slne.surf.friends.microservice.db.table.FriendshipsTable
import dev.slne.surf.friends.microservice.rabbit.handler.FriendRequestHandler
import dev.slne.surf.friends.microservice.rabbit.handler.FriendshipHandler
import dev.slne.surf.microservice.api.microservice.Microservice
import dev.slne.surf.rabbitmq.api.ServerRabbitMQApi
import java.nio.file.Path
import kotlin.io.path.Path

@AutoService(Microservice::class)
class FriendsMicroservice : Microservice() {
    override val dataPath: Path = Path("config")
    private val rabbitApi = ServerRabbitMQApi.create("surf-friends", dataPath)
    private val databaseApi = DatabaseApi.create(dataPath)

    override suspend fun onBootstrap(args: List<String>) {
        suspendTransaction {
            SchemaUtils.create(
                FriendRequestsTable,
                FriendshipsTable
            )
        }

        rabbitApi.registerRequestHandler(FriendRequestHandler)
        rabbitApi.registerRequestHandler(FriendshipHandler)
        rabbitApi.freezeAndConnect()
    }

    override suspend fun onDisable() {
        rabbitApi.disconnect()
        databaseApi.shutdown()
    }
}