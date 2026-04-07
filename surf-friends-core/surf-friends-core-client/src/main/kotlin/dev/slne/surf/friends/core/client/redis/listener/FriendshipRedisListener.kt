package dev.slne.surf.friends.core.client.redis.listener

import dev.slne.surf.core.api.common.util.sendText
import dev.slne.surf.friends.api.utils.displayName
import dev.slne.surf.friends.api.utils.toSurfPlayer
import dev.slne.surf.friends.core.client.FriendsClientInstance
import dev.slne.surf.friends.core.client.redis.event.FriendRemoveRedisEvent
import dev.slne.surf.redis.event.OnRedisEvent

object FriendshipRedisListener {
    @OnRedisEvent
    fun onFriendRemove(
        event: FriendRemoveRedisEvent
    ) = FriendsClientInstance.INSTANCE.launch {
        val executor = event.executorUuid.toSurfPlayer()
        val target = event.targetUuid.toSurfPlayer()

        target?.sendText {
            appendInfoPrefix()

            append(executor.displayName())
            info(" hat dich als Freund entfernt.")
        }

        executor?.sendText {
            appendSuccessPrefix()

            success("Du hast ")
            append(target.displayName())
            success(" als Freund entfernt.")
        }
    }
}