package dev.slne.surf.friends.paper.command.argument.friend

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.common.surfCoreApi
import dev.slne.surf.friends.core.service.friendPlayerService
import dev.slne.surf.surfapi.core.api.messages.adventure.uuid
import dev.slne.surf.surfapi.core.api.util.logger
import kotlinx.coroutines.*
import kotlinx.coroutines.future.asDeferred
import kotlinx.coroutines.future.future

class NonFriendOfflinePlayerArgument(nodeName: String) :
    CustomArgument<Deferred<SurfPlayer?>, String>(StringArgument(nodeName), { info ->
        scope.future {
            surfCoreApi.getOfflinePlayer(info.input)
        }.asDeferred()
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection { info ->
                val friendPlayer =
                    friendPlayerService.players.find { it.uuid == info.sender.uuid() }
                        ?: return@stringCollection null
                val friendUuids = friendPlayer.friends
                    .map { it.getOtherUuid(friendPlayer.uuid) }
                    .toSet()

                surfCoreApi.getOnlinePlayers()
                    .filterNot { it.uuid == friendPlayer.uuid }
                    .filterNot { it.uuid in friendUuids }
                    .mapNotNull { it.lastKnownName }
            }
        )
    }

    companion object {
        private val log = logger()
        private val scope =
            CoroutineScope(Dispatchers.IO + CoroutineName("NonFriendOfflinePlayerArgument") + CoroutineExceptionHandler { _, throwable ->
                log.atWarning()
                    .withCause(throwable)
                    .log("An error occurred in NonFriendOfflinePlayerArgument")
            })
    }
}

inline fun CommandTree.nonFriendOfflinePlayerArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    NonFriendOfflinePlayerArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.nonFriendOfflinePlayerArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    NonFriendOfflinePlayerArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandAPICommand.nonFriendOfflinePlayerArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand =
    withArguments(NonFriendOfflinePlayerArgument(nodeName).setOptional(optional).apply(block))