package dev.slne.surf.friends.minestom.command.subcommand.request

import dev.slne.minestom.lobby.api.command.commandapi.CommandAPICommand
import dev.slne.minestom.lobby.api.command.commandapi.dsl.playerExecutorSuspend
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.minestom.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.friends.core.client.command.sendFriendRequest
import dev.slne.surf.friends.core.client.permission.FriendPermissions
import kotlinx.coroutines.Deferred

fun friendRequestSendCommand(commandName: String) = CommandAPICommand(commandName).apply {
    withPermission(FriendPermissions.COMMAND_FRIEND_REQUEST_SEND)
    surfOfflinePlayerArgument("target")

    playerExecutorSuspend { player, args ->
        val target: Deferred<SurfPlayer?> by args

        sendFriendRequest(player.uuid, target.await())?.let(player::sendMessage)
    }
}
