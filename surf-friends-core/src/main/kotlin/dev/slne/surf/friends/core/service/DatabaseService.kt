package dev.slne.surf.friends.core.service

  import dev.slne.surf.friends.api.model.FriendRequest
  import dev.slne.surf.friends.api.model.Friendship
  import dev.slne.surf.friends.api.util.FriendSettingsPair
  import dev.slne.surf.friends.core.pair.CoreFriendSettingsPair
  import dev.slne.surf.surfapi.core.api.util.requiredService
  import it.unimi.dsi.fastutil.objects.ObjectSet
  import java.nio.file.Path
  import java.util.UUID

  /**
   * Interface for database operations related to friendships and settings.
   */
  interface DatabaseService {

      /**
       * Establishes a connection to the database.
       *
       * @param path The path to the database storage location.
       */
      fun connect(path: Path)

      /**
       * Retrieves the friend list of a user.
       *
       * @param uuid The UUID of the user.
       * @return A list of friendships representing the user's friends.
       */
      suspend fun getFriends(uuid: UUID): ObjectSet<Friendship>

      /**
       * Retrieves a specific friendship between two users.
       *
       * @param playerA The UUID of the first user.
       * @param playerB The UUID of the second user.
       * @return The friendship if it exists, otherwise null.
       */
      suspend fun getFriendship(playerA: UUID, playerB: UUID): Friendship?

      /**
       * Retrieves a specific friend request between two users.
       *
       * @param sender The UUID of the sender.
       * @param target The UUID of the target user.
       * @return The friend request if it exists, otherwise null.
       */
      suspend fun getFriendRequest(sender: UUID, target: UUID): FriendRequest?

      /**
       * Retrieves the list of sent friend requests of a user.
       *
       * @param uuid The UUID of the user.
       * @return A list of friend requests representing the recipients of the sent requests.
       */
      suspend fun getSentFriendRequests(uuid: UUID): ObjectSet<FriendRequest>

      /**
       * Retrieves the list of received friend requests of a user.
       *
       * @param uuid The UUID of the user.
       * @return A list of friend requests representing the senders of the received requests.
       */
      suspend fun getReceivedFriendRequests(uuid: UUID): ObjectSet<FriendRequest>

      /**
       * Retrieves the friendship settings of a user.
       *
       * @param uuid The UUID of the user.
       * @return A pair representing the user's friendship settings.
       */
      suspend fun getFriendSettings(uuid: UUID): CoreFriendSettingsPair

      /**
       * Adds a friendship between two users.
       *
       * @param uuid The UUID of the first user.
       * @param friend The UUID of the friend.
       * @return The created friendship.
       */
      suspend fun addFriendship(uuid: UUID, friend: UUID): Friendship

      /**
       * Removes a friendship between two users.
       *
       * @param uuid The UUID of the first user.
       * @param friend The UUID of the friend.
       */
      suspend fun removeFriendship(uuid: UUID, friend: UUID)

      /**
       * Adds a friend request.
       *
       * @param sender The UUID of the sender.
       * @param receiver The UUID of the receiver.
       * @return The created friend request.
       */
      suspend fun addFriendRequest(sender: UUID, receiver: UUID): FriendRequest

      /**
       * Removes a friend request.
       *
       * @param sender The UUID of the sender.
       * @param receiver The UUID of the receiver.
       */
      suspend fun removeFriendRequest(sender: UUID, receiver: UUID)

      /**
       * Updates the friendship settings of a user.
       *
       * @param uuid The UUID of the user.
       * @param pair The pair representing the new friendship settings.
       * @return The updated friendship settings.
       */
      suspend fun updateFriendSettings(uuid: UUID, pair: CoreFriendSettingsPair): CoreFriendSettingsPair

      companion object {
          /**
           * Instance of the `DatabaseService`.
           */
          val INSTANCE = requiredService<DatabaseService>()
      }
  }

  /**
   * Extension function to retrieve the instance of the `DatabaseService`.
   */
  val databaseService get() = DatabaseService.INSTANCE