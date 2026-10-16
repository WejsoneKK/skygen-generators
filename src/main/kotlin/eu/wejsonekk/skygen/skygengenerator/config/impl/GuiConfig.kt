package eu.wejsonekk.skygen.skygengenerator.config.impl

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment

/**
 * Look & feel of the plugin GUIs (`gui.yml`).
 */
class GuiConfig : OkaeriConfig() {

    @Comment("Base64 tekstury glowki przycisku 'poprzednia strona'")
    var previousPageHead: String = ""

    @Comment("Base64 tekstury glowki przycisku 'nastepna strona'")
    var nextPageHead: String = ""

    @Comment("Base64 tekstury glowki przycisku 'powrot'")
    var backHead: String = ""

    @Comment("Material tla (filler) - nazwa XMaterial, np. GRAY_STAINED_GLASS_PANE")
    var fillerMaterial: String = "GRAY_STAINED_GLASS_PANE"

    @Comment("Nazwa itemu tla (MiniMessage, spacja = brak nazwy)")
    var fillerName: String = " "

    @Comment("Material akcentu (np. obramowanie) - nazwa XMaterial")
    var accentMaterial: String = "LIME_STAINED_GLASS_PANE"

    @Comment("Nazwa itemu akcentu (MiniMessage, spacja = brak nazwy)")
    var accentName: String = " "
}
