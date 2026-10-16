package eu.wejsonekk.skygen.skygengenerator.api.event

import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoost
import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Base class for every gen boost related event.
 *
 * Fired asynchronously unless a subclass states otherwise, because boosts are
 * managed off the main thread.
 */
abstract class GenBoostEvent(
    val boost: GenBoost,
    isAsync: Boolean = true,
) : Event(isAsync), Cancellable {

    private var cancelled: Boolean = false

    override fun isCancelled(): Boolean = this.cancelled

    override fun setCancelled(cancel: Boolean) {
        this.cancelled = cancel
    }

    abstract override fun getHandlers(): HandlerList
}
