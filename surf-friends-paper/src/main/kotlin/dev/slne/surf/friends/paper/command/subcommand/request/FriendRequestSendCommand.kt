package dev.slne.surf.friends.paper.command.subcommand.request

import dev.jorel.commandapi.CommandAPICommand
import dev.slne.surf.api.core.command.args.awaitingOrNull
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.friends.core.client.command.sendFriendRequest
import dev.slne.surf.friends.core.client.permission.FriendPermissions

class FriendRequestSendCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(FriendPermissions.COMMAND_FRIEND_REQUEST_SEND)
        surfOfflinePlayerArgument("target")
        playerExecutorSuspend { player, args ->
            val target = args.awaitingOrNull<SurfPlayer?>("target")

            sendFriendRequest(player.uniqueId, target)?.let(player::sendMessage)
        }
    }
}
