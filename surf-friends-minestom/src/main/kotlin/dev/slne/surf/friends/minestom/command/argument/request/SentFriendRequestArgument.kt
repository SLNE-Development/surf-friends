package dev.slne.surf.friends.minestom.command.argument.request

import dev.slne.minestom.lobby.api.command.commandapi.CommandAPICommand
import dev.slne.minestom.lobby.api.command.commandapi.argument.Argument
import dev.slne.minestom.lobby.api.command.commandapi.argument.CustomArgument
import dev.slne.minestom.lobby.api.command.commandapi.argument.StringArgument
import dev.slne.minestom.lobby.api.command.commandapi.suggestion.ArgumentSuggestions
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.core.client.command.findSentFriendRequestTarget
import dev.slne.surf.friends.core.client.command.sentFriendRequestNames
import dev.slne.surf.friends.minestom.command.friendArgumentScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import net.minestom.server.entity.Player

class SentFriendRequestArgument(nodeName: String) :
    CustomArgument<Deferred<FriendsPlayer?>, String>(StringArgument(nodeName), { info ->
        val senderUuid = (info.sender as? Player)?.uuid
        val input = info.currentInput

        friendArgumentScope.async { senderUuid?.let { findSentFriendRequestTarget(it, input) } }
    }) {

    init {
        replaceSuggestions(ArgumentSuggestions.stringCollectionAsync { info ->
            val player = info.sender as? Player ?: return@stringCollectionAsync emptyList()

            sentFriendRequestNames(player.uuid)
        })
    }
}

inline fun CommandAPICommand.sentFriendRequestArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<Deferred<FriendsPlayer?>>.() -> Unit = {}
): CommandAPICommand =
    withArguments(SentFriendRequestArgument(nodeName).setOptional(optional).apply(block))
