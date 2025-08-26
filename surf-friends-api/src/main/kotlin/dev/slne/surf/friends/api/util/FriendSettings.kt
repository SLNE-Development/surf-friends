package dev.slne.surf.friends.api.util

interface FriendSettings {
    var announcementsEnabled: Boolean
    var soundsEnabled: Boolean

    fun copy(block: FriendSettings.() -> Unit): FriendSettings
}