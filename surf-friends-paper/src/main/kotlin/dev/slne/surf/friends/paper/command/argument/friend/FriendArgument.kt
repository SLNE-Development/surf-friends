package dev.slne.surf.friends.paper.command.argument.friend

import com.github.shynixn.mccoroutine.folia.scope
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.api.core.messages.adventure.uuid
import dev.slne.surf.api.core.messages.adventure.uuidOrNull
import dev.slne.surf.api.paper.command.args.SuspendCustomArgument
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.core.client.command.NOT_FRIENDS_MESSAGE
import dev.slne.surf.friends.core.client.command.findFriend
import dev.slne.surf.friends.core.client.command.friendNames
import dev.slne.surf.friends.paper.plugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future

class FriendArgument(nodeName: String) :
    SuspendCustomArgument<FriendsPlayer, String>(StringArgument(nodeName)) {

    override suspend fun CoroutineScope.parse(info: CustomArgumentInfo<String>): FriendsPlayer {
        return findFriend(info.sender.uuid(), info.currentInput)
            ?: throw CustomArgumentException.fromAdventureComponent(NOT_FRIENDS_MESSAGE)
    }

    init {
        replaceSuggestions(
            ArgumentSuggestions.stringsAsync { info ->
                plugin.scope.future {
                    val uuid = info.sender.uuidOrNull() ?: return@future null

                    friendNames(uuid).toTypedArray()
                }
            }
        )
    }
}

fun CommandAPICommand.friendArgument(
    nodeName: String
): CommandAPICommand = withArguments(FriendArgument(nodeName))
