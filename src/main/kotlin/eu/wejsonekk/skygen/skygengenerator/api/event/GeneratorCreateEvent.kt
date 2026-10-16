package eu.wejsonekk.skygen.skygengenerator.api.event

import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import org.bukkit.event.HandlerList

/**
 * Called before a generator is persisted and added to the cache. Cancellable.
 */
class GeneratorCreateEvent(
    generator: Generator,
) : GeneratorEvent(generator) {

    companion object {
        @JvmField
        val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }

    override fun getHandlers(): HandlerList = HANDLERS
}
