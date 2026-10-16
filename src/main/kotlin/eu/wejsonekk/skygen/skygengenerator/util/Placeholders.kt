package eu.wejsonekk.skygen.skygengenerator.util

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

/**
 * Optional PlaceholderAPI bridge.
 *
 * [available] never touches PlaceholderAPI types, so it is safe to call on
 * servers without the plugin. [apply] only delegates to [PlaceholderApiBridge]
 * (which references the PlaceholderAPI classes) when the plugin is enabled.
 */
object Placeholders {

    val available: Boolean
        get() = Bukkit.getPluginManager().getPlugin("PlaceholderAPI")?.isEnabled == true

    fun apply(player: Player?, text: String): String {
        if (!this.available || text.indexOf('%') < 0) {
            return text
        }
        return PlaceholderApiBridge.apply(player, text)
    }
}

/**
 * Isolates every reference to the PlaceholderAPI classes so that loading
 * [Placeholders] never triggers a [NoClassDefFoundError].
 */
private object PlaceholderApiBridge {

    fun apply(player: Player?, text: String): String =
        runCatching { me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player as OfflinePlayer?, text) }
            .getOrDefault(text)
}
