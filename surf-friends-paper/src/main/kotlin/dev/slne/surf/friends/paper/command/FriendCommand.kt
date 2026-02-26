package dev.slne.surf.friends.paper.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.core.api.paper.command.argument.surfOfflinePlayerArgument
import dev.slne.surf.friends.paper.command.argument.friend.offlineFriendArgument
import dev.slne.surf.friends.paper.permission.PermissionRegistry

fun friendCommand() = commandTree("friend") {
    withPermission(PermissionRegistry.PREFIX_COMMAND)

    literalArgument("add") {
        surfOfflinePlayerArgument("target") {

        }
    }

    literalArgument("remove") {
        offlineFriendArgument("friend") {

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