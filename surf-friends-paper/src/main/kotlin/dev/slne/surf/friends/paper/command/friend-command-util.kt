package dev.slne.surf.friends.paper.command

import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.friends.api.friend.Friendship
import dev.slne.surf.friends.api.friend.friendRequest
import dev.slne.surf.friends.api.friend.friendship
import dev.slne.surf.friends.core.loader.redisApi
import dev.slne.surf.friends.core.service.friendRequestService
import dev.slne.surf.friends.core.service.friendShipService
import dev.slne.surf.friends.paper.redis.event.FriendNotifyRedisEvent
import dev.slne.surf.friends.paper.redis.event.FriendRequestNotifyRedisEvent
import dev.slne.surf.friends.paper.util.friendPlayer
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickRunsCommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.Player
import java.util.*

suspend fun addFriend(player: Player, target: SurfPlayer?, rawTarget: String) {
    if (target == null) {
        player.sendText {
            appendErrorPrefix()
            error("Der Spieler wurde nicht gefunden.")
        }
        return
    }

    if (target.uuid == player.uniqueId) {
        player.sendText {
            appendErrorPrefix()
            error("Du kannst dich nicht selbst als Freund hinzufügen.")
        }
        return
    }

    val friendPlayer = player.friendPlayer

    if (friendPlayer.hasSentFriendRequest(target.uuid)) {
        player.sendText {
            appendErrorPrefix()
            error("Du hast diesem Spieler bereits eine Freundschaftsanfrage gesendet.")
        }
        return
    }

    if (friendPlayer.hasFriend(target.uuid)) {
        player.sendText {
            appendErrorPrefix()
            error("Du bist bereits mit diesem Spieler befreundet.")
        }
        return
    }

    val existingRequest = friendPlayer.receivedFriendRequests.find { it.senderUuid == target.uuid }

    if (existingRequest != null) {
        acceptFriend(player, existingRequest)
        return
    }

    val friendRequest = friendRequest(
        senderUuid = player.uniqueId,
        receiverUuid = target.uuid,
        senderName = player.name,
        receiverName = target.lastKnownName ?: rawTarget
    )

    friendRequestService.saveFriendRequest(friendRequest)

    player.sendText {
        appendSuccessPrefix()
        success("Du hast eine Freundschaftsanfrage an ")
        variableValue(target.lastKnownName ?: rawTarget)
        success(" gesendet.")
    }

    redisApi.publishEvent(
        FriendRequestNotifyRedisEvent(
            playerUuid = target.uuid,
            buildText {
                appendInfoPrefix()
                info("Du hast eine Freundschaftsanfrage von ")
                variableValue(player.name)
                info(" erhalten.")
                appendSpace()
                append {
                    darkSpacer("[")
                    success("Annehmen")
                    darkSpacer("]")
                    clickRunsCommand("/friend accept ${player.name}")
                }
                appendSpace()
                append {
                    darkSpacer("[")
                    error("Ablehnen")
                    darkSpacer("]")
                    clickRunsCommand("/friend decline ${player.name}")
                }
            }
        ))
}

suspend fun acceptFriend(player: Player, receivedRequest: FriendRequest) {
    val friendPlayer = player.friendPlayer

    if (friendPlayer.hasFriend(receivedRequest.senderUuid)) {
        player.sendText {
            appendErrorPrefix()
            error("Du bist bereits mit diesem Spieler befreundet.")
        }
        return
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

    redisApi.publishEvent(
        FriendNotifyRedisEvent(
            playerUuid = receivedRequest.senderUuid,
            buildText {
                appendInfoPrefix()
                info("Deine Freundschaftsanfrage an ")
                variableValue(receivedRequest.receiverName)
                info(" wurde angenommen.")
            }
        ))
}

fun listFriends(player: Player) {
    val friendPlayer = player.friendPlayer

    if (friendPlayer.friends.isEmpty()) {
        player.sendText {
            appendInfoPrefix()
            info("Du hast keine Freunde.")
        }
        return
    }

    player.sendText {
        appendNewline()
        append(
            friendPagination(player.uniqueId).renderComponent(
                friendPlayer.friends
                    .sortedByDescending {
                        it.getOtherUuid(player.uniqueId).currentServerName() != null
                    }
                    .sortedBy { it.getOtherName(player.uniqueId) })
        )
    }
}

private fun friendPagination(ownUuid: UUID) = Pagination<Friendship> {
    title { primary("Deine Freunde".toSmallCaps(), TextDecoration.BOLD) }

    rowRenderer { friendship, _ ->
        listOf(buildText {
            spacer("-")
            appendSpace()
            variableValue(friendship.getOtherName(ownUuid))
            appendSpace()
            spacer("(")
            friendship.getOtherUuid(ownUuid).currentServerName().let {
                if (it != null) {
                    success("Online auf $it")
                } else {
                    error("Offline")
                }
            }
            spacer(")")
        })
    }
}

fun UUID.currentServerName() = surfCoreApi.getPlayer(this)?.currentServer?.displayName