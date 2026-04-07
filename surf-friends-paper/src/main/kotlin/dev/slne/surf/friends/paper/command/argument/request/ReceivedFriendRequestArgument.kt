package dev.slne.surf.friends.paper.command.argument.request

import com.github.shynixn.mccoroutine.folia.scope
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.uuid
import dev.slne.surf.api.core.messages.adventure.uuidOrNull
import dev.slne.surf.api.paper.command.args.SuspendCustomArgument
import dev.slne.surf.friends.api.player.FriendsPlayer
import dev.slne.surf.friends.paper.plugin
import dev.slne.surf.friends.paper.util.resolveUsername
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.future.future

class ReceivedFriendRequestArgument(nodeName: String) :
    SuspendCustomArgument<FriendsPlayer, String>(StringArgument(nodeName)) {

    override suspend fun CoroutineScope.parse(info: CustomArgumentInfo<String>): FriendsPlayer {
        val senderPlayer = FriendsPlayer[info.sender.uuid()]

        val matchingRequest = senderPlayer.receivedFriendRequests.firstOrNull { request ->
            resolveUsername(request.senderUuid) == info.currentInput
        } ?: throw CustomArgumentException.fromAdventureComponent(
            buildText {
                appendErrorPrefix()
                error("Du hast keine Freundschaftsanfrage von diesem Spieler erhalten.")
            }
        )

        return FriendsPlayer[matchingRequest.senderUuid]
    }

    init {
        replaceSuggestions(
            ArgumentSuggestions.stringsAsync { info ->
                plugin.scope.future {
                    val uuid = info.sender.uuidOrNull() ?: return@future arrayOf()
                    val friendsPlayer = FriendsPlayer[uuid]

                    friendsPlayer.receivedFriendRequests.mapNotNull { request ->
                        resolveUsername(request.senderUuid)
                    }.toTypedArray()
                }
            }
        )
    }
}

fun CommandAPICommand.receivedFriendRequestArgument(
    nodeName: String
): CommandAPICommand = withArguments(ReceivedFriendRequestArgument(nodeName))
