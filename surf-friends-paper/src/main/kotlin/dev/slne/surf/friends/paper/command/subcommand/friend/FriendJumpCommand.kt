package dev.slne.surf.friends.paper.command.subcommand.friend

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.paper.util.FriendPermissionRegistry
import org.bukkit.Bukkit

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

        val onlineFriend = Bukkit.getPlayer(target.uuid)

        if (onlineFriend == null) {
            player.sendText {
                appendErrorPrefix()
                append(target.displayName())
                error(" ist nicht online.")
            }
            return@playerExecutor
        }

        player.teleportAsync(onlineFriend.location)

        player.sendText {
            appendSuccessPrefix()
            success("Du wurdest zu ")
            append(target.displayName())
            success(" teleportiert.")
        }
    }
}

