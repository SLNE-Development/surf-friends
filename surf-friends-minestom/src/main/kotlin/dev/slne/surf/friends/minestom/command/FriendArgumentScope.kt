package dev.slne.surf.friends.minestom.command

import dev.slne.surf.api.core.util.logger
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

private val log = logger()

internal val friendArgumentScope = CoroutineScope(
    Dispatchers.Default +
            CoroutineName("FriendArguments") +
            CoroutineExceptionHandler { _, throwable ->
                log.atWarning()
                    .withCause(throwable)
                    .log("An error occurred while resolving a friend command argument")
            }
)
