package eu.wejsonekk.skygen.skygengenerator.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage

/**
 * A 1..6 row GUI able to paginate [content]. Only the inner slots (everything
 * except the outer border and the bottom row) are used for content, so a
 * bordered background (see [GuiLayout.bordered]) stays visible.
 *
 * The bottom row is reserved for the navigation heads, page info and the
 * additional [controls].
 */
abstract class PaginatedGui(
    rows: Int,
    title: Component,
    protected val miniMessage: MiniMessage,
) : AbstractGui(rows, title) {

    protected var page: Int = 0

    protected abstract fun content(): List<GuiEntry>

    protected open fun controls(): Map<Int, GuiEntry> = emptyMap()

    /** Slots available for the [content] (inner area, without the border). */
    protected open fun contentSlots(): List<Int> {
        val slots = ArrayList<Int>()
        for (row in 1 until this.rows - 1) {
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
        if (this.page > 0) {
            this.setItem(base, GuiItems.previousPage()) { player -> this.page -= 1; this.open(player) }
        }
        if (this.page < pages - 1) {
            this.setItem(base + 8, GuiItems.nextPage()) { player -> this.page += 1; this.open(player) }
        }

        this.setItem(base + 4, GuiItems.pageInfo(this.page + 1, pages))

        this.controls().forEach { (slot, entry) -> this.setItem(slot, entry.item, entry.action) }
    }
}
