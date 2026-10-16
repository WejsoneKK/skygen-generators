package eu.wejsonekk.skygen.skygengenerator.generator.gui

import com.cryptomorin.xseries.XMaterial
import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.generator.model.GeneratorImpl
import eu.wejsonekk.skygen.skygengenerator.generator.service.GeneratorService
import eu.wejsonekk.skygen.skygengenerator.gui.AbstractGui
import eu.wejsonekk.skygen.skygengenerator.gui.GuiItems
import eu.wejsonekk.skygen.skygengenerator.gui.GuiLayout
import eu.wejsonekk.skygen.skygengenerator.gui.ItemBuilder
import eu.wejsonekk.skygen.skygengenerator.util.DurationFormat
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.time.Duration

/**
 * Editor for a single generator. Every button immediately persists the change
 * and re-renders the GUI.
 */
class GeneratorEditGui(
    private val service: GeneratorService,
    private val miniMessage: MiniMessage,
    private var generator: Generator,
) : AbstractGui(3, miniMessage.deserialize("<dark_gray>Generator <white>${generator.id}")) {

    override fun layout(): GuiLayout = GuiLayout.bordered(3, GuiItems.filler(), GuiItems.accent())

    override fun build() {
        this.setItem(10, this.infoItem())
        this.setItem(11, this.toggleItem()) { player -> this.apply(this.copy(enabled = !this.generator.enabled), player) }
        this.setItem(12, this.amountItem(1)) { player -> this.apply(this.copy(amount = this.generator.amount + 1), player) }
        this.setItem(13, this.amountItem(-1)) { player -> this.apply(this.copy(amount = (this.generator.amount - 1).coerceAtLeast(1)), player) }
        this.setItem(14, this.intervalItem(30)) { player -> this.apply(this.copy(interval = this.generator.interval.plusSeconds(30)), player) }
        this.setItem(15, this.intervalItem(-30)) { player -> this.apply(this.copy(interval = this.shorten()), player) }
        this.setItem(16, this.materialItem()) { player -> this.apply(this.copy(material = XMaterial.matchXMaterial(player.inventory.itemInMainHand.type)), player) }

        this.backButton(18)
        this.setItem(22, this.deleteItem()) { player -> this.delete(player) }
        this.setItem(26, GuiItems.close()) { player -> player.closeInventory() }
    }

    private fun apply(updated: Generator, player: Player) {
        this.service.update(updated)
        this.generator = updated
        this.open(player)
    }

    private fun delete(player: Player) {
        this.service.removeGenerator(this.generator.id)
        this.back(player)
    }

    private fun copy(
        material: XMaterial = this.generator.material,
        amount: Int = this.generator.amount,
        interval: Duration = this.generator.interval,
        enabled: Boolean = this.generator.enabled,
    ): Generator = GeneratorImpl(
        id = this.generator.id,
        material = material,
        amount = amount.coerceAtLeast(1),
        interval = interval,
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

    private fun infoItem(): ItemStack {
        val owner = this.generator.ownerId?.toString() ?: "brak"
        return ItemBuilder.of(this.miniMessage, XMaterial.PAPER)
            .name("<gold>Informacje")
            .lore(
                "<gray>ID: <white>${this.generator.id}",
                "<gray>Item: <white>${this.generator.material} <gray>x<white>${this.generator.amount}",
                "<gray>Interwal: <white>${DurationFormat.format(this.generator.interval)}",
                "<gray>Wlasciciel: <white>$owner",
            )
            .build()
    }

    private fun toggleItem(): ItemStack = this.named(
        XMaterial.LEVER,
        "<gold>Stan: ${if (this.generator.enabled) "<green>wlaczony" else "<red>wylaczony"}",
    )

    private fun amountItem(delta: Int): ItemStack = this.named(
        if (delta > 0) XMaterial.LIME_DYE else XMaterial.RED_DYE,
        if (delta > 0) "<green>Ilosc +1" else "<red>Ilosc -1",
    )

    private fun intervalItem(delta: Int): ItemStack = this.named(
        if (delta > 0) XMaterial.LIME_DYE else XMaterial.RED_DYE,
        if (delta > 0) "<green>Interwal +30s" else "<red>Interwal -30s",
    )

    private fun materialItem(): ItemStack = ItemBuilder.of(this.miniMessage, XMaterial.PAPER)
        .name("<gold>Zmien material")
        .lore("<gray>Na item trzymany w dloni")
        .build()

    private fun deleteItem(): ItemStack = this.named(XMaterial.BARRIER, "<red>Usun generator")

    private fun named(material: XMaterial, name: String): ItemStack =
        ItemBuilder.of(this.miniMessage, material).name(name).build()
}
