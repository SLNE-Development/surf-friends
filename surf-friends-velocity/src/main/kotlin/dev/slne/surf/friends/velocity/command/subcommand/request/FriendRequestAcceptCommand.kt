package dev.slne.surf.friends.velocity.command.subcommand.request

import com.github.shynixn.mccoroutine.velocity.launch

import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.subcommand

import dev.slne.surf.friends.core.service.friendService
import dev.slne.surf.friends.velocity.command.argument.playerStringArgument
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.redis.event.FriendRemoveRedisEvent
import dev.slne.surf.friends.velocity.redis.event.FriendRequestAcceptRedisEvent
import dev.slne.surf.friends.velocity.redis.redisLoader
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry
import dev.slne.surf.friends.velocity.util.sendText
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService

fun CommandAPICommand.friendRequestAcceptCommand() = subcommand("accept") {
    withPermission(FriendPermissionRegistry.COMMAND_FRIEND_REQUEST_ACCEPT)
    playerStringArgument("target")
    playerExecutor { player, args ->
        container.launch {
            val target: String by args
            val targetUuid = PlayerLookupService.getUuid(target) ?: return@launch run {
                player.uniqueId.sendText {
                    error("Der Spieler $target wurde nicht gefunden.")
                }
            }

            val friendRequest = friendService.getFriendRequest(targetUuid, player.uniqueId)

            if (friendRequest == null) {
                player.uniqueId.sendText {
                    error("Du hast keine Freundschaftsanfrage von $target erhalten.")
                }
                return@launch
            }

            val friendShip = friendService.getFriendship(player.uniqueId, targetUuid)

            if (friendShip != null) {
                player.uniqueId.sendText {
                    error("Du bist bereits mit $target befreundet.")
                }
                return@launch
            }

            friendService.acceptFriendRequest(targetUuid, player.uniqueId)

            player.uniqueId.sendText {
                success("Du bist nun mit ")
                variableValue(target)
                success(" befreundet.")
            }

            redisLoader.redisApi.publishEvent(FriendRequestAcceptRedisEvent(
                targetUuid, player.uniqueId, player.username
            ))
        }
    }
}