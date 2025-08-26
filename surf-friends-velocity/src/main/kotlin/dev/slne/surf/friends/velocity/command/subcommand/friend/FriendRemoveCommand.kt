package dev.slne.surf.friends.velocity.command.subcommand.friend

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

class FriendRemoveCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        playerStringArgument("target")
        withPermission(FriendPermissionRegistry.COMMAND_FRIEND_REMOVE)
        playerExecutor { player, args ->
            container.launch {
                val target: String by args
                val targetUuid = PlayerLookupService.getUuid(target) ?: return@launch run {
                    player.uniqueId.sendText {
                        error("Der Spieler $target wurde nicht gefunden.")
                    }
                }

                val friendShip = friendService.getFriendship(player.uniqueId, targetUuid)

                if(friendShip == null) {
                    player.uniqueId.sendText {
                        error("Du bist nicht mit $target befreundet.")
                    }
                    return@launch
                }

                friendService.removeFriendship(player.uniqueId, targetUuid)

                player.uniqueId.sendText {
                    success("Du hast die Freundschaft mit ")
                    variableValue(target)
                    success(" beendet.")
                }

                targetUuid.sendText {
                    info("Die Freundschaft mit ")
                    variableValue(player.username)
                    info(" wurde beendet.")
                }
            }
        }
    }
}