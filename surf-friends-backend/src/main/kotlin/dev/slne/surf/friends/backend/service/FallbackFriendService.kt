package dev.slne.surf.friends.backend.service

import com.google.auto.service.AutoService
import dev.slne.surf.friends.api.friend.FriendRequest
import dev.slne.surf.friends.core.service.FriendService
import dev.slne.surf.friends.core.service.databaseService
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(FriendService::class)
class FallbackFriendService : FriendService, Services.Fallback {
    override suspend fun createFriendship(uuid: UUID, friend: UUID) =
        databaseService.getFriendship(uuid, friend) ?: databaseService.addFriendship(
            uuid,
            friend
        )

    override suspend fun removeFriendship(uuid: UUID, friend: UUID) {
        databaseService.removeFriendship(uuid, friend)
        databaseService.removeFriendship(friend, uuid)
    }

    override suspend fun getFriendship(
        playerA: UUID,
        playerB: UUID
    ) = databaseService.getFriendship(playerA, playerB)

    override suspend fun areFriends(
        uuid: UUID,
        friend: UUID
    ) = databaseService.getFriendship(uuid, friend)

    override suspend fun getFriendships(uuid: UUID) = databaseService.getFriends(uuid)

    override suspend fun sendFriendRequest(sender: UUID, receiver: UUID): FriendRequest =
        databaseService.getFriendRequest(sender, receiver)
            ?: databaseService.addFriendRequest(sender, receiver)

    override suspend fun acceptFriendRequest(sender: UUID, receiver: UUID) {
        databaseService.removeFriendRequest(sender, receiver)
        databaseService.addFriendship(sender, receiver)
        databaseService.addFriendship(receiver, sender)
    }

    override suspend fun declineFriendRequest(sender: UUID, receiver: UUID) =
        databaseService.removeFriendRequest(sender, receiver)

    override suspend fun revokeFriendRequest(sender: UUID, receiver: UUID) =
        databaseService.removeFriendRequest(sender, receiver)

    override suspend fun getSentFriendRequests(uuid: UUID) =
        databaseService.getSentFriendRequests(uuid)

    override suspend fun getReceivedFriendRequests(uuid: UUID) =
        databaseService.getReceivedFriendRequests(uuid)

    override suspend fun getFriendRequest(
        sender: UUID,
        target: UUID
    ) = databaseService.getFriendRequest(sender, target)

    override suspend fun toggleAnnouncements(uuid: UUID): Boolean {
        val friendSettings = databaseService.getFriendSettings(uuid)
        val newSettings = friendSettings.modify {
            announcementsEnabled = !friendSettings.announcementsEnabled
        }

        return databaseService.updateFriendSettings(uuid, newSettings).announcementsEnabled
    }

    override suspend fun toggleSounds(uuid: UUID): Boolean {
        val friendSettings = databaseService.getFriendSettings(uuid)
        val newSettings = friendSettings.modify {
            soundsEnabled = !friendSettings.soundsEnabled
        }

        return databaseService.updateFriendSettings(uuid, newSettings).soundsEnabled
    }
}