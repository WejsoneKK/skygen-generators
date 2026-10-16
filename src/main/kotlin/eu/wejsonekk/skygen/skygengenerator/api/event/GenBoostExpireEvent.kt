package eu.wejsonekk.skygen.skygengenerator.api.event

import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoost
import org.bukkit.event.HandlerList

/**
 * Called after a gen boost ran out of time and was removed. Not cancellable.
 */
class GenBoostExpireEvent(
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
