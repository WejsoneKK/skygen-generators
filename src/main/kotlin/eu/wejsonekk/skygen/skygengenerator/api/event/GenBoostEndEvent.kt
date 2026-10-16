package eu.wejsonekk.skygen.skygengenerator.api.event

import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoost
import org.bukkit.event.HandlerList

/**
 * Called before a running gen boost is stopped by an explicit call (API /
 * `/genboost`), as opposed to [GenBoostExpireEvent] which is fired when a boost
 * simply runs out of time. Cancellable.
 */
class GenBoostEndEvent(
    boost: GenBoost,
) : GenBoostEvent(boost) {

    override fun getHandlers(): HandlerList = HANDLERS

    companion object {
        @JvmField
        val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }
}
