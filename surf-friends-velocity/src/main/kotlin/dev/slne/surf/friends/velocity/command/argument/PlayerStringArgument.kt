package dev.slne.surf.friends.velocity.command.argument

import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CommandAPIArgumentType
import dev.jorel.commandapi.executors.CommandArguments
import dev.slne.surf.core.api.common.surfCoreApi

class PlayerStringArgument(nodeName: String) :
    Argument<String>(nodeName, StringArgumentType.string()) {
    override fun getPrimitiveType(): Class<String> {
        return String::class.java
    }

    override fun getArgumentType(): CommandAPIArgumentType {
        return CommandAPIArgumentType.PRIMITIVE_TEXT
    }

    override fun <Source : Any> parseArgument(
        cmdCtx: CommandContext<Source>,
        key: String,
        previousArgs: CommandArguments
    ): String {
        return StringArgumentType.getString(cmdCtx, key)
    }

    init {
        replaceSuggestions(ArgumentSuggestions.stringCollection {
            surfCoreApi.getOnlinePlayers().mapNotNull { it.lastKnownName }
        })
    }
}

inline fun CommandAPICommand.playerStringArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandAPICommand = withArguments(
    PlayerStringArgument(nodeName).setOptional(optional).apply(block)
)