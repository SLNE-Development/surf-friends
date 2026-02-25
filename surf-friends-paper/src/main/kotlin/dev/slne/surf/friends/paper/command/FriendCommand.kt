package dev.slne.surf.friends.paper.command

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.slne.surf.friends.paper.permission.PermissionRegistry

fun friendCommand() = commandAPICommand("friend") {
    withPermission(PermissionRegistry.BASE_COMMAND)
}