package eu.wejsonekk.skygen.skygengenerator.config.impl

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment

/**
 * Item templates used as the physical representation of gen boosts.
 * Serialized with XSeries (XItemStack) so they survive server version changes.
 * Configured in game through `/genboost set-item <personal|global>`.
 */
class BoostItemsConfig : OkaeriConfig() {

    @Comment("Zserializowany (XItemStack) item reprezentujacy fizyczny personalny GenBoost")
    var personal: String = ""

    @Comment("Zserializowany (XItemStack) item reprezentujacy fizyczny globalny GenBoost")
    var global: String = ""
}
