package dev.slne.surf.friends.velocity.util

import dev.slne.surf.friends.core.service.FriendService
import dev.slne.surf.friends.velocity.plugin
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import it.unimi.dsi.fastutil.objects.ObjectList
import java.util.UUID

suspend fun FriendService.getOnlineFriends(user: UUID): ObjectList<UUID> {
    return this.getFriendships(user)
        .mapNotNull {
            val other = if (it.friendUuid == user) it.userUuid else it.friendUuid
            if (other.isOnline()) other else null
        }
        .toCollection(mutableObjectListOf())
}

fun UUID.isOnline(): Boolean {
    return plugin.proxy.getPlayer(this).isPresent
}