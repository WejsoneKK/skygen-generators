package eu.wejsonekk.skygen.skygengenerator.api.event

import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoost
import org.bukkit.event.HandlerList

/**
 * Called when the periodic lottery picked [boost], right before it is
 * activated. Cancellable: cancelling skips this single draw (the timer is
 * still reset).
 */
class GenBoostLotteryEvent(
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
