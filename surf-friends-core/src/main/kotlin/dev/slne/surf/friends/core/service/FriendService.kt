package dev.slne.surf.friends.core.service

import dev.slne.surf.surfapi.core.api.util.requiredService

val friendService get() = requiredService<FriendService>()

interface FriendService {

}

