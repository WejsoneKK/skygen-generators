package eu.wejsonekk.skygen.skygengenerator.api.event

import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Base class for every generator related event.
 *
 * All generator events are fired asynchronously because generator data is
 * loaded and mutated off the main thread.
 */
abstract class GeneratorEvent(
    val generator: Generator,
    isAsync: Boolean = true,
) : Event(isAsync), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean = this.cancelled

    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }

    abstract override fun getHandlers(): HandlerList
}
