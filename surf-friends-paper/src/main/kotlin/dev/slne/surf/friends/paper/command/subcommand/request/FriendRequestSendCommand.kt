package dev.slne.surf.friends.paper.command.subcommand.request

import dev.jorel.commandapi.CommandAPICommand
import dev.slne.surf.api.core.command.args.awaitingOrNull
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.clickRunsCommand
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.paper.util.FriendPermissionRegistry

class FriendRequestSendCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(FriendPermissionRegistry.COMMAND_FRIEND_REQUEST_SEND)
        surfOfflinePlayerArgument("target")
        playerExecutorSuspend { player, args ->
            val target = args.awaitingOrNull<SurfPlayer?>("target")

            if (target == null) {
                player.sendText {
                    appendErrorPrefix()
                    error("Der angegebene Spieler wurde nicht gefunden.")
                }
                return@playerExecutorSuspend
            }

            if (player.uniqueId == target.uuid) {
                player.sendText {
                    appendErrorPrefix()
                    error("Du kannst dir keine Freundschaftsanfrage selbst senden.")
                }
                return@playerExecutorSuspend
            }

            val playerFriendsPlayer = FriendsPlayer[player.uniqueId]
            val targetFriendsPlayer = FriendsPlayer[target.uuid]

            if (playerFriendsPlayer.hasFriendship(targetFriendsPlayer)) {
                player.sendText {
                    appendErrorPrefix()
                    error("Du bist bereits mit ")
                    append(target.displayName())
                    error(" befreundet.")
                }
                return@playerExecutorSuspend
            }

            if (playerFriendsPlayer.hasSentFriendRequest(targetFriendsPlayer)) {
                player.sendText {
                    appendErrorPrefix()
                    error("Du hast bereits eine Freundschaftsanfrage an ")
                    append(target.displayName())
                    error(" gesendet.")
                }
                return@playerExecutorSuspend
            }

            if (playerFriendsPlayer.hasReceivedFriendRequest(targetFriendsPlayer)) {
                player.sendText {
                    appendErrorPrefix()
                    error("Du hast bereits eine Freundschaftsanfrage von ")
                    append(target.displayName())
                    error(" erhalten. Möchtest du diese annehmen?")
                    append {
                        clickRunsCommand("/friend accept ${target.username}")
                        spacer(" [")
                        info("Akzeptieren".toSmallCaps())
                        spacer("]")
                    }
                }
                return@playerExecutorSuspend
            }

            playerFriendsPlayer.sendFriendRequest(targetFriendsPlayer)
        }
    }
}

