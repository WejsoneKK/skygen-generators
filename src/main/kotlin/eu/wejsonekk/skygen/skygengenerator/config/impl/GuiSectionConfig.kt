package eu.wejsonekk.skygen.skygengenerator.config.impl

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment

/**
 * A single, fully configurable GUI (e.g. `guis.list` or `guis.editor`).
 *
 * [items] maps a logical name (see the plugin defaults) to its [GuiItemConfig]
 * and [slots] maps the same logical name to the inventory slot it is placed in.
 */
class GuiSectionConfig : OkaeriConfig() {

    @Comment("Tytul GUI (MiniMessage). Obsluguje placeholdery")
    var title: String = ""

    @Comment("Liczba wierszy (1-6)")
    var rows: Int = 6

    @Comment("Definicje itemow (material, nazwa, opis, tekstura, glow...)")
    var items: MutableMap<String, GuiItemConfig> = linkedMapOf()

    @Comment("Przypisanie itemow do slotow. Klucz = nazwa itemu z sekcji items")
    var slots: MutableMap<String, Int> = linkedMapOf()

    fun item(key: String): GuiItemConfig = this.items.getOrPut(key) { GuiItemConfig() }

    fun slot(key: String, fallback: Int): Int = this.slots[key] ?: fallback
}
