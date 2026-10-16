package eu.wejsonekk.skygen.skygengenerator.api.event

import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import org.bukkit.event.HandlerList

/**
 * Called before an existing generator is updated (material, amount, interval,
 * owner, ...). Cancellable.
 *
 * @param generator the new, proposed state
 * @param previous the state before the change
 */
class GeneratorEditEvent(
    generator: Generator,
    val previous: Generator,
) : GeneratorEvent(generator) {

    companion object {
        @JvmField
        val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }

    override fun getHandlers(): HandlerList = HANDLERS
}
