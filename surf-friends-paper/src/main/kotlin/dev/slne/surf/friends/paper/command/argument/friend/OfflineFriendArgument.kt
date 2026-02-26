package dev.slne.surf.friends.paper.command.argument.friend

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.friends.core.service.friendPlayerService
import dev.slne.surf.surfapi.core.api.messages.adventure.uuid
import dev.slne.surf.surfapi.core.api.util.logger
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.future.future

class OfflineFriendArgument(nodeName: String) :
    CustomArgument<Deferred<FriendPlayer?>, String>(StringArgument(nodeName), { info ->
        scope.future {
            friendPlayerService.findOrLoadPlayer(info.input)
        }.asDeferred()
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection { info ->
                friendPlayerService.players.find { it.uuid == info.sender.uuid() }?.friends?.map { it.acceptorName }
            }
        )
    }

    companion object {
        private val log = logger()
        private val scope =
            CoroutineScope(Dispatchers.IO + CoroutineName("OfflineFriendArgument") + CoroutineExceptionHandler { _, throwable ->
                log.atWarning()
                    .withCause(throwable)
                    .log("An error occurred in OfflineFriendArgument")
            })
    }
}

inline fun CommandTree.offlineFriendArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    OfflineFriendArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.offlineFriendArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    OfflineFriendArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandAPICommand.offlineFriendArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(OfflineFriendArgument(nodeName).setOptional(optional).apply(block))