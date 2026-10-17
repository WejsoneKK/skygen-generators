package eu.wejsonekk.skygen.skygengenerator.generator.adapter

import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import org.bukkit.inventory.ItemStack

/**
 * Adapter between the generator domain and the Bukkit item model. Drops the exact
 * item template stored on the generator (name, lore, enchantments, ...), not just
 * its material.
 */
class GeneratorItemAdapter {

    fun toItemStack(generator: Generator, amount: Int = generator.amount): ItemStack {
        val item = generator.item.clone()
        item.amount = amount.coerceIn(1, item.maxStackSize)
        return item
    }
}
