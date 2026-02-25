package dev.slne.surf.friends.paper.command

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.slne.surf.friends.paper.permission.PermissionRegistry

fun friendCommand() = commandTree("friend") {
    withPermission(PermissionRegistry.PREFIX_COMMAND)
}