package eu.wejsonekk.skygen.skygengenerator.generator.hologram

import eu.wejsonekk.skygen.skygengenerator.config.impl.GeneratorConfig
import org.bukkit.Bukkit

/**
 * Loader that isolates every reference to the FancyHolograms API classes.
 *
 * [isAvailable] never touches FancyHolograms types, so it is always safe to call.
 * [create] is only invoked when FancyHolograms is actually enabled, which keeps
 * servers without the plugin free of any [NoClassDefFoundError].
 */
object FancyHologramsHook {

    private const val PLUGIN_NAME = "FancyHolograms"

    fun isAvailable(): Boolean = Bukkit.getPluginManager().isPluginEnabled(PLUGIN_NAME)

    fun create(config: GeneratorConfig): GeneratorHologramProvider? =
        runCatching { FancyHologramsProvider(config) }.getOrNull()
}
