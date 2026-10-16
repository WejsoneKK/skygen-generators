package eu.wejsonekk.skygen.skygengenerator.genboost.adapter

import com.cryptomorin.xseries.XMaterial
import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoostType
import eu.wejsonekk.skygen.skygengenerator.config.impl.GenBoostConfig
import eu.wejsonekk.skygen.skygengenerator.util.DurationFormat
import eu.wejsonekk.skygen.skygengenerator.util.XItemStackCodec
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin
import java.time.Duration

/**
 * Creates and reads the physical ("click to activate") gen boost items.
 *
 * The visual template is stored (as an XItemStack string) in [GenBoostConfig],
 * while per item boost data lives inside the item's
 * [org.bukkit.persistence.PersistentDataContainer].
 */
class GenBoostItemAdapter(
    plugin: Plugin,
    private val config: GenBoostConfig,
    private val miniMessage: MiniMessage,
) {

    private val keyType = NamespacedKey(plugin, "genboost_type")
    private val keyMultiplier = NamespacedKey(plugin, "genboost_multiplier")
    private val keyDuration = NamespacedKey(plugin, "genboost_duration")
    private val keyName = NamespacedKey(plugin, "genboost_name")

    data class BoostData(
        val type: GenBoostType,
        val multiplier: Double,
        val duration: Duration,
        val name: String,
    )

    fun template(type: GenBoostType): ItemStack {
        val raw = if (type == GenBoostType.GLOBAL) this.config.items.global else this.config.items.personal
        return XItemStackCodec.decode(raw) ?: this.defaultTemplate(type)
    }

    fun applyTemplate(type: GenBoostType, item: ItemStack) {
        val template = item.clone()
        template.amount = 1
        val meta = template.itemMeta
        if (meta != null) {
            meta.persistentDataContainer.remove(this.keyType)
            meta.persistentDataContainer.remove(this.keyMultiplier)
            meta.persistentDataContainer.remove(this.keyDuration)
            meta.persistentDataContainer.remove(this.keyName)
            template.itemMeta = meta
        }

        val encoded = XItemStackCodec.encode(template)
        if (type == GenBoostType.GLOBAL) {
            this.config.items.global = encoded
        } else {
            this.config.items.personal = encoded
        }
    }

    fun create(type: GenBoostType, multiplier: Double, duration: Duration, name: String): ItemStack {
        val item = this.template(type).clone()
        val meta = item.itemMeta ?: return item

        meta.persistentDataContainer.set(this.keyType, PersistentDataType.STRING, type.name)
        meta.persistentDataContainer.set(this.keyMultiplier, PersistentDataType.DOUBLE, multiplier)
        meta.persistentDataContainer.set(this.keyDuration, PersistentDataType.LONG, duration.toMillis())
        meta.persistentDataContainer.set(this.keyName, PersistentDataType.STRING, name)

        val lore = (meta.lore() ?: emptyList()).toMutableList()
        if (lore.isNotEmpty()) {
            lore.add(Component.empty())
        }
        lore.add(this.miniMessage.deserialize("<gray>Mnoznik: <white>x$multiplier"))
        lore.add(this.miniMessage.deserialize("<gray>Czas: <white>${DurationFormat.format(duration)}"))
        lore.add(this.miniMessage.deserialize("<yellow>Kliknij prawym, aby aktywowac"))
        meta.lore(lore)

        item.itemMeta = meta
        return item
    }

    fun read(item: ItemStack?): BoostData? {
        if (item == null || item.type.isAir) {
            return null
        }
        val meta = item.itemMeta ?: return null
        val container = meta.persistentDataContainer

        val rawType = container.get(this.keyType, PersistentDataType.STRING) ?: return null
        val type = runCatching { GenBoostType.valueOf(rawType) }.getOrNull() ?: return null
        val multiplier = container.get(this.keyMultiplier, PersistentDataType.DOUBLE) ?: return null
        val durationMillis = container.get(this.keyDuration, PersistentDataType.LONG) ?: return null
        val name = container.get(this.keyName, PersistentDataType.STRING) ?: "GenBoost"

        return BoostData(type, multiplier, Duration.ofMillis(durationMillis), name)
    }

    private fun defaultTemplate(type: GenBoostType): ItemStack {
        val material = if (type == GenBoostType.GLOBAL) XMaterial.BEACON else XMaterial.NETHER_STAR
        return material.parseItem() ?: ItemStack(Material.STONE)
    }
}
