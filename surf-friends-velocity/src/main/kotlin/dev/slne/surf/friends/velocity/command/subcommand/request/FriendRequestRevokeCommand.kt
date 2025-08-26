package dev.slne.surf.friends.velocity.command.subcommand.request

import com.github.shynixn.mccoroutine.velocity.launch

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.friends.core.service.friendService
import dev.slne.surf.friends.velocity.command.argument.playerStringArgument

import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry
import dev.slne.surf.friends.velocity.util.sendText
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService

class FriendRequestRevokeCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission(FriendPermissionRegistry.COMMAND_FRIEND_REQUEST_REVOKE)
        playerStringArgument("target")
        playerExecutor { player, args ->
            container.launch {
                val target: String by args
                val targetUuid = PlayerLookupService.getUuid(target) ?: return@launch run {
                    player.uniqueId.sendText {
                        error("Der Spieler $target wurde nicht gefunden.")
                    }
                }

                val friendRequest = friendService.getFriendRequest(player.uniqueId, targetUuid)

                if(friendRequest == null) {
                    player.uniqueId.sendText {
                        error("Du hast keine Freundschaftsanfrage an $target gesendet.")
                    }
                    return@launch
                }

                friendService.revokeFriendRequest(player.uniqueId, targetUuid)

                player.uniqueId.sendText {
                    success("Du hast die Freundschaftsanfrage an ")
                    variableValue(target)
                    success(" zurückgezogen.")
                }

                targetUuid.sendText {
                    info("Die Freundschaftsanfrage von ")
                    variableValue(player.username)
                    info(" wurde zurückgezogen.")
                }
            }
        }
    }
}