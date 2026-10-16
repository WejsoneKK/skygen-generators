package eu.wejsonekk.skygen.skygengenerator.generator.hologram

import eu.wejsonekk.skygen.skygengenerator.config.impl.GeneratorConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.TextDisplay

/**
 * Built in hologram provider. Uses version independent [TextDisplay] entities
 * (available since Minecraft 1.19.4) so it works without any extra plugin.
 */
class TextDisplayHologramProvider(
    private val config: GeneratorConfig,
    private val miniMessage: MiniMessage,
) : GeneratorHologramProvider {

    override val id: String = "TEXT_DISPLAY"

    private val displays = HashMap<String, TextDisplay>()

    override fun render(generatorId: String, lines: List<String>, location: Location) {
        val hologramLocation = this.hologramLocation(location)

        val existing = this.displays[generatorId]
        val display = if (existing != null && existing.isValid) {
            existing
        } else {
            this.spawn(hologramLocation) ?: return
        }

        this.displays[generatorId] = display
        display.text(this.miniMessage.deserialize(lines.joinToString("\n")))
    }

    override fun remove(generatorId: String) {
        this.displays.remove(generatorId)?.let { if (it.isValid) it.remove() }
    }

    override fun removeAll() {
        this.displays.values.forEach { if (it.isValid) it.remove() }
        this.displays.clear()
    }

    private fun spawn(location: Location): TextDisplay? {
        val world = location.world ?: return null
        return runCatching {
            world.spawn(location, TextDisplay::class.java) { display ->
                display.isPersistent = false
                display.billboard = Display.Billboard.CENTER
                display.setDefaultBackground(false)
                display.isSeeThrough = false
                display.isShadowed = true
                display.viewRange = 1.0f
                display.text(Component.empty())
            }
        }.getOrNull()
    }

    private fun hologramLocation(location: Location): Location =
        location.clone().add(0.5, this.config.hologramHeight, 0.5)
}
