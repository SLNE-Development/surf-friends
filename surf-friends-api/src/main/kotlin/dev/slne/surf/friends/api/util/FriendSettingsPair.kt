package dev.slne.surf.friends.api.util

interface FriendSettingsPair {
    var announcementsEnabled: Boolean
    var soundsEnabled: Boolean

    fun copy(block: FriendSettingsPair.() -> Unit): FriendSettingsPair
}