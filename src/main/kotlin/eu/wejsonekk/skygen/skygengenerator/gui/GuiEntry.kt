package eu.wejsonekk.skygen.skygengenerator.gui

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * A single clickable entry inside a GUI.
 */
data class GuiEntry(
    val item: ItemStack,
    val action: ((Player) -> Unit)? = null,
)
