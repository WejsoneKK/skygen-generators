package eu.wejsonekk.skygen.skygengenerator.generator.gui

import com.cryptomorin.xseries.XMaterial
import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.generator.service.GeneratorService
import eu.wejsonekk.skygen.skygengenerator.gui.GuiEntry
import eu.wejsonekk.skygen.skygengenerator.gui.GuiItems
import eu.wejsonekk.skygen.skygengenerator.gui.GuiLayout
import eu.wejsonekk.skygen.skygengenerator.gui.ItemBuilder
import eu.wejsonekk.skygen.skygengenerator.gui.PaginatedGui
import eu.wejsonekk.skygen.skygengenerator.util.DurationFormat
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.ItemStack

/**
 * Paginated list of every generator with sort & filter controls.
 */
class GeneratorListGui(
    private val service: GeneratorService,
    miniMessage: MiniMessage,
    private val generators: List<Generator>,
) : PaginatedGui(6, miniMessage.deserialize("<dark_gray>Generatory"), miniMessage) {

    private var sortMode: SortMode = SortMode.ID
    private var materialFilter: XMaterial? = null

    override fun layout(): GuiLayout = GuiLayout.bordered(6, GuiItems.filler(), GuiItems.accent())

    override fun content(): List<GuiEntry> {
        val filtered = this.generators.filter {
            this.materialFilter == null || it.material == this.materialFilter
        }

        val sorted = when (this.sortMode) {
            SortMode.ID -> filtered.sortedBy { it.id.lowercase() }
            SortMode.MATERIAL -> filtered.sortedBy { it.material.name }
            SortMode.AMOUNT -> filtered.sortedByDescending { it.amount }
            SortMode.INTERVAL -> filtered.sortedBy { it.interval.toMillis() }
        }

        return sorted.map { generator ->
            GuiEntry(this.item(generator)) { player ->
                GeneratorEditGui(this.service, this.miniMessage, generator).open(player)
            }
        }
    }

    override fun controls(): Map<Int, GuiEntry> {
        val base = this.size - 9
        return mapOf(
            base + 1 to GuiEntry(this.sortItem()) { player ->
                this.sortMode = this.sortMode.next()
                this.open(player)
            },
            base + 2 to GuiEntry(this.filterItem()) { player ->
                this.materialFilter = this.nextFilter()
                this.open(player)
            },
            base + 7 to GuiEntry(GuiItems.close()) { player -> player.closeInventory() },
        )
    }

    private fun item(generator: Generator): ItemStack {
        val owner = generator.ownerId?.toString() ?: "brak"
        val enabled = if (generator.enabled) "<green>tak" else "<red>nie"

        return ItemBuilder.of(this.miniMessage, generator.material)
            .amount(generator.amount.coerceIn(1, 64))
            .name("<white>${generator.id}")
            .lore(
                "<gray>Item: <white>${generator.material} <gray>x<white>${generator.amount}",
                "<gray>Interwal: <white>${DurationFormat.format(generator.interval)}",
                "<gray>Lokalizacja: <white>${generator.location.world} ${generator.location.x},${generator.location.y},${generator.location.z}",
                "<gray>Wlasciciel: <white>$owner",
                "<gray>Wlaczony: $enabled",
                "",
                "<yellow>Kliknij, aby edytowac",
            )
            .build()
    }

    private fun sortItem(): ItemStack = ItemBuilder.of(this.miniMessage, XMaterial.HOPPER)
        .name("<gold>Sortowanie: <white>${this.sortMode.displayName()}")
        .lore("<gray>Kliknij, aby zmienic")
        .build()

    private fun filterItem(): ItemStack = ItemBuilder.of(this.miniMessage, XMaterial.HOPPER)
        .name("<gold>Filtr: <white>${this.materialFilter?.name ?: "wszystkie"}")
        .lore("<gray>Kliknij, aby zmienic")
        .build()

    private fun nextFilter(): XMaterial? {
        val materials = this.generators.map { it.material }.distinct().sortedBy { it.name }
        if (materials.isEmpty()) {
            return null
        }

        val index = materials.indexOf(this.materialFilter)
        return when {
            index == -1 -> materials.first()
            index == materials.size - 1 -> null
            else -> materials[index + 1]
        }
    }

    private enum class SortMode(private val label: String) {
        ID("ID"),
        MATERIAL("Material"),
        AMOUNT("Ilosc"),
        INTERVAL("Interwal"),
        ;

        fun next(): SortMode = entries[(this.ordinal + 1) % entries.size]

        fun displayName(): String = this.label
    }
}
