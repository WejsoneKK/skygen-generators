package eu.wejsonekk.skygen.skygengenerator.api.generator

import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * Immutable, persistence friendly representation of a generator position.
 */
data class GeneratorLocation(
    val world: String,
    val x: Int,
    val y: Int,
    val z: Int,
) {

    fun toBukkitLocation(): Location? {
        val bukkitWorld = Bukkit.getWorld(this.world) ?: return null
        return Location(bukkitWorld, this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
    }

    companion object {
        @JvmStatic
        fun from(location: Location): GeneratorLocation = GeneratorLocation(
            location.world?.name ?: "world",
            location.blockX,
            location.blockY,
            location.blockZ,
        )
    }
}
