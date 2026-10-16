package eu.wejsonekk.skygen.skygengenerator.config.impl

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment

/**
 * Root gen boost configuration (`genboost.yml`).
 */
class GenBoostConfig : OkaeriConfig() {

    @Comment("Ustawienia losowania globalnych boostow")
    var lottery: GenBoostLotteryConfig = GenBoostLotteryConfig()

    @Comment("Itemy reprezentujace fizyczne genboosty")
    var items: BoostItemsConfig = BoostItemsConfig()
}
