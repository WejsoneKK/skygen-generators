package eu.wejsonekk.skygen.skygengenerator.generator.gui

import com.cryptomorin.xseries.XMaterial
import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.config.impl.GeneratorConfig
import eu.wejsonekk.skygen.skygengenerator.generator.service.GeneratorService
import eu.wejsonekk.skygen.skygengenerator.gui.GuiEntry
import eu.wejsonekk.skygen.skygengenerator.gui.GuiItems
import eu.wejsonekk.skygen.skygengenerator.gui.GuiLayout
import eu.wejsonekk.skygen.skygengenerator.gui.PaginatedGui
import eu.wejsonekk.skygen.skygengenerator.util.DurationFormat
import eu.wejsonekk.skygen.skygengenerator.util.Text
import org.bukkit.inventory.ItemStack

/**
 * Paginated list of every generator. Every element (background, navigation,
 * sort/filter controls, generator entry) is fully configurable in `gui.yml`.
 */
class GeneratorListGui(
    private val service: GeneratorService,
    private val generators: List<Generator>,
    private val generatorConfig: GeneratorConfig,
) : PaginatedGui(GuiItems.list(), Text.parse(GuiItems.list().title)) {

    private var sortMode: SortMode = SortMode.ID
    private var materialFilter: XMaterial? = null

    override fun layout(): GuiLayout = GuiLayout.bordered(this.rows, this.guiItem("filler"), this.guiItem("accent"))

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
                GeneratorEditGui(this.service, generator, this.generatorConfig).open(player)
            }
        }
    }

    override fun controls(): Map<Int, GuiEntry> {
        val base = this.size - 9
        return mapOf(
            this.slot("sort", base + 1) to GuiEntry(this.sortItem()) { player ->
                this.sortMode = this.sortMode.next()
                this.open(player)
            },
            this.slot("filter", base + 2) to GuiEntry(this.filterItem()) { player ->
                this.materialFilter = this.nextFilter()
                this.open(player)
            },
            this.slot("close", base + 7) to GuiEntry(this.guiItem("close")) { player -> player.closeInventory() },
        )
    }

    private fun item(generator: Generator): ItemStack = this.guiItem(
        "generator",
        mapOf(
            "id" to generator.id,
            "material" to generator.material.name,
            "amount" to generator.amount.toString(),
            "interval" to DurationFormat.format(generator.interval),
            "owner" to (generator.ownerId?.toString() ?: "brak"),
            "world" to generator.location.world,
            "x" to generator.location.x.toString(),
            "y" to generator.location.y.toString(),
            "z" to generator.location.z.toString(),
            "enabled" to if (generator.enabled) "<green>tak" else "<red>nie",
        ),
    )

    private fun sortItem(): ItemStack =
        this.guiItem("sort", mapOf("sort" to this.sortMode.displayName()))

    private fun filterItem(): ItemStack =
        this.guiItem("filter", mapOf("filter" to (this.materialFilter?.name ?: "wszystkie")))

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
