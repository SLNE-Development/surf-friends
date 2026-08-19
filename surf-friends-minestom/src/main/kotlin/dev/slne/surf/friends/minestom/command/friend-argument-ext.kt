package dev.slne.surf.friends.minestom.command

import dev.slne.minestom.lobby.api.command.commandapi.CommandAPI
import dev.slne.minestom.lobby.api.command.commandapi.executor.CommandArguments
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.core.client.command.NOT_FRIENDS_MESSAGE
import dev.slne.surf.friends.core.client.command.NO_RECEIVED_FRIEND_REQUEST_MESSAGE
import dev.slne.surf.friends.core.client.command.NO_SENT_FRIEND_REQUEST_MESSAGE
import kotlinx.coroutines.Deferred

/**
 * The friend the [node] argument names.
 */
suspend fun CommandArguments.resolveFriend(node: String): FriendsPlayer =
    get<Deferred<FriendsPlayer?>>(node).await()
        ?: CommandAPI.failWithMessage(NOT_FRIENDS_MESSAGE)

/**
 * The player the [node] argument names, who has sent the executor a friend request.
 */
suspend fun CommandArguments.resolveReceivedFriendRequestSender(node: String): FriendsPlayer =
    get<Deferred<FriendsPlayer?>>(node).await()
        ?: CommandAPI.failWithMessage(NO_RECEIVED_FRIEND_REQUEST_MESSAGE)

/**
 * The player the [node] argument names, whom the executor has sent a friend request to.
 */
suspend fun CommandArguments.resolveSentFriendRequestTarget(node: String): FriendsPlayer =
    get<Deferred<FriendsPlayer?>>(node).await()
        ?: CommandAPI.failWithMessage(NO_SENT_FRIEND_REQUEST_MESSAGE)
