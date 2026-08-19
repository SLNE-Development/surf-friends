package dev.slne.surf.friends.core.client.connection

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.clickRunsCommand
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.core.client.platform.FriendsPlatform
import java.util.*

/**
 * Announces players joining and leaving this server to their friends.
 */
object FriendConnectionNotifier {

    /**
     * Greets [uuid] with how many of their friends are on this server and how many friend requests
     * are still open, and returns the friends that were counted.
     */
    fun greet(uuid: UUID): List<UUID> {
        val friendPlayer = FriendsPlayer[uuid]

        val friendRequests = friendPlayer.receivedFriendRequests
        val onlineFriends = friendsOnThisServer(friendPlayer)

        if (onlineFriends.isNotEmpty()) {
            FriendsPlatform.INSTANCE.sendMessage(uuid, buildText {
                appendInfoPrefix()
                info("Aktuell sind ")
                variableValue(onlineFriends.size)
                info(" deiner Freunde online. ")

                append {
                    clickRunsCommand("/friend list")
                    info("[Ansehen]".toSmallCaps())
                    hoverEvent(buildText {
                        info("Klicke hier, um deine Freunde anzusehen.")
                    })
                }
            })
        }

        if (friendRequests.isNotEmpty()) {
            FriendsPlatform.INSTANCE.sendMessage(uuid, buildText {
                appendInfoPrefix()
                info("Du hast noch ")
                variableValue(friendRequests.size)
                info(" Freundschaftsanfragen offen. ")

                append {
                    clickRunsCommand("/friend requests")
                    info("[Ansehen]".toSmallCaps())
                    hoverEvent(buildText {
                        info("Klicke hier, um deine Freundschaftsanfragen anzusehen.")
                    })
                }
            })
        }

        return onlineFriends
    }

    /**
     * Tells [friendUuids] that [uuid] came online.
     */
    suspend fun announceOnline(uuid: UUID, friendUuids: List<UUID>) =
        announce(uuid, friendUuids, " ist nun online.")

    /**
     * Tells the friends of [uuid] on this server that they went offline.
     */
    suspend fun announceOffline(uuid: UUID) =
        announce(uuid, friendsOnThisServer(FriendsPlayer[uuid]), " ist nun offline.")

    private suspend fun announce(uuid: UUID, friendUuids: List<UUID>, message: String) {
        if (friendUuids.isEmpty()) return

        val displayName = FriendsPlayer[uuid].surfPlayer().displayName()

        friendUuids.forEach { friendUuid ->
            if (!FriendsPlayer[friendUuid].notificationsEnabled) return@forEach

            FriendsPlatform.INSTANCE.sendMessage(friendUuid, buildText {
                appendInfoPrefix()
                append(displayName)
                info(message)
            })
        }
    }

    private fun friendsOnThisServer(friendPlayer: FriendsPlayer) =
        friendPlayer.onlineFriendUuids.filter { FriendsPlatform.INSTANCE.isOnline(it) }
}
