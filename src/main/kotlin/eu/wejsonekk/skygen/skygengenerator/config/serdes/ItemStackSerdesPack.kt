package eu.wejsonekk.skygen.skygengenerator.config.serdes

import eu.okaeri.configs.schema.GenericsDeclaration
import eu.okaeri.configs.serdes.DeserializationData
import eu.okaeri.configs.serdes.ObjectSerializer
import eu.okaeri.configs.serdes.OkaeriSerdes
import eu.okaeri.configs.serdes.SerdesRegistry
import eu.okaeri.configs.serdes.SerializationData
import eu.wejsonekk.skygen.skygengenerator.util.XItemStackCodec
import org.bukkit.inventory.ItemStack

/**
 * Okaeri serdes pack that (de)serializes [ItemStack] fields with XSeries'
 * XItemStack instead of the version bound Bukkit serializer.
 *
 * Items are stored as a readable, flat map (material, amount, name, lore,
 * enchants, skull, custom-model-data, ...) so player heads, enchantments and
 * lore can all be configured separately while staying cross-version.
 *
 * Must be registered *after* [eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit]
 * because serializers are resolved in reverse registration order (last wins).
 */
class ItemStackSerdesPack : OkaeriSerdes {

    override fun register(registry: SerdesRegistry) {
        registry.registerExclusive(ItemStack::class.java, ItemStackXSeriesSerializer())
    }
}

private class ItemStackXSeriesSerializer : ObjectSerializer<ItemStack> {

    override fun supports(type: Class<*>): Boolean = ItemStack::class.java.isAssignableFrom(type)

    override fun serialize(item: ItemStack, data: SerializationData, generics: GenericsDeclaration) {
        XItemStackCodec.encodeToMap(item).forEach { (key, value) -> data.set(key, value) }
    }

    override fun deserialize(data: DeserializationData, generics: GenericsDeclaration): ItemStack? {
        if (data.isValue) {
            (data.valueRaw as? String)?.let { raw -> return XItemStackCodec.decode(raw) }
        }
        return XItemStackCodec.decodeFromMap(data.asMap())
    }
}
