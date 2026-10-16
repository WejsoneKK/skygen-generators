package eu.wejsonekk.skygen.skygengenerator.api.event

import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import com.cryptomorin.xseries.XMaterial
import org.bukkit.event.HandlerList

/**
 * Called when a generator produced its drops. Cancellable.
 *
 * The [amount] may be modified by listeners to change how many items are dropped.
 *
 * @param material final material dropped
 * @param amount final amount dropped (already includes boost multipliers)
 * @param multiplier boost multiplier applied for this cycle (1.0 = none)
 */
class GeneratorGenerateEvent(
    generator: Generator,
    val material: XMaterial,
    var amount: Int,
    val multiplier: Double,
) : GeneratorEvent(generator) {

    companion object {
        @JvmField
        val HANDLERS = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList = HANDLERS
    }

    override fun getHandlers(): HandlerList = HANDLERS
}
