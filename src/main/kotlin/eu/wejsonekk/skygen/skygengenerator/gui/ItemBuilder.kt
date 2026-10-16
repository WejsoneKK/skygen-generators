package eu.wejsonekk.skygen.skygengenerator.gui

import com.cryptomorin.xseries.XMaterial
import com.destroystokyo.paper.profile.ProfileProperty
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID

/**
 * Fluent builder for GUI [ItemStack]s. Names and lore are parsed with MiniMessage.
 * Uses [XMaterial] so the same code works across many server versions.
 */
class ItemBuilder private constructor(
    private val miniMessage: MiniMessage,
    private val item: ItemStack,
) {

    private val meta = item.itemMeta
    private val loreLines = mutableListOf<Component>()

    companion object {
        private const val TEXTURES = "textures"

        @JvmStatic
        fun of(miniMessage: MiniMessage, material: XMaterial, amount: Int = 1): ItemBuilder {
            val item = material.parseItem() ?: ItemStack(Material.STONE)
            item.amount = amount.coerceIn(1, item.maxStackSize)
            return ItemBuilder(miniMessage, item)
        }

        @JvmStatic
        fun of(miniMessage: MiniMessage, item: ItemStack): ItemBuilder =
            ItemBuilder(miniMessage, item.clone())
    }

    fun amount(amount: Int): ItemBuilder = apply {
        this.item.amount = amount.coerceIn(1, this.item.maxStackSize)
    }

    fun name(name: String): ItemBuilder = apply {
        this.meta?.displayName(this.parse(name))
    }

    fun lore(vararg lines: String): ItemBuilder = apply {
        lines.forEach { this.loreLines += this.parse(it) }
    }

    fun lore(lines: List<String>): ItemBuilder = this.lore(*lines.toTypedArray())

    /** Parses [text] as MiniMessage without the tooltip's default italic style. */
    private fun parse(text: String): Component = this.miniMessage.deserialize("<!italic>$text")

    /** Applies a base64 player head texture (only works on player heads). */
    fun texture(base64: String): ItemBuilder = apply {
        val skull = this.meta as? SkullMeta ?: return@apply
        if (base64.isNotBlank()) {
            val profile = Bukkit.createProfile(UUID.randomUUID(), null)
            profile.setProperty(ProfileProperty(TEXTURES, base64))
            skull.playerProfile = profile
        }
    }

    fun build(): ItemStack {
        val meta = this.meta ?: return this.item
        if (this.loreLines.isNotEmpty()) {
            meta.lore(this.loreLines)
        }
        this.item.itemMeta = meta
        return this.item
    }
}
