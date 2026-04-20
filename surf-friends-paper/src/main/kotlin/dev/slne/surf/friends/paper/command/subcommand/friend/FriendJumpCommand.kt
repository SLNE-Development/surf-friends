package dev.slne.surf.friends.paper.command.subcommand.friend

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.command.args.awaiting
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.paper.command.argument.friend.friendArgument
import dev.slne.surf.friends.paper.util.FriendPermissionRegistry
import org.bukkit.Bukkit

fun CommandAPICommand.friendJumpCommand() = subcommand("jump") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_JUMP)

    friendArgument("target")

    playerExecutorSuspend { player, args ->
        val target = args.awaiting<FriendsPlayer>("target")
        val targetSurfPlayer = target.surfPlayer()
        val onlineFriend = Bukkit.getPlayer(target.uuid)

        if (onlineFriend == null) {
            player.sendText {
                appendErrorPrefix()
                append(targetSurfPlayer.displayName())
                error(" ist nicht online.")
            }
            return@playerExecutorSuspend
        }

        player.teleportAsync(onlineFriend.location)

        player.sendText {
            appendSuccessPrefix()
            success("Du wurdest zu ")
            append(targetSurfPlayer.displayName())
            success(" teleportiert.")
        }
    }
}
