package dev.slne.surf.friends.core.pair

import dev.slne.surf.friends.api.util.FriendSettings

data class CoreFriendSettings(
    override var announcementsEnabled: Boolean = true,
    override var soundsEnabled: Boolean = true
) : FriendSettings {
    override fun modify(block: FriendSettings.() -> Unit) = this.apply(block)
}