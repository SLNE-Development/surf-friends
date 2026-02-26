package dev.slne.surf.friends.paper.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.core.api.common.player.SurfPlayer
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.friends.api.friend.friendRequest
import dev.slne.surf.friends.api.player.FriendPlayer
import dev.slne.surf.friends.core.service.friendRequestService
import dev.slne.surf.friends.core.service.friendShipService
import dev.slne.surf.friends.paper.command.argument.friend.offlineFriendArgument
import dev.slne.surf.friends.paper.permission.PermissionRegistry
import dev.slne.surf.friends.paper.util.friendPlayer
import dev.slne.surf.surfapi.bukkit.api.command.executors.playerExecutorSuspend
import dev.slne.surf.surfapi.core.api.command.args.awaiting
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText

fun friendCommand() = commandTree("friend") {
    withPermission(PermissionRegistry.PREFIX_COMMAND)

    literalArgument("add") {
        surfOfflinePlayerArgument("target") { // TODO: Exclude self and online friends
            playerExecutorSuspend { player, args ->
                val target = args.awaiting<SurfPlayer?>("target")

                if (target == null) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Der Spieler wurde nicht gefunden.")
                    }
                    return@playerExecutorSuspend
                }

                val friendPlayer = player.friendPlayer

                if (friendPlayer.hasFriend(target.uuid)) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Du bist bereits mit diesem Spieler befreundet.")
                    }
                    return@playerExecutorSuspend
                }

                if (friendPlayer.hasReceivedFriendRequest(target.uuid)) {
                    // TODO: Accept request
                    return@playerExecutorSuspend
                }

                val friendRequest = friendRequest(
                    senderUuid = player.uniqueId,
                    receiverUuid = target.uuid,
                    senderName = player.name,
                    receiverName = target.lastKnownName ?: args.getRaw("target")
                    ?: error("Player has no name and argument has no raw!")
                )

                friendRequestService.saveFriendRequest(friendRequest)

                player.sendText {
                    appendSuccessPrefix()
                    success("Du hast eine Freundschaftsanfrage an ")
                    variableValue(target.lastKnownName ?: args.getRaw("target") ?: "#Unbekannt")
                    success(" gesendet.")
                }

                // TODO: Notify target if online and if settings allow it
            }
        }
    }

    literalArgument("remove") {
        offlineFriendArgument("friend") {
            playerExecutorSuspend { player, args ->
                val friend = args.awaiting<FriendPlayer?>("friend")

                if (friend == null) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Der Spieler wurde nicht gefunden.")
                    }
                    return@playerExecutorSuspend
                }

                val friendPlayer = player.friendPlayer

                if (!friendPlayer.hasFriend(friend.uuid)) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Du bist mit diesem Spieler nicht befreundet.")
                    }
                    return@playerExecutorSuspend
                }

                val friendship =
                    friendPlayer.friends.firstOrNull { it.acceptedBy == friend.uuid || it.requestedBy == friend.uuid }

                if (friendship == null) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Du bist mit diesem Spieler nicht befreundet.")
                    }
                    return@playerExecutorSuspend
                }

                friendShipService.deleteFriendShip(friendship)

                player.sendText {
                    appendSuccessPrefix()
                    success("Du hast die Freundschaft mit ")
                    variableValue(friend.name)
                    success(" beendet.")
                }

                // TODO: Notify old friend if online and if settings allow it
            }
        }
    }

    literalArgument("list") {

    }

    literalArgument("requests") {

    }

    literalArgument("accept") {

    }

    literalArgument("decline") {

    }

    literalArgument("revoke") {

    }
}