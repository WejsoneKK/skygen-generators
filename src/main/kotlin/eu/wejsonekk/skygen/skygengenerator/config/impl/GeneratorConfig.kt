package eu.wejsonekk.skygen.skygengenerator.config.impl

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment
import eu.wejsonekk.skygen.skygengenerator.generator.hologram.HologramProvider
import java.time.Duration

/**
 * Generator behaviour (`generators.yml`).
 */
class GeneratorConfig : OkaeriConfig() {

    @Comment("Promien (w blokach), w jakim musi znajdowac sie gracz, aby generator produkowal")
    var activationRadius: Double = 16.0

    @Comment("Czy generator ma produkowac wylacznie gdy gracz jest w poblizu")
    var requireNearbyPlayer: Boolean = true

    @Comment("Jak czesto sprawdzany jest stan generatorow")
    var checkInterval: Duration = Duration.ofSeconds(1)

    @Comment("Maksymalny mnoznik boosta (zabezpieczenie)")
    var maxBoostMultiplier: Double = 10.0

    @Comment("Czy zapisywac generatory do bazy przy wylaczaniu pluginu")
    var saveOnShutdown: Boolean = true

    @Comment("Czy nad generatorem ma pojawiac sie hologram")
    var hologramEnabled: Boolean = true

    @Comment("Dostawca hologramow: AUTO (FancyHolograms jesli dostepne, inaczej wbudowany), TEXT_DISPLAY (wbudowany), FANCY_HOLOGRAMS")
    var hologramProvider: HologramProvider = HologramProvider.AUTO

    @Comment("Wysokosc hologramu nad generatorem (w blokach)")
    var hologramHeight: Double = 1.75

    @Comment("Linia hologramu opisujaca generator. Placeholdery: {id}, {material}, {amount}")
    var hologramGeneratorLine: String = "<yellow>{id} <gray>({material} x{amount})"

    @Comment("Linia hologramu z odliczaniem do nastepnej generacji. Placeholder: {time}")
    var hologramRespawnLine: String = "<gray>Nastepny resp za <white>{time}"

    @Comment("Linia hologramu gdy generator jest wylaczony")
    var hologramDisabledLine: String = "<red>Wylaczony"

    @Comment("Linia hologramu gdy brak gracza w poblizu (generator wstrzymany)")
    var hologramWaitingLine: String = "<red>Oczekiwanie na gracza"
}
