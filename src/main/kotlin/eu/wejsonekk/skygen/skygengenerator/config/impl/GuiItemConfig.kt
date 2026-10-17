package eu.wejsonekk.skygen.skygengenerator.config.impl

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment
import org.bukkit.inventory.ItemStack

/**
 * A single configurable GUI item. Names and lore use MiniMessage and support
 * PlaceholderAPI placeholders as well as the GUI specific `{...}` placeholders.
 */
class GuiItemConfig : OkaeriConfig() {

    @Comment("Pelny item (XItemStack). Gdy ustawiony, nadpisuje material/amount ponizej")
    var item: ItemStack? = null

    @Comment("Material (nazwa XMaterial), np. STONE, PLAYER_HEAD")
    var material: String = "STONE"

    @Comment("Nazwa itemu (MiniMessage). Puste = brak nazwy")
    var name: String = ""

    @Comment("Opis itemu (MiniMessage)")
    var lore: MutableList<String> = mutableListOf()

    @Comment("Ilosc w stacku")
    var amount: Int = 1

    @Comment("Base64 tekstura glowki (tylko dla PLAYER_HEAD)")
    var texture: String = ""

    @Comment("Podswietlenie (enchant glint)")
    var glow: Boolean = false

    @Comment("Custom model data (0 = brak)")
    var customModelData: Int = 0
}
