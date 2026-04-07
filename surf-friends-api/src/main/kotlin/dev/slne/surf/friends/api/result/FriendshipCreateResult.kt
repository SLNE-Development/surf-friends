package dev.slne.surf.friends.api.result

import dev.slne.surf.api.core.serializer.adventure.component.SerializableComponent
import kotlinx.serialization.Serializable
import net.kyori.adventure.text.ComponentLike

@Serializable
sealed class FriendshipCreateResult(
    val message: SerializableComponent
) : ComponentLike {
    override fun asComponent() = message
}