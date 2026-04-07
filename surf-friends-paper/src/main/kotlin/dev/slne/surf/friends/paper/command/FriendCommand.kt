package dev.slne.surf.friends.paper.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.friends.core.loader.redisApi
import dev.slne.surf.friends.core.service.friendRequestService
import dev.slne.surf.friends.core.service.friendShipService
import dev.slne.surf.friends.paper.command.argument.friend.nonFriendOfflinePlayerArgument
import dev.slne.surf.friends.paper.command.argument.friend.offlineFriendArgument
import dev.slne.surf.friends.paper.command.argument.request.receivedFriendRequestArgument
import dev.slne.surf.friends.paper.command.argument.request.sentFriendRequestArgument
import dev.slne.surf.friends.paper.permission.PermissionRegistry
import dev.slne.surf.friends.paper.redis.event.FriendNotifyRedisEvent
import dev.slne.surf.friends.paper.util.friendPlayer
import dev.slne.surf.surfapi.bukkit.api.command.executors.playerExecutorSuspend
import dev.slne.surf.surfapi.core.api.command.args.awaiting
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration

fun friendListCommand() = commandTree("fl") {
    withPermission(PermissionRegistry.PREFIX_COMMAND)

    playerExecutor { player, _ ->
        listFriends(player)
    }
}

fun friendAddCommand() = commandTree("fa") {
    withPermission(PermissionRegistry.PREFIX_COMMAND)

    nonFriendOfflinePlayerArgument("target") {
        playerExecutorSuspend { player, args ->
            val target = args.awaiting<SurfPlayer?>("target")

            addFriend(player, target, args.getRaw("target") ?: "#Unknown")
        }
    }
}

fun friendCommand() = commandTree("friend") {
    withPermission(PermissionRegistry.PREFIX_COMMAND)

    literalArgument("add") {
        nonFriendOfflinePlayerArgument("target") {
            playerExecutorSuspend { player, args ->
                val target = args.awaiting<SurfPlayer?>("target")

                addFriend(player, target, args.getRaw("target") ?: "#Unknown")
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

                redisApi.publishEvent(
                    FriendNotifyRedisEvent(
                        playerUuid = friend.uuid,
                        buildText {
                            appendInfoPrefix()
                            info("Die Freundschaft mit ")
                            variableValue(player.name)
                            info(" wurde beendet.")
                        }
                    ))
            }
        }
    }

    literalArgument("list") {
        playerExecutor { player, _ ->
            listFriends(player)
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
                acceptFriend(player, receivedRequest)
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

                redisApi.publishEvent(
                    FriendNotifyRedisEvent(
                        playerUuid = receivedRequest.senderUuid,
                        buildText {
                            appendInfoPrefix()
                            info("Deine Freundschaftsanfrage an ")
                            variableValue(receivedRequest.receiverName)
                            info(" wurde abgelehnt.")
                        }
                    ))
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

                redisApi.publishEvent(
                    FriendNotifyRedisEvent(
                        playerUuid = sentRequest.receiverUuid,
                        buildText {
                            appendInfoPrefix()
                            info("Die Freundschaftsanfrage von ")
                            variableValue(player.name)
                            info(" wurde zurückgezogen.")
                        }
                    ))
            }
        }
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