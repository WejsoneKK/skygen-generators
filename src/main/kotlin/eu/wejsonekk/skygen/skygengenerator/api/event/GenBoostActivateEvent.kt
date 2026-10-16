package eu.wejsonekk.skygen.skygengenerator.api.event

import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoost
import org.bukkit.event.HandlerList

/**
 * Called before a gen boost is activated. Cancellable.
 */
class GenBoostActivateEvent(
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
