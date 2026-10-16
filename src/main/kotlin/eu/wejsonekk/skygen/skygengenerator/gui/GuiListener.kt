package eu.wejsonekk.skygen.skygengenerator.gui

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

/**
 * Routes inventory clicks to the [AbstractGui] that owns the clicked inventory.
 */
class GuiListener : Listener {

    @EventHandler
    fun onClick(event: InventoryClickEvent) {
        val holder = event.view.topInventory.holder
        if (holder !is AbstractGui) {
            return
        }

        event.isCancelled = true

        val player = event.whoClicked as? Player ?: return
        if (event.clickedInventory !== event.view.topInventory) {
            return
        }

        holder.click(player, event.rawSlot)
    }

    @EventHandler
    fun onDrag(event: InventoryDragEvent) {
        if (event.view.topInventory.holder is AbstractGui) {
            event.isCancelled = true
        }
    }
}
