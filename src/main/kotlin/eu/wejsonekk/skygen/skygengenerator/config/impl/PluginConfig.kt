package eu.wejsonekk.skygen.skygengenerator.config.impl

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment
import java.time.Duration

/**
 * General plugin configuration (`config.yml`).
 */
class PluginConfig : OkaeriConfig() {

    @Comment("Wlacz dodatkowe logi debug")
    var debug: Boolean = false

    @Comment("Jak czesto generatory sa zapisywane do bazy danych")
    var saveInterval: Duration = Duration.ofMinutes(5)

    @Comment("Domyslny typ generatora uzywany przez komende /wskygen create")
    var defaultType: String = "stone"

    @Comment("Czy dozwolone jest stawianie wielu generatorow w tym samym miejscu")
    var allowStackedGenerators: Boolean = false
}
