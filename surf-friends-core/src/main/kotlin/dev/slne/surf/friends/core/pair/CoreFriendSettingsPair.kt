package dev.slne.surf.friends.core.pair

import dev.slne.surf.friends.api.util.FriendSettingsPair

class CoreFriendSettingsPair(
    override var announcementsEnabled: Boolean = true,
    override var soundsEnabled: Boolean = true
) : FriendSettingsPair {
    override fun copy(block: FriendSettingsPair.() -> Unit): CoreFriendSettingsPair {
        val copy = CoreFriendSettingsPair(announcementsEnabled, soundsEnabled)
        copy.block()
        return copy
    }
}