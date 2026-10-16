package eu.wejsonekk.skygen.skygengenerator.generator.hologram

import org.bukkit.Location

/**
 * Backend used to display generator holograms. Implementations:
 * - [TextDisplayHologramProvider] (built in, default) using Bukkit [org.bukkit.entity.TextDisplay]
 * - [FancyHologramsProvider] when the FancyHolograms plugin is installed
 */
interface GeneratorHologramProvider {

    /** Human readable provider name, used for logging. */
    val id: String

    /** Creates or updates the hologram of [generatorId]. */
    fun render(generatorId: String, lines: List<String>, location: Location)

    /** Removes the hologram of [generatorId]. */
    fun remove(generatorId: String)

    /** Removes every hologram owned by this provider. */
    fun removeAll()
}
