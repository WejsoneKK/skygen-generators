package eu.wejsonekk.skygen.skygengenerator.api.event

import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoost
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.inventory.ItemStack

/**
 * Called right before a physical gen boost item is handed to [player].
 * Cancellable — cancelling prevents the item from being given.
 *
 * @param item the item that is about to be given (may be modified, e.g. amount)
 */
class GenBoostReceiveEvent(
    val player: Player,
    boost: GenBoost,
    val item: ItemStack,
) : GenBoostEvent(boost, isAsync = false) {

    override fun getHandlers(): HandlerList = HANDLERS

    companion object {
        @JvmField
        val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }
}
