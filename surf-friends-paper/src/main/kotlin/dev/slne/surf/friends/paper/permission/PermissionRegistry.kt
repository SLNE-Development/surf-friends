package dev.slne.surf.friends.paper.permission

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

object PermissionRegistry : PermissionRegistry() {
    const val BASE = "surf.friends"
    const val BASE_COMMAND = "$BASE.command"

    val COMMAND_FRIEND_REQUEST_SEND = create("$BASE_COMMAND.request.send")
    val COMMAND_FRIEND_REQUEST_ACCEPT = create("$BASE_COMMAND.request.accept")
    val COMMAND_FRIEND_REQUEST_DECLINE = create("$BASE_COMMAND.request.decline")
    val COMMAND_FRIEND_REQUEST_REVOKE = create("$BASE_COMMAND.request.revoke")
    val COMMAND_FRIEND_REQUEST_LIST = create("$BASE_COMMAND.request.list")

    val COMMAND_FRIEND_INFO = create("$BASE_COMMAND.info")
    val COMMAND_FRIEND_JUMP = create("$BASE_COMMAND.jump")
    val COMMAND_FRIEND_REMOVE = create("$BASE_COMMAND.remove")
    val COMMAND_FRIEND_LIST = create("$BASE_COMMAND.list")
}