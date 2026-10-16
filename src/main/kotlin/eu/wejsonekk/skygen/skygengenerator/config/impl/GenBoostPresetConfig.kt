package eu.wejsonekk.skygen.skygengenerator.config.impl

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment
import java.time.Duration

/**
 * A single boost preset that can be drawn by the global lottery.
 */
class GenBoostPresetConfig : OkaeriConfig() {

    @Comment("Nazwa wyswietlana boosta")
    var name: String = "GenBoost"

    @Comment("Mnoznik generowanych przedmiotow (np. 1.5 = 150%)")
    var multiplier: Double = 1.5

    @Comment("Czas trwania boosta (np. 5m, 1h)")
    var duration: Duration = Duration.ofMinutes(5)

    @Comment("Waga losowania - im wyższa, tym wieksza szansa")
    var weight: Int = 10
}
