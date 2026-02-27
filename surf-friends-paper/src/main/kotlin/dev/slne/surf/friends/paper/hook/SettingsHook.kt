package dev.slne.surf.friends.paper.hook

import dev.slne.surf.settings.api.surfSettingsApi
import org.bukkit.Bukkit
import java.util.*

object SettingsHook {
    fun isEnabled() = Bukkit.getPluginManager().isPluginEnabled("surf-settings-paper")

    fun hasFriendRequestsEnabled(playerUuid: UUID): Boolean {
        return if (isEnabled()) {
            surfSettingsApi.getPlayerSetting(playerUuid, "friend_requests")?.getBoolean()
                ?: true
        } else {
            true
        }
    }

    fun hasFriendNotifyEnabled(playerUuid: UUID): Boolean {
        return if (isEnabled()) {
            surfSettingsApi.getPlayerSetting(playerUuid, "friend_notify")?.getBoolean()
                ?: false
        } else {
            false
        }
    }
}