package eu.wejsonekk.skygen.skygengenerator.util

import com.cryptomorin.xseries.XItemStack
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import java.io.StringReader

/**
 * Version independent (de)serialization of [ItemStack]s using XSeries'
 * [XItemStack.serializer] / [XItemStack.deserializer] builders (the static
 * `serialize`/`deserialize` helpers are deprecated).
 *
 * Values are stored as a YAML string so they safely survive server updates.
 */
object XItemStackCodec {

    private const val ROOT = "item"

    fun encode(item: ItemStack): String {
        val config = YamlConfiguration()
        val section = config.createSection(ROOT)
        XItemStack.serializer()
            .fromItem(item)
            .toConfig(section)
            .serialize()
        return config.saveToString()
    }

    fun decode(raw: String?): ItemStack? {
        if (raw.isNullOrBlank()) {
            return null
        }

        return runCatching {
            val config = YamlConfiguration.loadConfiguration(StringReader(raw))
            val section = config.getConfigurationSection(ROOT) ?: return null
            XItemStack.deserializer()
                .fromConfig(section)
                .deserialize()
        }.getOrNull()
    }
}
