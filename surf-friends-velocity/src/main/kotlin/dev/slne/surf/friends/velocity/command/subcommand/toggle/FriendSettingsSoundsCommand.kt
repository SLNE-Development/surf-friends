package dev.slne.surf.friends.velocity.command.subcommand.toggle

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.friends.core.service.friendService
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry
import dev.slne.surf.friends.velocity.util.sendText

fun CommandAPICommand.toggleSoundsCommand() = subcommand("notify-sounds") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_TOGGLE_SOUNDS)
    playerExecutor { player, _ ->
        container.launch {
            when (friendService.toggleSounds(player.uniqueId)) {
                true -> player.uniqueId.sendText {
                    success("Du hast die Soundbenachrichtigungen aktiviert.")
                }

                false -> player.uniqueId.sendText {
                    success("Du hast die Soundbenachrichtigungen deaktiviert.")
                }
            }
        }
    }
}