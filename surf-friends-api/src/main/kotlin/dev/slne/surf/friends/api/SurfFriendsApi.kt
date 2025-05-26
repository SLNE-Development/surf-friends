package dev.slne.surf.friends.api

import dev.slne.surf.friends.api.model.FriendRequest
import dev.slne.surf.friends.api.model.Friendship
import it.unimi.dsi.fastutil.objects.ObjectSet
import java.util.UUID

interface SurfFriendsApi {
    /**
     * Creates a friendship between two users.
     *
     * @param uuid The UUID of the first user.
     * @param friend The UUID of the friend.
     * @return The created Friendship object.
     */
    suspend fun createFriendship(uuid: UUID, friend: UUID): Friendship

    /**
     * Removes a friendship between two users.
     *
     * @param uuid The UUID of the first user.
     * @param friend The UUID of the friend.
     */
    suspend fun removeFriendship(uuid: UUID, friend: UUID)

    /**
     * Retrieves all friendships of a user.
     *
     * @param uuid The UUID of the user.
     * @return A set of Friendships representing the user's friends.
     */
    suspend fun getFriendships(uuid: UUID): ObjectSet<Friendship>

    /**
     * Sends a friend request from one user to another.
     *
     * @param sender The UUID of the sender.
     * @param receiver The UUID of the receiver.
     * @return The created FriendRequest object.
     */
    suspend fun sendFriendRequest(sender: UUID, receiver: UUID): FriendRequest

    /**
     * Accepts a friend request.
     *
     * @param sender The UUID of the sender of the request.
     * @param receiver The UUID of the receiver of the request.
     */
    suspend fun acceptFriendRequest(sender: UUID, receiver: UUID)

    /**
     * Declines a friend request.
     *
     * @param sender The UUID of the sender of the request.
     * @param receiver The UUID of the receiver of the request.
     */
    suspend fun declineFriendRequest(sender: UUID, receiver: UUID)

    /**
     * Revokes a sent friend request.
     *
     * @param sender The UUID of the sender of the request.
     * @param receiver The UUID of the receiver of the request.
     */
    suspend fun revokeFriendRequest(sender: UUID, receiver: UUID)

    /**
     * Retrieves all sent friend requests of a user.
     *
     * @param uuid The UUID of the user.
     * @return A set of FriendRequests representing the recipients of the sent requests.
     */
    suspend fun getSentFriendRequests(uuid: UUID): ObjectSet<FriendRequest>

    /**
     * Retrieves all received friend requests of a user.
     *
     * @param uuid The UUID of the user.
     * @return A set of FriendRequests representing the senders of the received requests.
     */
    suspend fun getReceivedFriendRequests(uuid: UUID): ObjectSet<FriendRequest>

    /**
     * Toggles announcements for a user.
     *
     * @param uuid The UUID of the user.
     * @return The new status of announcements (true if enabled, false if disabled).
     */
    suspend fun toggleAnnouncements(uuid: UUID): Boolean

    /**
     * Toggles sounds for a user.
     *
     * @param uuid The UUID of the user.
     * @return The new status of sounds (true if enabled, false if disabled).
     */
    suspend fun toggleSounds(uuid: UUID): Boolean

    /**
     * Checks if two users are friends and retrieves their friendship details.
     *
     * @param uuid The UUID of the first user.
     * @param friend The UUID of the second user.
     * @return The Friendship object representing the friendship between the two users, or null if they are not friends.
     */
    suspend fun areFriends(uuid: UUID, friend: UUID): Friendship?
}