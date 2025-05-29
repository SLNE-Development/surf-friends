package dev.slne.surf.friends.velocity.command.subcommand.request

import com.github.shynixn.mccoroutine.velocity.launch

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.friends.core.service.databaseService

import dev.slne.surf.friends.core.service.friendService
import dev.slne.surf.friends.velocity.command.argument.playerStringArgument
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry
import dev.slne.surf.friends.velocity.util.sendText
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.clickRunsCommand
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService

class FriendRequestSendCommand(commandName: String): CommandAPICommand(commandName) {
    init {
        withPermission(FriendPermissionRegistry.COMMAND_FRIEND_REQUEST_SEND)
        playerStringArgument("target")
        playerExecutor { player, args ->
            container.launch {
                val target: String by args
                val targetUuid = PlayerLookupService.getUuid(target) ?: return@launch run {
                    player.uniqueId.sendText {
                        error("Der Spieler $target wurde nicht gefunden.")
                    }
                }

                if(player.uniqueId == targetUuid) {
                    player.uniqueId.sendText {
                        error("Du kannst dir keine Freundschaftsanfrage selbst senden.")
                    }
                    return@launch
                }

                val friendShip = friendService.getFriendship(player.uniqueId, targetUuid)

                if(friendShip != null) {
                    player.uniqueId.sendText {
                        error("Du bist bereits mit $target befreundet.")
                    }
                    return@launch
                }

                val friendRequest = friendService.getFriendRequest(player.uniqueId, targetUuid)
                val receivedFriendRequest = friendService.getFriendship(targetUuid, player.uniqueId)

                if(friendRequest != null) {
                    player.uniqueId.sendText {
                        error("Du hast bereits eine Freundschaftsanfrage an $target gesendet.")
                    }
                    return@launch
                }

                if(receivedFriendRequest != null) {
                    player.uniqueId.sendText {
                        error("Du hast bereits eine Freundschaftsanfrage von $target erhalten.")
                    }
                    return@launch
                }

                friendService.sendFriendRequest(player.uniqueId, targetUuid)

                player.uniqueId.sendText {
                    success("Du hast eine Freundschaftsanfrage an ")
                    variableValue(target)
                    success(" gesendet.")
                    append {
                        clickRunsCommand("/friend revoke $target")
                        spacer(" [")
                        info("Zurückziehen".toSmallCaps())
                        spacer("]")
                    }
                }

                val targetSettings = databaseService.getFriendSettings(targetUuid)

                if(targetSettings.announcementsEnabled) {
                    targetUuid.sendText {
                        info("Du hast eine Freundschaftsanfrage von ")
                        variableValue(player.username)
                        info(" erhalten.")
                        append {
                            clickRunsCommand("/friend accept ${player.username}")
                            spacer(" [")
                            info("Akzeptieren".toSmallCaps())
                            spacer("]")
                        }
                        append {
                            clickRunsCommand("/friend decline ${player.username}")
                            spacer(" [")
                            info("Ablehnen".toSmallCaps())
                            spacer("]")
                        }
                    }
                }
            }
        }
    }
}