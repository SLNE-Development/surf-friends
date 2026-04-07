package dev.slne.surf.friends.paper.command.argument.friend

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

class FriendArgument(nodeName: String) :
    SuspendCustomArgument<FriendsPlayer, String>(StringArgument(nodeName)) {

    override suspend fun CoroutineScope.parse(info: CustomArgumentInfo<String>): FriendsPlayer {
        val senderPlayer = FriendsPlayer[info.sender.uuid()]

        val matchingFriendship = senderPlayer.friendships.firstOrNull { friendship ->
            resolveUsername(friendship.friendUuid) == info.currentInput
        } ?: throw CustomArgumentException.fromAdventureComponent(
            buildText {
                appendErrorPrefix()
                error("Du bist nicht mit diesem Spieler befreundet.")
            }
        )

        return FriendsPlayer[matchingFriendship.friendUuid]
    }

    init {
        replaceSuggestions(
            ArgumentSuggestions.stringsAsync { info ->
                plugin.scope.future {
                    val uuid = info.sender.uuidOrNull() ?: return@future null
                    val friendsPlayer = FriendsPlayer[uuid]

                    friendsPlayer.friendships.mapNotNull { friendship ->
                        resolveUsername(friendship.friendUuid)
                    }.toTypedArray()
                }
            }
        )
    }
}

fun CommandAPICommand.friendArgument(
    nodeName: String
): CommandAPICommand = withArguments(FriendArgument(nodeName))
