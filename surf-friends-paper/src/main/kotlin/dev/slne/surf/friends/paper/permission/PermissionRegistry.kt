package dev.slne.surf.friends.paper.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object PermissionRegistry : PermissionRegistry() {
    const val PREFIX = "surf.friends"
    const val PREFIX_COMMAND = "$PREFIX.command"

    val COMMAND = create(PREFIX_COMMAND)
}