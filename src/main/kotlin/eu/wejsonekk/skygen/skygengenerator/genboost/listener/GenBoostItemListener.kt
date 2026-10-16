package eu.wejsonekk.skygen.skygengenerator.genboost.listener

import eu.wejsonekk.skygen.skygengenerator.genboost.service.GenBoostService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

/**
 * Activates physical gen boost items on right click.
 */
class GenBoostItemListener(
    private val service: GenBoostService,
) : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        if (event.hand != EquipmentSlot.HAND) {
            return
        }

        val item = event.item ?: return
        if (this.service.activateFromItem(event.player, item)) {
            event.isCancelled = true
        }
    }
}
