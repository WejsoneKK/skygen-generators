package eu.wejsonekk.skygen.skygengenerator.config.impl

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment

/**
 * Look & feel of every plugin GUI (`gui.yml`).
 *
 * Each GUI lives in its own section ([list], [editor]) and every item / button
 * can be fully customised: material, name, lore, texture, glow, amount, custom
 * model data and the slot it is placed in.
 */
class GuiConfig : OkaeriConfig() {

    @Comment("GUI listy generatorow")
    var list: GuiSectionConfig = defaultList()

    @Comment("GUI edycji pojedynczego generatora")
    var editor: GuiSectionConfig = defaultEditor()

    companion object {

        private fun item(
            material: String,
            name: String = "",
            vararg lore: String,
        ): GuiItemConfig = GuiItemConfig().apply {
            this.material = material
            this.name = name
            this.lore = lore.toMutableList()
        }

        private fun head(texture: String = "", name: String = "", vararg lore: String): GuiItemConfig =
            GuiItemConfig().apply {
                this.material = "PLAYER_HEAD"
                this.texture = texture
                this.name = name
                this.lore = lore.toMutableList()
            }

        fun defaultList(): GuiSectionConfig = GuiSectionConfig().apply {
            this.title = "<dark_gray>Generatory"
            this.rows = 6
            this.items = linkedMapOf(
                "filler" to item("GRAY_STAINED_GLASS_PANE", "<gray> "),
                "accent" to item("LIME_STAINED_GLASS_PANE", "<gray> "),
                "previousPage" to head(name = "<dark_gray>« <red>Poprzednia strona"),
                "nextPage" to head(name = "<dark_gray>» <red>Następna strona"),
                "pageInfo" to item("PAPER", "<yellow>Strona <white>{page}<yellow>/<white>{pages}"),
                "sort" to item("HOPPER", "<gold>Sortowanie: <white>{sort}", "<gray>Kliknij, aby zmienić"),
                "filter" to item("HOPPER", "<gold>Filtr: <white>{filter}", "<gray>Kliknij, aby zmienić"),
                "close" to item("BARRIER", "<red>Zamknij"),
                "generator" to item(
                    "STONE",
                    "<white>{id}",
                    "<gray>Item: <white>{material} <gray>x<white>{amount}",
                    "<gray>Interwał: <white>{interval}",
                    "<gray>Lokalizacja: <white>{world} {x},{y},{z}",
                    "<gray>Właściciel: <white>{owner}",
                    "<gray>Włączony: {enabled}",
                    "",
                    "<yellow>Kliknij, aby edytować",
                ),
            )
            this.slots = linkedMapOf(
                "previousPage" to 45,
                "sort" to 46,
                "filter" to 47,
                "pageInfo" to 49,
                "close" to 52,
                "nextPage" to 53,
            )
        }

        fun defaultEditor(): GuiSectionConfig = GuiSectionConfig().apply {
            this.title = "<dark_gray>Generator <white>{id}"
            this.rows = 4
            this.items = linkedMapOf(
                "filler" to item("GRAY_STAINED_GLASS_PANE", "<gray> "),
                "accent" to item("LIME_STAINED_GLASS_PANE", "<gray> "),
                "info" to item(
                    "PAPER",
                    "<gold>Informacje",
                    "<gray>ID: <white>{id}",
                    "<gray>Item: <white>{item} <gray>x<white>{amount}",
                    "<gray>Materiał: <white>{material}",
                    "<gray>Interwał: <white>{interval}",
                    "<gray>Zasięg: <white>{radius}",
                    "<gray>Właściciel: <white>{owner}",
                ),
                "toggle" to item("LEVER", "<gold>Stan: {enabled}"),
                "amountIncrease" to item("LIME_DYE", "<green>Ilość +1"),
                "amountDecrease" to item("RED_DYE", "<red>Ilość -1"),
                "intervalIncrease" to item("LIME_DYE", "<green>Interwał +30s"),
                "intervalDecrease" to item("RED_DYE", "<red>Interwał -30s"),
                "changeItem" to item("PAPER", "<gold>Zmień item", "<gray>Ustaw cały item z dłoni"),
                "radiusIncrease" to item("LIME_DYE", "<green>Zasięg +1", "<gray>Aktualnie: <white>{radius}"),
                "radiusDecrease" to item("RED_DYE", "<red>Zasięg -1", "<gray>Aktualnie: <white>{radius}"),
                "delete" to item("BARRIER", "<red>Usuń generator"),
                "back" to head(name = "<dark_gray>« <red>Powrót"),
                "close" to item("BARRIER", "<red>Zamknij"),
            )
            this.slots = linkedMapOf(
                "info" to 10,
                "toggle" to 11,
                "amountIncrease" to 12,
                "amountDecrease" to 13,
                "intervalIncrease" to 14,
                "intervalDecrease" to 15,
                "changeItem" to 16,
                "radiusDecrease" to 19,
                "radiusIncrease" to 20,
                "back" to 27,
                "delete" to 31,
                "close" to 35,
            )
        }
    }
}
