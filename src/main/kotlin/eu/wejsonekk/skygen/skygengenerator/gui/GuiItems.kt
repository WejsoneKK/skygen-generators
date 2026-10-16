package eu.wejsonekk.skygen.skygengenerator.gui

import com.cryptomorin.xseries.XMaterial
import eu.wejsonekk.skygen.skygengenerator.config.impl.GuiConfig
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.inventory.ItemStack

/**
 * Shared, ready to use GUI items (navigation heads, page info, filler/accent).
 * Initialised once from the plugin with the [GuiConfig].
 */
object GuiItems {

    private lateinit var miniMessage: MiniMessage
    private lateinit var config: GuiConfig

    fun init(miniMessage: MiniMessage, config: GuiConfig) {
        this.miniMessage = miniMessage
        this.config = config
    }

    // --- navigation ---

    fun previousPage(): ItemStack =
        head(this.config.previousPageHead, "<dark_gray>« <red>Poprzednia strona")

    fun nextPage(): ItemStack =
        head(this.config.nextPageHead, "<dark_gray>» <red>Następna strona")

    fun back(): ItemStack =
        head(this.config.backHead, "<dark_gray>« <red>Powrót")

    fun pageInfo(page: Int, pages: Int): ItemStack =
        ItemBuilder.of(this.miniMessage, XMaterial.PAPER)
            .name("<yellow>Strona <white>$page<yellow>/<white>$pages")
            .build()

    fun close(): ItemStack =
        ItemBuilder.of(this.miniMessage, XMaterial.BARRIER)
            .name("<red>Zamknij")
            .build()

    // --- layout / filler ---

    fun filler(): ItemStack = ItemBuilder.of(this.miniMessage, this.fillerMaterial())
        .name(this.config.fillerName)
        .build()

    fun accent(): ItemStack = ItemBuilder.of(this.miniMessage, this.accentMaterial())
        .name(this.config.accentName)
        .build()

    fun material(raw: String, fallback: XMaterial): XMaterial =
        XMaterial.matchXMaterial(raw).orElse(fallback)

    private fun fillerMaterial(): XMaterial =
        this.material(this.config.fillerMaterial, XMaterial.GRAY_STAINED_GLASS_PANE)

    private fun accentMaterial(): XMaterial =
        this.material(this.config.accentMaterial, XMaterial.LIME_STAINED_GLASS_PANE)

    private fun head(texture: String, name: String): ItemStack =
        ItemBuilder.of(this.miniMessage, XMaterial.PLAYER_HEAD)
            .texture(texture)
            .name(name)
            .build()
}
