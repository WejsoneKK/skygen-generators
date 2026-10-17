package eu.wejsonekk.skygen.skygengenerator.config.impl

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment
import org.bukkit.inventory.ItemStack

/**
 * Item templates used as the physical representation of gen boosts.
 * Serialized with XSeries (XItemStack) so they survive server version changes.
 * Configured in game through `/genboost set-item <personal|global>`.
 */
class BoostItemsConfig : OkaeriConfig() {

    @Comment("Item reprezentujacy fizyczny personalny GenBoost (null = domyslny)")
    var personal: ItemStack? = null

    @Comment("Item reprezentujacy fizyczny globalny GenBoost (null = domyslny)")
    var global: ItemStack? = null

    @Comment("Czy fizyczne GenBoosty mozna aktywowac takze klikajac w powietrzu")
    var allowAirActivation: Boolean = true
}
