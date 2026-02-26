package dev.slne.surf.friends.paper.command.argument.request

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.friends.core.service.friendPlayerService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.uuid

class ReceivedFriendRequestArgument(nodeName: String) :
    CustomArgument<FriendRequest, String>(StringArgument(nodeName), { info ->
        val senderPlayer = friendPlayerService.players.first { it.uuid == info.sender.uuid() }

        senderPlayer.receivedFriendRequests.firstOrNull { it.senderName == info.input }
            ?: throw CustomArgumentException.fromAdventureComponent(
                buildText {
                    appendErrorPrefix()
                    error("Du hast keine Freundschaftsanfrage von diesem Spieler erhalten.")
                })
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection { sender ->
                friendPlayerService.players.first { it.uuid == sender.sender.uuid() }.receivedFriendRequests.map { it.senderName }
            }
        )
    }
}

inline fun CommandTree.receivedFriendRequestArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    ReceivedFriendRequestArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.receivedFriendRequestArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    ReceivedFriendRequestArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandAPICommand.receivedFriendRequestArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(ReceivedFriendRequestArgument(nodeName).setOptional(optional).apply(block))