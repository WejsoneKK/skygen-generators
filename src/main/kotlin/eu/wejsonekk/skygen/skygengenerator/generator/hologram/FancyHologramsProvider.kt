package eu.wejsonekk.skygen.skygengenerator.generator.hologram

import de.oliver.fancyholograms.api.FancyHologramsPlugin
import de.oliver.fancyholograms.api.data.TextHologramData
import de.oliver.fancyholograms.api.hologram.Hologram
import eu.wejsonekk.skygen.skygengenerator.config.impl.GeneratorConfig
import org.bukkit.Location
import org.bukkit.entity.Display

/**
 * Hologram provider backed by the FancyHolograms plugin.
 *
 * This class is only instantiated when FancyHolograms is present, so its imports
 * never trigger a [NoClassDefFoundError] on servers without the plugin.
 */
class FancyHologramsProvider(
    private val config: GeneratorConfig,
) : GeneratorHologramProvider {

    override val id: String = "FANCY_HOLOGRAMS"

    private val manager = FancyHologramsPlugin.get().hologramManager
    private val holograms = HashMap<String, Hologram>()

    override fun render(generatorId: String, lines: List<String>, location: Location) {
        val hologramLocation = this.hologramLocation(location)

        val existing = this.holograms[generatorId]
        if (existing != null) {
            val data = existing.data as? TextHologramData ?: return
            data.setText(lines)
            data.setLocation(hologramLocation)
            existing.queueUpdate()
            return
        }

        val name = hologramName(generatorId)
        this.manager.getHologram(name).ifPresent { this.manager.removeHologram(it) }

        val data = TextHologramData(name, hologramLocation)
        data.setPersistent(false)
        data.setBillboard(Display.Billboard.CENTER)
        data.setSeeThrough(false)
        data.setTextShadow(true)
        data.setText(lines)

        val hologram = this.manager.create(data)
        this.manager.addHologram(hologram)
        this.holograms[generatorId] = hologram
    }

    override fun remove(generatorId: String) {
        this.holograms.remove(generatorId)?.let { this.manager.removeHologram(it) }
    }

    override fun removeAll() {
        this.holograms.keys.toList().forEach { this.remove(it) }
    }

    private fun hologramName(generatorId: String): String =
        "skygen_" + generatorId.lowercase().replace(Regex("[^a-z0-9_-]"), "_")

    private fun hologramLocation(location: Location): Location =
        location.clone().add(0.5, this.config.hologramHeight, 0.5)
}
