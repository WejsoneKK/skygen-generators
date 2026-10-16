package eu.wejsonekk.skygen.skygengenerator.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player

/**
 * Central MiniMessage parser with optional PlaceholderAPI support.
 *
 * Every place that turns a configured string into a [Component] should go
 * through this object so that `%placeholder%` values are resolved before the
 * MiniMessage tags are parsed. Initialised once by the plugin.
 */
object Text {

    private lateinit var miniMessage: MiniMessage

    fun init(miniMessage: MiniMessage) {
        this.miniMessage = miniMessage
    }

    /** Resolves PlaceholderAPI placeholders (when a viewer is given) without parsing tags. */
    fun resolve(text: String, viewer: Player? = null): String = Placeholders.apply(viewer, text)

    /** Resolves placeholders and parses MiniMessage into a [Component]. */
    fun parse(text: String, viewer: Player? = null): Component =
        this.miniMessage.deserialize(resolve(text, viewer))

    /** Same as [parse] but strips the default italic style (used for item names / lore). */
    fun parseItem(text: String, viewer: Player? = null): Component =
        this.miniMessage.deserialize("<!italic>" + resolve(text, viewer))

    fun parseList(lines: List<String>, viewer: Player? = null): List<Component> =
        lines.map { this.parse(it, viewer) }
}
