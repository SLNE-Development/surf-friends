package dev.slne.surf.friends.fallback.service

import com.google.auto.service.AutoService
import dev.slne.surf.friends.api.model.FriendRequest
import dev.slne.surf.friends.api.model.Friendship
import dev.slne.surf.friends.core.service.FriendService
import dev.slne.surf.friends.core.service.databaseService
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.util.Services
import java.util.*

@AutoService(FriendService::class)
class FallbackFriendService : FriendService, Services.Fallback {
    override suspend fun createFriendship(uuid: UUID, friend: UUID): Friendship {
        return databaseService.getFriendship(uuid, friend) ?: databaseService.addFriendship(
            uuid,
            friend
        )
    }

    override suspend fun removeFriendship(uuid: UUID, friend: UUID) {
        databaseService.removeFriendship(uuid, friend)
        databaseService.removeFriendship(friend, uuid)
    }

    override suspend fun getFriendship(
        playerA: UUID,
        playerB: UUID
    ): Friendship? {
        return databaseService.getFriendship(playerA, playerB)
    }

    override suspend fun areFriends(
        uuid: UUID,
        friend: UUID
    ): Friendship? {
        return databaseService.getFriendship(uuid, friend)
    }

    override suspend fun getFriendships(uuid: UUID): ObjectSet<Friendship> {
        return databaseService.getFriends(uuid)
    }

    override suspend fun sendFriendRequest(sender: UUID, receiver: UUID): FriendRequest {
        return databaseService.getFriendRequest(sender, receiver)
            ?: databaseService.addFriendRequest(sender, receiver)
    }

    override suspend fun acceptFriendRequest(sender: UUID, receiver: UUID) {
        databaseService.removeFriendRequest(sender, receiver)
        databaseService.addFriendship(sender, receiver)
        databaseService.addFriendship(receiver, sender)
    }

    override suspend fun declineFriendRequest(sender: UUID, receiver: UUID) {
        databaseService.removeFriendRequest(sender, receiver)
    }

    override suspend fun revokeFriendRequest(sender: UUID, receiver: UUID) {
        databaseService.removeFriendRequest(sender, receiver)
    }

    override suspend fun getSentFriendRequests(uuid: UUID): ObjectSet<FriendRequest> {
        return databaseService.getSentFriendRequests(uuid)
    }

    override suspend fun getReceivedFriendRequests(uuid: UUID): ObjectSet<FriendRequest> {
        return databaseService.getReceivedFriendRequests(uuid)
    }

    override suspend fun getFriendRequest(
        sender: UUID,
        target: UUID
    ): FriendRequest? {
        return databaseService.getFriendRequest(sender, target)
    }

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