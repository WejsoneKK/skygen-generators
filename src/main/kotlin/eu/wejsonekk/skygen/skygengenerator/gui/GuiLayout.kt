package eu.wejsonekk.skygen.skygengenerator.gui

import org.bukkit.inventory.ItemStack

/**
 * A chest GUI layout template. The [pattern] is a grid of characters where every
 * symbol is mapped to an [ItemStack] through [ingredient]. Filler is applied only
 * on empty slots, so real content can overwrite it.
 *
 * Example:
 * ```
 * GuiLayout.builder()
 *     .pattern(
 *         "#########",
 *         "#       #",
 *         "#########",
 *     )
 *     .ingredient('#', GuiItems.accent())
 *     .ingredient(' ', GuiItems.filler())
 *     .build()
 * ```
 */
class GuiLayout private constructor(
    private val pattern: List<String>,
    private val ingredients: Map<Char, ItemStack>,
) {

    fun apply(gui: AbstractGui) {
        this.pattern.forEachIndexed { row, line ->
            line.forEachIndexed { column, symbol ->
                val item = this.ingredients[symbol] ?: return@forEachIndexed
                gui.fillSlot(row * 9 + column, item)
            }
        }
    }

    class Builder {

        private val pattern = mutableListOf<String>()
        private val ingredients = mutableMapOf<Char, ItemStack>()

        fun pattern(vararg rows: String): Builder = apply {
            this.pattern.clear()
            this.pattern.addAll(rows)
        }

        fun ingredient(symbol: Char, item: ItemStack): Builder = apply {
            this.ingredients[symbol] = item
        }

        fun build(): GuiLayout = GuiLayout(this.pattern.toList(), this.ingredients.toMap())
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()

        fun fullBackground(rows: Int, filler: ItemStack): GuiLayout =
            builder()
                .pattern(*Array(rows) { "#".repeat(9) })
                .ingredient('#', filler)
                .build()

        fun bordered(rows: Int, filler: ItemStack, accent: ItemStack): GuiLayout {
            val rowsPattern = (0 until rows).map { row ->
                buildString {
                    for (column in 0 until 9) {
                        val border = row == 0 || row == rows - 1 || column == 0 || column == 8
                        append(if (border) '#' else '-')
                    }
                }
            }
            return builder()
                .pattern(*rowsPattern.toTypedArray())
                .ingredient('#', accent)
                .ingredient('-', filler)
                .build()
        }
    }
}
