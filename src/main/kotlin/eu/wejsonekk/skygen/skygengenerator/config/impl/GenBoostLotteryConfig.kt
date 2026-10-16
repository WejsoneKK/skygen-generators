package eu.wejsonekk.skygen.skygengenerator.config.impl

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment
import java.time.Duration

/**
 * Settings of the periodic global boost lottery.
 */
class GenBoostLotteryConfig : OkaeriConfig() {

    @Comment("Czy losowanie boostow jest wlaczone")
    var enabled: Boolean = true

    @Comment("Co ile czasu losowany jest boost")
    var interval: Duration = Duration.ofMinutes(5)

    @Comment("Czy ogloszenia maja trafiac do wszystkich graczy (false = tylko w poblizu generatorow)")
    var announceToAll: Boolean = true

    @Comment("Czy licznik do nastepnego losowania ma byc pokazywany na bossbarze")
    var countdownBossBar: Boolean = true

    @Comment("Od kiedy (przed losowaniem) ma pojawiac sie bossbar z odliczaniem")
    var countdownThreshold: Duration = Duration.ofMinutes(5)

    @Comment("Pula presetow do losowania (losowanie wg wagi)")
    var presets: MutableMap<String, GenBoostPresetConfig> = linkedMapOf(
        "small" to GenBoostPresetConfig().apply {
            this.name = "Small Boost"
            this.multiplier = 1.5
            this.duration = Duration.ofMinutes(5)
            this.weight = 60
        },
        "big" to GenBoostPresetConfig().apply {
            this.name = "Big Boost"
            this.multiplier = 2.0
            this.duration = Duration.ofMinutes(3)
            this.weight = 30
        },
        "legendary" to GenBoostPresetConfig().apply {
            this.name = "Legendary Boost"
            this.multiplier = 3.0
            this.duration = Duration.ofMinutes(1)
            this.weight = 10
        },
    )
}
