package eu.wejsonekk.skygen.skygengenerator.gui

import eu.wejsonekk.skygen.skygengenerator.config.impl.GuiSectionConfig
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

/**
 * Base class for every chest based GUI. Implements [InventoryHolder] so clicks can
 * be safely routed back to the owning GUI instance (see [GuiListener]).
 *
 * The look & feel (title, rows, items, slots) comes from the [section] config, so
 * every GUI is fully configurable. Opening a GUI automatically pushes the
 * currently open [AbstractGui] onto the per player [GuiHistory], so a "back"
 * button (see [backButton]) can return to the previous menu.
 */
abstract class AbstractGui(
    protected val section: GuiSectionConfig,
    private val title: Component,
) : InventoryHolder {

    private val inventory: Inventory =
        Bukkit.createInventory(this, this.section.rows.coerceIn(1, 6) * 9, title)
    private val actions = HashMap<Int, (Player) -> Unit>()

    /** Player the current render is being prepared for (used for placeholders). */
    protected var viewer: Player? = null
        private set

    final override fun getInventory(): Inventory = this.inventory

    protected val size: Int get() = this.rows * 9

    protected val rows: Int get() = this.section.rows.coerceIn(1, 6)

    protected open fun layout(): GuiLayout? = null

    /** Builds the configured item [key] with the given `{...}` [placeholders]. */
    protected fun guiItem(key: String, placeholders: Map<String, String> = emptyMap()): ItemStack =
        GuiItems.build(this.section.items[key], this.viewer, placeholders)

    /** Resolves the configured slot [key] or falls back to [fallback]. */
    protected fun slot(key: String, fallback: Int): Int = this.section.slot(key, fallback)

    protected fun setItem(slot: Int, item: ItemStack, action: ((Player) -> Unit)? = null) {
        this.inventory.setItem(slot, item)
        if (action == null) {
            this.actions.remove(slot)
        } else {
            this.actions[slot] = action
        }
    }

    /** Sets [item] on [slot] only when the slot is still empty (used by layouts). */
    fun fillSlot(slot: Int, item: ItemStack) {
        if (slot in 0 until this.size && this.inventory.getItem(slot) == null) {
            this.inventory.setItem(slot, item)
        }
    }

    protected fun backButton(fallbackSlot: Int) {
        this.setItem(this.slot("back", fallbackSlot), this.guiItem("back")) { player -> this.back(player) }
    }

    /** Opens this GUI and remembers the currently open GUI for [back]. */
    fun open(player: Player) {
        val current = player.openInventory.topInventory.holder
        if (current !== this && current is AbstractGui) {
            GuiHistory.push(player, current)
        }
        this.openDirect(player)
    }

    /** Opens this GUI without touching the navigation history. */
    fun openDirect(player: Player) {
        this.viewer = player
        this.render()
        player.openInventory(this.inventory)
    }

    fun refresh(player: Player) {
        this.viewer = player
        this.render()
        player.updateInventory()
    }

    /** Returns to the previous GUI from the history, or closes if there is none. */
    fun back(player: Player) {
        val previous = GuiHistory.pop(player)
        if (previous == null) {
            player.closeInventory()
            return
        }
        previous.openDirect(player)
    }

    fun click(player: Player, slot: Int) {
        this.actions[slot]?.invoke(player)
    }

    private fun render() {
        this.inventory.clear()
        this.actions.clear()
        this.layout()?.apply(this)
        this.build()
    }

    protected abstract fun build()
}
