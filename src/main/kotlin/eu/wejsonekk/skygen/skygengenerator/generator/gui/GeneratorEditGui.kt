package eu.wejsonekk.skygen.skygengenerator.generator.gui

import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.config.impl.GeneratorConfig
import eu.wejsonekk.skygen.skygengenerator.generator.model.GeneratorImpl
import eu.wejsonekk.skygen.skygengenerator.generator.service.GeneratorService
import eu.wejsonekk.skygen.skygengenerator.gui.AbstractGui
import eu.wejsonekk.skygen.skygengenerator.gui.GuiItems
import eu.wejsonekk.skygen.skygengenerator.gui.GuiLayout
import eu.wejsonekk.skygen.skygengenerator.notice.NoticeService
import eu.wejsonekk.skygen.skygengenerator.util.DurationFormat
import eu.wejsonekk.skygen.skygengenerator.util.Text
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Duration

/**
 * Editor for a single generator. Every button immediately persists the change
 * and re-renders the GUI. Fully configurable through `gui.yml` (`guis.editor`).
 */
class GeneratorEditGui(
    private val service: GeneratorService,
    private var generator: Generator,
    private val generatorConfig: GeneratorConfig,
    private val noticeService: NoticeService,
) : AbstractGui(
    GuiItems.editor(),
    Text.parse(GuiItems.editor().title.replace("{id}", generator.id)),
) {

    override fun layout(): GuiLayout = GuiLayout.bordered(this.rows, this.guiItem("filler"), this.guiItem("accent"))

    override fun build() {
        val ph = this.placeholders()

        this.setItem(this.slot("info", 10), this.guiItem("info", ph))
        this.setItem(this.slot("toggle", 11), this.guiItem("toggle", ph)) { player ->
            this.apply(this.copy(enabled = !this.generator.enabled), player)
        }
        this.setItem(this.slot("amountIncrease", 12), this.guiItem("amountIncrease", ph)) { player ->
            this.apply(this.copy(amount = this.generator.amount + 1), player)
        }
        this.setItem(this.slot("amountDecrease", 13), this.guiItem("amountDecrease", ph)) { player ->
            this.apply(this.copy(amount = (this.generator.amount - 1).coerceAtLeast(1)), player)
        }
        this.setItem(this.slot("intervalIncrease", 14), this.guiItem("intervalIncrease", ph)) { player ->
            this.apply(this.copy(interval = this.generator.interval.plusSeconds(30)), player)
        }
        this.setItem(this.slot("intervalDecrease", 15), this.guiItem("intervalDecrease", ph)) { player ->
            this.apply(this.copy(interval = this.shorten()), player)
        }
        this.setItem(
            this.slot("changeItem", 16),
            GuiItems.build(
                this.section.items["changeItem"],
                this.viewer,
                ph,
                this.generator.item.clone().apply { amount = 1 },
            ),
        ) { player -> this.changeItem(player) }
        this.setItem(this.slot("radiusIncrease", 20), this.guiItem("radiusIncrease", ph)) { player ->
            this.apply(this.copy(activationRadius = this.currentRadius() + 1.0), player)
        }
        this.setItem(this.slot("radiusDecrease", 19), this.guiItem("radiusDecrease", ph)) { player ->
            this.apply(this.copy(activationRadius = (this.currentRadius() - 1.0).coerceAtLeast(1.0)), player)
        }

        this.backButton(27)
        this.setItem(this.slot("delete", 31), this.guiItem("delete", ph)) { player -> this.delete(player) }
        this.setItem(this.slot("close", 35), this.guiItem("close", ph)) { player -> player.closeInventory() }
    }

    private fun placeholders(): Map<String, String> = mapOf(
        "id" to this.generator.id,
        "item" to this.itemName(),
        "material" to this.generator.material.name,
        "amount" to this.generator.amount.toString(),
        "interval" to DurationFormat.format(this.generator.interval),
        "radius" to this.currentRadius().toString(),
        "owner" to (this.generator.ownerId?.toString() ?: "brak"),
        "enabled" to if (this.generator.enabled) "<green>włączony" else "<red>wyłączony",
    )

    private fun itemName(item: ItemStack = this.generator.item): String {
        val displayName = item.itemMeta?.displayName()
            ?: return this.generator.material.name
        return PlainTextComponentSerializer.plainText().serialize(displayName)
    }

    private fun currentRadius(): Double = this.generator.activationRadius ?: this.generatorConfig.activationRadius

    private fun changeItem(player: Player) {
        val held = player.inventory.itemInMainHand
        if (held.type.isAir) {
            this.noticeService.create()
                .viewer(player)
                .notice { it.generatorItemNoItem }
                .send()
            return
        }

        val template = held.clone().apply { amount = 1 }
        val updated = this.copy(item = template)
        this.service.update(updated)
        this.generator = updated
        this.refresh(player)

        this.noticeService.create()
            .viewer(player)
            .notice { it.generatorItemChanged }
            .placeholder("{id}", updated.id)
            .placeholder("{item}", itemName(updated.item))
            .send()
    }

    private fun apply(updated: Generator, player: Player) {
        this.service.update(updated)
        this.generator = updated
        this.refresh(player)
    }

    private fun delete(player: Player) {
        this.service.removeGenerator(this.generator.id)
        this.back(player)
    }

    private fun copy(
        item: ItemStack = this.generator.item,
        amount: Int = this.generator.amount,
        interval: Duration = this.generator.interval,
        activationRadius: Double? = this.generator.activationRadius,
        enabled: Boolean = this.generator.enabled,
    ): Generator = GeneratorImpl(
        id = this.generator.id,
        item = item.clone(),
        amount = amount.coerceAtLeast(1),
        interval = interval,
        activationRadius = activationRadius,
        ownerId = this.generator.ownerId,
        location = this.generator.location,
        enabled = enabled,
        createdAt = this.generator.createdAt,
        lastGenerationAt = this.generator.lastGenerationAt,
    )

    private fun shorten(): Duration {
        val shortened = this.generator.interval.minusSeconds(30)
        return if (shortened.isNegative || shortened.isZero) Duration.ofSeconds(1) else shortened
    }
}
