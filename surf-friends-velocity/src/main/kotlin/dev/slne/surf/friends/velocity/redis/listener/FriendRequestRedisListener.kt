package dev.slne.surf.friends.velocity.redis.listener

import dev.slne.surf.friends.velocity.plugin
import dev.slne.surf.friends.velocity.redis.event.FriendRequestAcceptRedisEvent
import dev.slne.surf.friends.velocity.redis.event.FriendRequestDenyRedisEvent
import dev.slne.surf.friends.velocity.redis.event.FriendRequestRevokeRedisEvent
import dev.slne.surf.friends.velocity.redis.event.FriendRequestSendRedisEvent
import dev.slne.surf.redis.event.OnRedisEvent
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.clickRunsCommand
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlin.jvm.optionals.getOrNull

object FriendRequestRedisListener {
    @OnRedisEvent
    fun onFriendRequestAccept(event: FriendRequestAcceptRedisEvent) {
        val player = plugin.proxy.getPlayer(event.requester).getOrNull() ?: return

        player.sendText {
            appendInfoPrefix()
            info("Du bist nun mit ")
            variableValue(event.targetName)
            info(" befreundet.")
        }
    }

    @OnRedisEvent
    fun onFriendRequestDeny(event: FriendRequestDenyRedisEvent) {
        val player = plugin.proxy.getPlayer(event.requester).getOrNull() ?: return

        player.sendText {
            appendInfoPrefix()
            info("Die Freundschaftsanfrage an ")
            variableValue(event.targetName)
            info(" wurde abgelehnt.")
        }
    }

    @OnRedisEvent
    fun onFriendRequestRevoke(event: FriendRequestRevokeRedisEvent) {
        val player = plugin.proxy.getPlayer(event.target).getOrNull() ?: return

        player.sendText {
            appendInfoPrefix()
            info("Die Freundschaftsanfrage von ")
            variableValue(event.requesterName)
            info(" wurde zurückgezogen.")
        }
    }

    @OnRedisEvent
    fun onFriendRequestSend(event: FriendRequestSendRedisEvent) {
        val player = plugin.proxy.getPlayer(event.target).getOrNull() ?: return

        if (!event.announce) {
            return
        }

        player.sendText {
            appendInfoPrefix()
            info("Du hast eine Freundschaftsanfrage von ")
            variableValue(event.requesterName)
            info(" erhalten.")
            appendSpace()
            append {
                clickRunsCommand("/friend accept ${event.requesterName}")
                spacer("[")
                text("Akzeptieren", Colors.GREEN)
                spacer("]")
            }
            appendSpace()
            append {
                clickRunsCommand("/friend decline ${event.requesterName}")
                spacer("[")
                text("Ablehnen", Colors.RED)
                spacer("]")
            }
        }
    }
}