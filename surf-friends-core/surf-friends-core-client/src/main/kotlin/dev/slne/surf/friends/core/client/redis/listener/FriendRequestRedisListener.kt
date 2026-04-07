package dev.slne.surf.friends.core.client.redis.listener

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.clickCallback
import dev.slne.surf.core.api.common.util.sendText
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.api.utils.toSurfPlayer
import dev.slne.surf.friends.core.client.FriendsClientInstance
import dev.slne.surf.friends.core.client.redis.event.FriendRequestAcceptRedisEvent
import dev.slne.surf.friends.core.client.redis.event.FriendRequestDenyRedisEvent
import dev.slne.surf.friends.core.client.redis.event.FriendRequestRevokeRedisEvent
import dev.slne.surf.friends.core.client.redis.event.FriendRequestSendRedisEvent
import dev.slne.surf.redis.event.OnRedisEvent
import net.kyori.adventure.text.event.HoverEvent

object FriendRequestRedisListener {
    @OnRedisEvent
    fun onFriendRequestAccept(
        event: FriendRequestAcceptRedisEvent
    ) = FriendsClientInstance.INSTANCE.launch {
        val executor = event.executorUuid.toSurfPlayer()
        val target = event.targetUuid.toSurfPlayer()

        target?.sendText {
            appendInfoPrefix()

            append(executor.displayName())
            info(" hat deine Freundschaftsanfrage angenommen.")
        }

        executor?.sendText {
            appendSuccessPrefix()

            success("Du hast die Freundschaftsanfrage von ")
            append(target.displayName())
            success(" angenommen.")
        }
    }

    @OnRedisEvent
    fun onFriendRequestDeny(
        event: FriendRequestDenyRedisEvent
    ) = FriendsClientInstance.INSTANCE.launch {
        val executor = event.executorUuid.toSurfPlayer()
        val target = event.targetUuid.toSurfPlayer()

        target?.sendText {
            appendInfoPrefix()

            append(executor.displayName())
            info(" hat deine Freundschaftsanfrage abgelehnt.")
        }

        executor?.sendText {
            appendSuccessPrefix()

            success("Du hast die Freundschaftsanfrage von ")
            append(target.displayName())
            success(" abgelehnt.")
        }
    }

    @OnRedisEvent
    fun onFriendRequestRevoke(
        event: FriendRequestRevokeRedisEvent
    ) = FriendsClientInstance.INSTANCE.launch {
        val executor = event.executorUuid.toSurfPlayer()
        val target = event.targetUuid.toSurfPlayer()

        if (event.notifyTarget) {
            target?.sendText {
                appendInfoPrefix()

                info("Die Freundschaftsanfrage von ")
                append(executor.displayName())
                info(" wurde zurückgezogen.")
            }
        }

        executor?.sendText {
            appendSuccessPrefix()

            success("Du hast die Freundschaftsanfrage an ")
            append(target.displayName())
            success(" zurückgezogen.")
        }
    }

    @OnRedisEvent
    fun onFriendRequestSend(
        event: FriendRequestSendRedisEvent
    ) = FriendsClientInstance.INSTANCE.launch {
        val executor = event.executorUuid.toSurfPlayer()
        val target = event.targetUuid.toSurfPlayer()

        val executorFriendsPlayer = FriendsPlayer[event.executorUuid]
        val targetFriendPlayer = FriendsPlayer[event.targetUuid]

        if (event.notifyTarget) {
            target?.sendText {
                appendInfoPrefix()

                info("Du hast eine Freundschaftsanfrage von ")
                append(executor.displayName())
                info(" erhalten.")
                appendSpace()
                append {
                    success("[✔]")
                    hoverEvent(HoverEvent.showText(buildText {
                        spacer("Klicke, um die Freundschaftsanfrage von ")
                        append(executor.displayName())
                        spacer(" anzunehmen.")
                    }))
                    clickCallback {
                        FriendsClientInstance.INSTANCE.launch {
                            targetFriendPlayer.acceptFriendRequest(executorFriendsPlayer)
                        }
                    }
                }
                append {
                    error("[❌]")
                    hoverEvent(HoverEvent.showText(buildText {
                        spacer("Klicke, um die Freundschaftsanfrage von ")
                        append(executor.displayName())
                        spacer(" abzulehnen.")
                    }))
                    clickCallback {
                        FriendsClientInstance.INSTANCE.launch {
                            targetFriendPlayer.declineFriendRequest(executorFriendsPlayer)
                        }
                    }
                }
            }
        }

        executor?.sendText {
            appendSuccessPrefix()

            success("Du hast eine Freundschaftsanfrage an ")
            append(target.displayName())
            success(" gesendet.")
        }
    }
}