package eu.wejsonekk.skygen.skygengenerator.util

import org.bukkit.inventory.ItemStack
import java.util.Base64

/**
 * Compact, single line (de)serialization of [ItemStack]s for persistence.
 *
 * Uses Bukkit's [ItemStack.serializeAsBytes] / [ItemStack.deserializeBytes] and
 * encodes the raw NBT bytes as Base64, so the whole item (name, lore, enchants,
 * NBT, ...) is stored exactly and safely in a text column.
 */
object ItemStackBytesCodec {

    fun encode(item: ItemStack): String =
        Base64.getEncoder().encodeToString(item.serializeAsBytes())

    fun decode(raw: String?): ItemStack? {
        if (raw.isNullOrBlank()) {
            return null
        }

        return runCatching {
            ItemStack.deserializeBytes(Base64.getDecoder().decode(raw.trim()))
        }.getOrNull()
    }
}
