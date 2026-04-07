package dev.slne.surf.friends.velocity.command.subcommand.friend

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.velocity.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.velocity.server
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry
import kotlin.jvm.optionals.getOrNull

fun CommandAPICommand.friendJumpCommand() = subcommand("jump") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_JUMP)

    surfOfflinePlayerArgument("target")

    playerExecutor { player, args ->
        val target: SurfPlayer by args

        if (player.uniqueId == target.uuid) {
            player.sendText {
                appendErrorPrefix()
                error("Du kannst nicht zu dir selbst springen.")
            }
            return@playerExecutor
        }

        val playerFriendsPlayer = FriendsPlayer[player.uniqueId]
        val targetFriendsPlayer = FriendsPlayer[target.uuid]

        if (!playerFriendsPlayer.hasFriendship(targetFriendsPlayer)) {
            player.sendText {
                appendErrorPrefix()
                error("Du bist nicht mit ")
                append(target.displayName())
                error(" befreundet.")
            }
            return@playerExecutor
        }

        val onlineFriend = server.getPlayer(target.uuid).getOrNull()

        if (onlineFriend == null) {
            player.sendText {
                appendErrorPrefix()
                append(target.displayName())
                error(" ist nicht online.")
            }
            return@playerExecutor
        }

        val targetServer = onlineFriend.currentServer.getOrNull()?.server

        if (targetServer == null) {
            player.sendText {
                appendErrorPrefix()
                append(target.displayName())
                error(" ist nicht auf einem Server.")
            }
            return@playerExecutor
        }

        player.createConnectionRequest(targetServer).fireAndForget()

        player.sendText {
            appendSuccessPrefix()
            success("Du bist ")
            append(target.displayName())
            success(" auf den Server gefolgt.")
        }
    }
}
