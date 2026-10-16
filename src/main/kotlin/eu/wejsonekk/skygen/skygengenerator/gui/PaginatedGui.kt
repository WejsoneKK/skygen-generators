package eu.wejsonekk.skygen.skygengenerator.gui

import eu.wejsonekk.skygen.skygengenerator.config.impl.GuiSectionConfig
import net.kyori.adventure.text.Component

/**
 * A paginated GUI. The look & feel (title, rows, items, slots) comes from the
 * [GuiSectionConfig]. Only the inner slots (everything except the outer border)
 * are used for [content], so a bordered background stays visible.
 *
 * The navigation items (`previousPage`, `nextPage`, `pageInfo`) and the
 * additional [controls] can be freely placed through the section slots.
 */
abstract class PaginatedGui(
    section: GuiSectionConfig,
    title: Component,
) : AbstractGui(section, title) {

    protected var page: Int = 0

    protected abstract fun content(): List<GuiEntry>

    protected open fun controls(): Map<Int, GuiEntry> = emptyMap()

    /** Slots available for the [content] (inner area, without the border). */
    protected open fun contentSlots(): List<Int> {
        val rows = this.section.rows.coerceIn(1, 6)
        val slots = ArrayList<Int>()
        for (row in 1 until rows - 1) {
            for (column in 1 until 8) {
                slots.add(row * 9 + column)
            }
        }
        return slots
    }

    final override fun build() {
        val slots = this.contentSlots()
        val perPage = slots.size.coerceAtLeast(1)
        val entries = this.content()
        val pages = if (entries.isEmpty()) 1 else (entries.size + perPage - 1) / perPage
        this.page = this.page.coerceIn(0, pages - 1)

        entries.drop(this.page * perPage).take(perPage).forEachIndexed { index, entry ->
            if (index < slots.size) {
                this.setItem(slots[index], entry.item, entry.action)
            }
        }

        val base = this.size - 9
        val placeholders = mapOf("page" to (this.page + 1).toString(), "pages" to pages.toString())

        if (this.page > 0) {
            this.setItem(this.slot("previousPage", base), this.guiItem("previousPage", placeholders)) { player ->
                this.page -= 1
                this.open(player)
            }
        }
        if (this.page < pages - 1) {
            this.setItem(this.slot("nextPage", base + 8), this.guiItem("nextPage", placeholders)) { player ->
                this.page += 1
                this.open(player)
            }
        }

        this.setItem(this.slot("pageInfo", base + 4), this.guiItem("pageInfo", placeholders))

        this.controls().forEach { (slot, entry) -> this.setItem(slot, entry.item, entry.action) }
    }
}
