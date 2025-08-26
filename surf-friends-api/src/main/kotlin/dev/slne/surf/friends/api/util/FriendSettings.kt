package dev.slne.surf.friends.api.util

interface FriendSettings {
    var announcementsEnabled: Boolean
    var soundsEnabled: Boolean

    fun modify(block: FriendSettings.() -> Unit): FriendSettings
}