package dev.slne.surf.friends.paper.util

import dev.slne.surf.api.core.service.PlayerLookupService
import dev.slne.surf.core.api.common.SurfCoreApi
import java.util.*

suspend fun resolveUsername(uuid: UUID): String? {
    return SurfCoreApi.getOfflinePlayer(uuid)?.username
        ?: PlayerLookupService.getUsername(uuid)
}