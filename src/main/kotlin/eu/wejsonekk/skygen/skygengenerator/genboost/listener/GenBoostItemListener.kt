package eu.wejsonekk.skygen.skygengenerator.genboost.listener

import eu.wejsonekk.skygen.skygengenerator.config.impl.GenBoostConfig
import eu.wejsonekk.skygen.skygengenerator.genboost.service.GenBoostService
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

/**
 * Activates physical gen boost items on right click (in air and on blocks).
 * Air activation can be disabled through `genboost.yml` (`items.allowAirActivation`).
 */
class GenBoostItemListener(
    private val service: GenBoostService,
    private val config: GenBoostConfig,
) : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onInteract(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_AIR && event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }
        if (event.action == Action.RIGHT_CLICK_AIR && !this.config.items.allowAirActivation) {
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
