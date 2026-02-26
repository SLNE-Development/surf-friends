package dev.slne.surf.friends.paper.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.friends.api.friend.Friendship
import dev.slne.surf.friends.api.friend.friendRequest
import dev.slne.surf.friends.api.friend.friendship
import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.friends.core.service.friendRequestService
import dev.slne.surf.friends.core.service.friendShipService
import dev.slne.surf.friends.paper.command.argument.friend.offlineFriendArgument
import dev.slne.surf.friends.paper.command.argument.request.receivedFriendRequestArgument
import dev.slne.surf.friends.paper.command.argument.request.sentFriendRequestArgument
import dev.slne.surf.friends.paper.permission.PermissionRegistry
import dev.slne.surf.friends.paper.util.friendPlayer
import dev.slne.surf.surfapi.bukkit.api.command.executors.playerExecutorSuspend
import dev.slne.surf.surfapi.core.api.command.args.awaiting
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration
import java.util.*

fun friendCommand() = commandTree("friend") {
    withPermission(PermissionRegistry.PREFIX_COMMAND)

    literalArgument("add") {
        surfOfflinePlayerArgument("target") { // TODO: Exclude self and online friends
            playerExecutorSuspend { player, args ->
                val target = args.awaiting<SurfPlayer?>("target")

                if (target == null) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Der Spieler wurde nicht gefunden.")
                    }
                    return@playerExecutorSuspend
                }

                if (target.uuid == player.uniqueId) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Du kannst dich nicht selbst als Freund hinzufügen.")
                    }
                    return@playerExecutorSuspend
                }

                val friendPlayer = player.friendPlayer

                if (friendPlayer.hasSentFriendRequest(target.uuid)) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Du hast diesem Spieler bereits eine Freundschaftsanfrage gesendet.")
                    }
                    return@playerExecutorSuspend
                }

                if (friendPlayer.hasFriend(target.uuid)) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Du bist bereits mit diesem Spieler befreundet.")
                    }
                    return@playerExecutorSuspend
                }

                if (friendPlayer.hasReceivedFriendRequest(target.uuid)) {
                    // TODO: Accept request
                    return@playerExecutorSuspend
                }

                val friendRequest = friendRequest(
                    senderUuid = player.uniqueId,
                    receiverUuid = target.uuid,
                    senderName = player.name,
                    receiverName = target.lastKnownName ?: args.getRaw("target")
                    ?: error("Player has no name and argument has no raw!")
                )

                friendRequestService.saveFriendRequest(friendRequest)

                player.sendText {
                    appendSuccessPrefix()
                    success("Du hast eine Freundschaftsanfrage an ")
                    variableValue(target.lastKnownName ?: args.getRaw("target") ?: "#Unbekannt")
                    success(" gesendet.")
                }

                // TODO: Notify target if online and if settings allow it
            }
        }
    }

    literalArgument("remove") {
        offlineFriendArgument("friend") {
            playerExecutorSuspend { player, args ->
                val friend = args.awaiting<FriendPlayer?>("friend")

                if (friend == null) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Der Spieler wurde nicht gefunden.")
                    }
                    return@playerExecutorSuspend
                }

                val friendPlayer = player.friendPlayer

                if (!friendPlayer.hasFriend(friend.uuid)) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Du bist mit diesem Spieler nicht befreundet.")
                    }
                    return@playerExecutorSuspend
                }

                val friendship =
                    friendPlayer.friends.firstOrNull { it.acceptedBy == friend.uuid || it.requestedBy == friend.uuid }

                if (friendship == null) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Du bist mit diesem Spieler nicht befreundet.")
                    }
                    return@playerExecutorSuspend
                }

                friendShipService.deleteFriendShip(friendship)

                player.sendText {
                    appendSuccessPrefix()
                    success("Du hast die Freundschaft mit ")
                    variableValue(friend.name)
                    success(" beendet.")
                }

                // TODO: Notify old friend if online and if settings allow it
            }
        }
    }

    literalArgument("list") {
        playerExecutor { player, _ ->
            val friendPlayer = player.friendPlayer

            if (friendPlayer.friends.isEmpty()) {
                player.sendText {
                    appendInfoPrefix()
                    info("Du hast keine Freunde.")
                }
                return@playerExecutor
            }

            player.sendText {
                appendNewline()
                append(friendPagination(player.uniqueId).renderComponent(friendPlayer.friends))
            }
        }
    }

    literalArgument("requests") {
        literalArgument("sent") {
            playerExecutor { player, _ ->
                val friendPlayer = player.friendPlayer

                if (friendPlayer.sentFriendRequests.isEmpty()) {
                    player.sendText {
                        appendInfoPrefix()
                        info("Du hast keine gesendeten Freundschaftsanfragen.")
                    }
                    return@playerExecutor
                }

                player.sendText {
                    appendNewline()
                    append(sendRequestsPagination.renderComponent(friendPlayer.sentFriendRequests))
                }
            }
        }

        literalArgument("received") {
            playerExecutor { player, _ ->
                val friendPlayer = player.friendPlayer

                if (friendPlayer.receivedFriendRequests.isEmpty()) {
                    player.sendText {
                        appendInfoPrefix()
                        info("Du hast keine empfangenen Freundschaftsanfragen.")
                    }
                    return@playerExecutor
                }

                player.sendText {
                    appendNewline()
                    append(receivedRequestsPagination.renderComponent(friendPlayer.receivedFriendRequests))
                }
            }
        }
    }

    literalArgument("accept") {
        receivedFriendRequestArgument("receivedRequest") {
            playerExecutorSuspend { player, args ->
                val receivedRequest: FriendRequest by args
                val friendPlayer = player.friendPlayer

                if (friendPlayer.hasFriend(receivedRequest.senderUuid)) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Du bist bereits mit diesem Spieler befreundet.")
                    }
                    return@playerExecutorSuspend
                }

                val friendship = friendship(
                    requestedBy = receivedRequest.senderUuid,
                    acceptedBy = receivedRequest.receiverUuid,
                    requesterName = receivedRequest.senderName,
                    acceptorName = receivedRequest.receiverName
                )

                friendRequestService.deleteFriendRequest(receivedRequest)
                friendShipService.saveFriendShip(friendship)

                player.sendText {
                    appendSuccessPrefix()
                    success("Du hast die Freundschaftsanfrage von ")
                    variableValue(receivedRequest.senderName)
                    success(" angenommen.")
                }

                // TODO: Notify sender if online and if settings allow it
            }
        }
    }

    literalArgument("decline") {
        receivedFriendRequestArgument("receivedRequest") {
            playerExecutorSuspend { player, args ->
                val receivedRequest: FriendRequest by args

                friendRequestService.deleteFriendRequest(receivedRequest)

                player.sendText {
                    appendSuccessPrefix()
                    success("Du hast die Freundschaftsanfrage von ")
                    variableValue(receivedRequest.senderName)
                    success(" abgelehnt.")
                }

                // TODO: Notify sender if online and if settings allow it
            }
        }
    }

    literalArgument("revoke") {
        sentFriendRequestArgument("sentRequest") {
            playerExecutorSuspend { player, args ->
                val sentRequest: FriendRequest by args

                friendRequestService.deleteFriendRequest(sentRequest)

                player.sendText {
                    appendSuccessPrefix()
                    success("Du hast die Freundschaftsanfrage an ")
                    variableValue(sentRequest.receiverName)
                    success(" zurückgezogen.")
                }

                // TODO: Notify receiver if online and if settings allow it
            }
        }
    }
}

private fun friendPagination(ownUuid: UUID) = Pagination<Friendship> {
    title { primary("Deine Freunde".toSmallCaps(), TextDecoration.BOLD) }

    rowRenderer { friendship, _ ->
        listOf(buildText {
            spacer("-")
            appendSpace()
            variableValue(friendship.getOtherName(ownUuid))

            // TODO: Display if online, if yes: where
        })
    }
}

private val sendRequestsPagination = Pagination<FriendRequest> {
    title { primary("Gesendete Anfragen".toSmallCaps(), TextDecoration.BOLD) }

    rowRenderer { friendRequest, _ ->
        listOf(buildText {
            spacer("-")
            appendSpace()
            variableValue(friendRequest.receiverName)
        })
    }
}

private val receivedRequestsPagination = Pagination<FriendRequest> {
    title { primary("Empfangene Anfragen".toSmallCaps(), TextDecoration.BOLD) }

    rowRenderer { friendRequest, _ ->
        listOf(buildText {
            spacer("-")
            appendSpace()
            variableValue(friendRequest.senderName)
        })
    }
}