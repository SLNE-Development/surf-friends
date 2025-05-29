package dev.slne.surf.friends.velocity.command.subcommand.friend

import com.github.shynixn.mccoroutine.velocity.launch
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.friends.core.service.friendService
import dev.slne.surf.friends.velocity.command.argument.playerStringArgument
import dev.slne.surf.friends.velocity.container
import dev.slne.surf.friends.velocity.util.FriendPermissionRegistry
import dev.slne.surf.friends.velocity.util.format
import dev.slne.surf.friends.velocity.util.getUsernameAsync
import dev.slne.surf.friends.velocity.util.sendText
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.service.PlayerLookupService
import net.kyori.adventure.text.format.TextDecoration

class FriendInfoCommand(commandName: String) : CommandAPICommand(commandName) {
    init {
        withPermission(FriendPermissionRegistry.COMMAND_FRIEND_INFO)
        playerStringArgument("target")
        playerExecutor { player, args ->
            container.launch {
                val target: String by args
                val targetUuid = PlayerLookupService.getUuid(target) ?: return@launch run {
                    player.uniqueId.sendText {
                        error("Der Spieler $target wurde nicht gefunden.")
                    }
                }

                val friendShip = friendService.getFriendship(player.uniqueId, targetUuid)

                if (friendShip == null) {
                    player.uniqueId.sendText {
                        error("Du bist nicht mit $target befreundet.")
                    }
                    return@launch
                }

                val userName = friendShip.userUuid.getUsernameAsync()
                val targetName = friendShip.friendUuid.getUsernameAsync()

                player.sendText {
                    info("Freundschaft".toSmallCaps())
                    appendNewline()
                    append {
                        info("| ")
                        decorate(TextDecoration.BOLD)
                    }
                    variableKey("Spieler: ".toSmallCaps())
                    variableValue(userName)
                    appendNewline()

                    append {
                        info("| ")
                        decorate(TextDecoration.BOLD)
                    }
                    variableKey("Freund: ".toSmallCaps())
                    variableValue(targetName)
                    appendNewline()

                    append {
                        info("| ")
                        decorate(TextDecoration.BOLD)
                    }
                    variableKey("Befreundet seit: ".toSmallCaps())
                    variableValue(friendShip.createdAt.format())
                }
            }
        }
    }
}