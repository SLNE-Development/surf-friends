package dev.slne.surf.friends.paper.command.argument.friend

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.friends.core.service.friendPlayerService
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.uuid

class FriendPlayerArgument(nodeName: String) :
    CustomArgument<FriendPlayer, String>(StringArgument(nodeName), { info ->
        friendPlayerService.players.firstOrNull { it.name == info.input }
            ?: throw CustomArgumentException.fromAdventureComponent(
                buildText {
                    appendErrorPrefix()
                    error("Der Spieler wurde nicht gefunden.")
                })
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection { sender ->
                friendPlayerService.players.find { it.uuid == sender.sender.uuid() }?.friends?.map {
                    it.getOtherName(
                        sender.sender.uuid()
                    )
                }
            }
        )
    }
}

inline fun CommandTree.friendPlayerArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    FriendPlayerArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.friendPlayerArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    FriendPlayerArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandAPICommand.friendPlayerArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(FriendPlayerArgument(nodeName).setOptional(optional).apply(block))