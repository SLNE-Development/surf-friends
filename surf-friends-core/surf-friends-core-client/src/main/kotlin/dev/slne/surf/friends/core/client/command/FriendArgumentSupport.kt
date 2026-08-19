package dev.slne.surf.friends.core.client.command

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.core.client.util.resolveUsername
import java.util.*

/**
 * The message shown when the named player is not a friend of the executor.
 */
val NOT_FRIENDS_MESSAGE = buildText {
    appendErrorPrefix()
    error("Du bist nicht mit diesem Spieler befreundet.")
}

/**
 * The message shown when the named player has not sent a friend request to the executor.
 */
val NO_RECEIVED_FRIEND_REQUEST_MESSAGE = buildText {
    appendErrorPrefix()
    error("Du hast keine Freundschaftsanfrage von diesem Spieler erhalten.")
}

/**
 * The message shown when the executor has not sent a friend request to the named player.
 */
val NO_SENT_FRIEND_REQUEST_MESSAGE = buildText {
    appendErrorPrefix()
    error("Du hast keine Freundschaftsanfrage an diesen Spieler gesendet.")
}

/**
 * The friend of [player] named [input], matched regardless of case.
 */
suspend fun findFriend(player: UUID, input: String): FriendsPlayer? {
    val friendship = FriendsPlayer[player].friendships.firstOrNull { friendship ->
        resolveUsername(friendship.friendUuid).equals(input, ignoreCase = true)
    } ?: return null

    return FriendsPlayer[friendship.friendUuid]
}

/**
 * The names of the friends of [player].
 */
suspend fun friendNames(player: UUID): List<String> =
    FriendsPlayer[player].friendships.mapNotNull { friendship ->
        resolveUsername(friendship.friendUuid)
    }

/**
 * The player named [input] who has sent [player] a friend request.
 */
suspend fun findReceivedFriendRequestSender(player: UUID, input: String): FriendsPlayer? {
    val request = FriendsPlayer[player].receivedFriendRequests.firstOrNull { request ->
        resolveUsername(request.senderUuid) == input
    } ?: return null

    return FriendsPlayer[request.senderUuid]
}

/**
 * The names of the players who have sent [player] a friend request.
 */
suspend fun receivedFriendRequestNames(player: UUID): List<String> =
    FriendsPlayer[player].receivedFriendRequests.mapNotNull { request ->
        resolveUsername(request.senderUuid)
    }

/**
 * The player named [input] whom [player] has sent a friend request to.
 */
suspend fun findSentFriendRequestTarget(player: UUID, input: String): FriendsPlayer? {
    val request = FriendsPlayer[player].sentFriendRequests.firstOrNull { request ->
        resolveUsername(request.targetUuid) == input
    } ?: return null

    return FriendsPlayer[request.targetUuid]
}

/**
 * The names of the players whom [player] has sent a friend request to.
 */
suspend fun sentFriendRequestNames(player: UUID): List<String> =
    FriendsPlayer[player].sentFriendRequests.mapNotNull { request ->
        resolveUsername(request.targetUuid)
    }
