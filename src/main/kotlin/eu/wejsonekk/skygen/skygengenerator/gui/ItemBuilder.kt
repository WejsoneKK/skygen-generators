package eu.wejsonekk.skygen.skygengenerator.gui

import com.cryptomorin.xseries.XMaterial
import com.destroystokyo.paper.profile.ProfileProperty
import eu.wejsonekk.skygen.skygengenerator.util.Text
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.UUID

/**
 * Fluent builder for GUI [ItemStack]s. Names and lore are parsed with MiniMessage
 * ([Text]) and support PlaceholderAPI placeholders for the target [viewer].
 * Uses [XMaterial] so the same code works across many server versions.
 */
class ItemBuilder private constructor(
    private val item: ItemStack,
    private val viewer: Player?,
) {

    private val meta = item.itemMeta
    private val loreLines = mutableListOf<Component>()

    companion object {
        private const val TEXTURES = "textures"

        @JvmStatic
        fun of(material: XMaterial, amount: Int = 1, viewer: Player? = null): ItemBuilder {
            val item = material.parseItem() ?: ItemStack(Material.STONE)
            item.amount = amount.coerceIn(1, item.maxStackSize)
            return ItemBuilder(item, viewer)
        }

        @JvmStatic
        fun of(item: ItemStack, viewer: Player? = null): ItemBuilder =
            ItemBuilder(item.clone(), viewer)
    }

    fun amount(amount: Int): ItemBuilder = apply {
        this.item.amount = amount.coerceIn(1, this.item.maxStackSize)
    }

    fun name(name: String): ItemBuilder = apply {
        if (name.isNotBlank()) {
            this.meta?.displayName(Text.parseItem(name, this.viewer))
        }
    }

    fun lore(vararg lines: String): ItemBuilder = apply {
        lines.forEach {
            if (it.isNotEmpty()) {
                this.loreLines += Text.parseItem(it, this.viewer)
            } else {
                this.loreLines += Component.empty()
            }
        }
    }

    fun lore(lines: List<String>): ItemBuilder = this.lore(*lines.toTypedArray())

    fun glow(glow: Boolean): ItemBuilder = apply {
        if (glow) {
            this.meta?.addEnchant(Enchantment.UNBREAKING, 1, true)
            this.meta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
    }

    fun customModelData(data: Int): ItemBuilder = apply {
        if (data > 0) {
            this.meta?.setCustomModelData(data)
        }
    }

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
