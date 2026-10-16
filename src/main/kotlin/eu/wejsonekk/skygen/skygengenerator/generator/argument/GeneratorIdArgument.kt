package eu.wejsonekk.skygen.skygengenerator.generator.argument

import dev.rollczi.litecommands.argument.Argument
import dev.rollczi.litecommands.argument.parser.ParseResult
import dev.rollczi.litecommands.argument.resolver.ArgumentResolver
import dev.rollczi.litecommands.invalidusage.InvalidUsage
import dev.rollczi.litecommands.invocation.Invocation
import dev.rollczi.litecommands.suggestion.SuggestionContext
import dev.rollczi.litecommands.suggestion.SuggestionResult
import eu.wejsonekk.skygen.skygengenerator.generator.service.GeneratorService
import org.bukkit.command.CommandSender

/**
 * LiteCommands argument for a generator id. Accepts any non blank id and
 * suggests the ids of every currently loaded generator.
 */
class GeneratorIdArgument(
    private val service: GeneratorService,
) : ArgumentResolver<CommandSender, GeneratorId>() {

    override fun parse(
        invocation: Invocation<CommandSender>,
        context: Argument<GeneratorId>,
        argument: String,
    ): ParseResult<GeneratorId> {
        if (argument.isBlank()) {
            return ParseResult.failure(InvalidUsage.Cause.INVALID_ARGUMENT)
        }
        return ParseResult.success(GeneratorId(argument))
    }

    override fun suggest(
        invocation: Invocation<CommandSender>,
        argument: Argument<GeneratorId>,
        context: SuggestionContext,
    ): SuggestionResult = SuggestionResult.of(
        this.service.cachedGenerators().map { it.id },
    )
}
