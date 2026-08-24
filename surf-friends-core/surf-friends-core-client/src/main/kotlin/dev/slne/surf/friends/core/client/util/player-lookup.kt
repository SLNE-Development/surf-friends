package dev.slne.surf.friends.core.client.util

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.asLoadingCache
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.core.api.common.SurfCoreApi
import java.util.*
import kotlin.time.Duration.Companion.minutes

private val offlineUsernames = Caffeine.newBuilder()
    .maximumSize(10_000)
    .expireAfterWrite(2.minutes)
    .asLoadingCache<UUID, String?> { uuid ->
        SurfCoreApi.getOfflinePlayer(uuid)?.username ?: PlayerLookupService.getUsername(uuid)
    }

/**
 * The name of the player identified by [uuid], or `null` if it cannot be resolved.
 */
suspend fun resolveUsername(uuid: UUID): String? {
    return SurfCoreApi.getPlayer(uuid)?.username ?: offlineUsernames.get(uuid)
}
