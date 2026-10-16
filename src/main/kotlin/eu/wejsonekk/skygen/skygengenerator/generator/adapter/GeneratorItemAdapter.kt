package eu.wejsonekk.skygen.skygengenerator.generator.adapter

import com.cryptomorin.xseries.XMaterial
import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Adapter between the generator domain and the Bukkit item model. Uses [XMaterial]
 * so generators keep working across server versions where materials were changed.
 */
class GeneratorItemAdapter {

    fun toItemStack(generator: Generator, amount: Int = generator.amount): ItemStack {
        val item = generator.material.parseItem()
            ?: XMaterial.STONE.parseItem()
            ?: ItemStack(Material.STONE)
        item.amount = amount.coerceIn(1, item.maxStackSize)
        return item
    }
}
