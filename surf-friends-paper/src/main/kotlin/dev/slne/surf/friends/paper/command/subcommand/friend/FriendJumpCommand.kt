package dev.slne.surf.friends.paper.command.subcommand.friend

import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.api.core.command.args.awaiting
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.paper.command.argument.friend.friendArgument
import dev.slne.surf.friends.paper.plugin
import dev.slne.surf.friends.paper.util.FriendPermissionRegistry
import org.bukkit.Bukkit

fun CommandAPICommand.friendJumpCommand() = subcommand("jump") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_JUMP)

    friendArgument("target")

    playerExecutor { player, args ->
        plugin.launch {
            val target = args.awaiting<FriendsPlayer>("target")
            val targetSurfPlayer = target.surfPlayer()
            val onlineFriend = Bukkit.getPlayer(target.uuid)

            if (onlineFriend == null) {
                player.sendText {
                    appendErrorPrefix()
                    append(targetSurfPlayer.displayName())
                    error(" ist nicht online.")
                }
                return@launch
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
}
