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

class SentFriendRequestArgument(nodeName: String) :
    CustomArgument<FriendRequest, String>(StringArgument(nodeName), { info ->
        val senderPlayer =
            friendPlayerService.players.firstOrNull { it.uuid == info.sender.uuid() }
                ?: throw CustomArgumentException.fromAdventureComponent(
                    buildText {
                        appendErrorPrefix()
                        error("Dein Spielerprofil konnte nicht gefunden werden.")
                    })

        senderPlayer.sentFriendRequests.firstOrNull { it.receiverName == info.input }
            ?: throw CustomArgumentException.fromAdventureComponent(
                buildText {
                    appendErrorPrefix()
                    error("Du hast keine Freundschaftsanfrage an diesen Spieler gesendet.")
                })
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection { sender ->
                friendPlayerService.players.first { it.uuid == sender.sender.uuid() }.sentFriendRequests.map { it.receiverName }
            }
        )
    }
}

inline fun CommandTree.sentFriendRequestArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    SentFriendRequestArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.sentFriendRequestArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    SentFriendRequestArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandAPICommand.sentFriendRequestArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(SentFriendRequestArgument(nodeName).setOptional(optional).apply(block))