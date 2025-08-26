package dev.slne.surf.friends.velocity.command.subcommand.toggle

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand
import dev.slne.surf.friends.core.service.friendService
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry
import dev.slne.surf.friends.velocity.util.sendText

fun CommandAPICommand.toggleAnnouncementsCommand() = subcommand("notify-messages") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_TOGGLE_ANNOUNCEMENTS)
    playerExecutor { player, _ ->
        container.launch {
            when (friendService.toggleAnnouncements(player.uniqueId)) {
                true -> player.uniqueId.sendText {
                    success("Du hast die Freundes-Benachrichtigungen aktiviert.")
                }

                false -> player.uniqueId.sendText {
                    success("Du hast die Freundes-Benachrichtigungen deaktiviert.")
                }
            }
        }
    }
}