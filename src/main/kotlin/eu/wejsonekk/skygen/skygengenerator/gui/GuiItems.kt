package eu.wejsonekk.skygen.skygengenerator.gui

import com.cryptomorin.xseries.XMaterial
import eu.wejsonekk.skygen.skygengenerator.config.impl.GuiConfig
import eu.wejsonekk.skygen.skygengenerator.config.impl.GuiItemConfig
import eu.wejsonekk.skygen.skygengenerator.config.impl.GuiSectionConfig
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

/**
 * Turns a [GuiItemConfig] into a ready to use [ItemStack], applying the GUI
 * specific `{...}` placeholders (PAPI placeholders are resolved by [ItemBuilder]).
 */
object GuiItems {

    private lateinit var config: GuiConfig

    fun init(config: GuiConfig) {
        this.config = config
    }

    fun list(): GuiSectionConfig = this.config.list

    fun editor(): GuiSectionConfig = this.config.editor

    fun build(
        item: GuiItemConfig?,
        viewer: Player?,
        placeholders: Map<String, String> = emptyMap(),
        override: ItemStack? = null,
    ): ItemStack {
        val definition = item ?: GuiItemConfig()
        val base = override?.clone() ?: definition.item?.clone()

        val builder = if (base != null) {
            ItemBuilder.of(base, viewer)
        } else {
            val material = XMaterial.matchXMaterial(definition.material).orElse(XMaterial.STONE)
            ItemBuilder.of(material, definition.amount, viewer)
        }
            .texture(definition.texture)
            .glow(definition.glow)
            .customModelData(definition.customModelData)

        val name = apply(definition.name, placeholders)
        if (name.isNotBlank()) {
            builder.name(name)
        }

        val lore = definition.lore.map { apply(it, placeholders) }
        if (lore.isNotEmpty()) {
            builder.lore(lore)
        }

        return builder.build()
    }

    fun apply(text: String, placeholders: Map<String, String>): String {
        if (placeholders.isEmpty()) {
            return text
        }
        var result = text
        placeholders.forEach { (key, value) -> result = result.replace("{$key}", value) }
        return result
    }
}
