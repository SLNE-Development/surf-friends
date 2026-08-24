package dev.slne.surf.friends.core.client.connection

import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.clickRunsCommand
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.core.client.FriendsClientInstance
import dev.slne.surf.friends.core.client.platform.FriendsPlatform
import it.unimi.dsi.fastutil.objects.ObjectArrayList
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
        val openFriendRequests = countReceivedFriendRequests(uuid)
        val onlineFriends = friendsOnThisServer(uuid)

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

        if (openFriendRequests > 0) {
            FriendsPlatform.INSTANCE.sendMessage(uuid, buildText {
                appendInfoPrefix()
                info("Du hast noch ")
                variableValue(openFriendRequests)
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
        announce(uuid, friendsOnThisServer(uuid), " ist nun offline.")

    private suspend fun announce(uuid: UUID, friendUuids: List<UUID>, message: String) {
        if (friendUuids.isEmpty()) return

        val displayName = FriendsPlayer[uuid].surfPlayer().displayName()

        val announcement = buildText {
            appendInfoPrefix()
            append(displayName)
            info(message)
        }
        val platform = FriendsPlatform.INSTANCE

        friendUuids.forEach { friendUuid ->
            if (!FriendsPlayer[friendUuid].notificationsEnabled) return@forEach

            platform.sendMessage(friendUuid, announcement)
        }
    }

    /**
     * How many friend requests [uuid] has received.
     */
    private fun countReceivedFriendRequests(uuid: UUID): Int {
        val snapshot = FriendsClientInstance.INSTANCE.friendRequests.snapshot()
        var count = 0

        for (index in snapshot.indices) {
            if (snapshot[index].targetUuid == uuid) count++
        }

        return count
    }

    /**
     * The friends of [uuid] that are connected to this server.
     */
    private fun friendsOnThisServer(uuid: UUID): List<UUID> {
        val snapshot = FriendsClientInstance.INSTANCE.friendships.snapshot()
        val platform = FriendsPlatform.INSTANCE
        val online = ObjectArrayList<UUID>()

        for (index in snapshot.indices) {
            val friendship = snapshot[index]
            if (friendship.playerUuid != uuid) continue
            if (!platform.isOnline(friendship.friendUuid)) continue

            online.add(friendship.friendUuid)
        }

        return online
    }
}
