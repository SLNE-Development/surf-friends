package dev.slne.surf.friends.paper.command.argument.request

import com.github.shynixn.mccoroutine.folia.scope
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.api.core.messages.adventure.uuid
import dev.slne.surf.api.core.messages.adventure.uuidOrNull
import dev.slne.surf.api.paper.command.args.SuspendCustomArgument
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.core.client.command.NO_RECEIVED_FRIEND_REQUEST_MESSAGE
import dev.slne.surf.friends.core.client.command.findReceivedFriendRequestSender
import dev.slne.surf.friends.core.client.command.receivedFriendRequestNames
import dev.slne.surf.friends.paper.plugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future

class ReceivedFriendRequestArgument(nodeName: String) :
    SuspendCustomArgument<FriendsPlayer, String>(StringArgument(nodeName)) {

    override suspend fun CoroutineScope.parse(info: CustomArgumentInfo<String>): FriendsPlayer {
        return findReceivedFriendRequestSender(info.sender.uuid(), info.currentInput)
            ?: throw CustomArgumentException.fromAdventureComponent(
                NO_RECEIVED_FRIEND_REQUEST_MESSAGE
            )
    }

    init {
        replaceSuggestions(
            ArgumentSuggestions.stringsAsync { info ->
                plugin.scope.future {
                    val uuid = info.sender.uuidOrNull() ?: return@future arrayOf()

                    receivedFriendRequestNames(uuid).toTypedArray()
                }
            }
        )
    }
}

fun CommandAPICommand.receivedFriendRequestArgument(
    nodeName: String
): CommandAPICommand = withArguments(ReceivedFriendRequestArgument(nodeName))
