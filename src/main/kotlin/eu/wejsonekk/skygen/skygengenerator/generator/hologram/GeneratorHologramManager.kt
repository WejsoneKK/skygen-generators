package eu.wejsonekk.skygen.skygengenerator.generator.hologram

import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.config.impl.GeneratorConfig
import eu.wejsonekk.skygen.skygengenerator.generator.service.GeneratorService
import eu.wejsonekk.skygen.skygengenerator.util.DurationFormat
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import org.slf4j.Logger
import java.time.Duration
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap

/**
 * Keeps a hologram above every generator showing which generator it is and the
 * time left until the next generation. Runs on the main thread.
 *
 * The actual rendering is delegated to a [GeneratorHologramProvider]. By default
 * ([HologramProvider.AUTO]) FancyHolograms is used when installed and the built
 * in TextDisplay provider otherwise.
 */
class GeneratorHologramManager(
    private val plugin: Plugin,
    private val service: GeneratorService,
    private val config: GeneratorConfig,
    private val logger: Logger,
) {

    private val provider: GeneratorHologramProvider = this.resolveProvider()

    private val tracked = ConcurrentHashMap.newKeySet<String>()

    private var task: BukkitTask? = null

    fun start() {
        if (!this.config.hologramEnabled) {
            return
        }
        this.stop()
        this.task = Bukkit.getScheduler().runTaskTimer(
            this.plugin,
            Runnable { this.tick() },
            20L,
            20L,
        )
    }

    fun stop() {
        this.task?.cancel()
        this.task = null
        this.provider.removeAll()
        this.tracked.clear()
    }

    /** Removes the hologram of a single generator immediately. */
    fun remove(generatorId: String) {
        if (this.tracked.remove(generatorId)) {
            this.provider.remove(generatorId)
        }
    }

    private fun tick() {
        val now = Instant.now()
        val generators = this.service.cachedGenerators().associateBy { it.id }

        this.tracked.filter { it !in generators }.forEach { this.remove(it) }

        for (generator in generators.values) {
            val location = generator.location.toBukkitLocation() ?: continue
            val world = location.world ?: continue
            if (!world.isChunkLoaded(location.blockX shr 4, location.blockZ shr 4)) {
                continue
            }

            this.provider.render(generator.id, this.lines(generator, world, location, now), location)
            this.tracked.add(generator.id)
        }
    }

    private fun lines(generator: Generator, world: World, location: Location, now: Instant): List<String> {
        val header = this.config.hologramGeneratorLine
            .replace("{id}", generator.id)
            .replace("{material}", generator.material.name)
            .replace("{amount}", generator.amount.toString())

        val status = when {
            !generator.enabled -> this.config.hologramDisabledLine
            this.config.requireNearbyPlayer && !this.hasNearbyPlayer(world, location, generator) -> this.config.hologramWaitingLine
            else -> {
                val base = generator.lastGenerationAt ?: generator.createdAt
                val remaining = Duration.between(now, base.plus(generator.interval)).coerceAtLeast(Duration.ZERO)
                this.config.hologramRespawnLine.replace("{time}", DurationFormat.format(remaining))
            }
        }

        return listOf(header, status)
    }

    private fun hasNearbyPlayer(world: World, location: Location, generator: Generator): Boolean =
        world.getNearbyPlayers(location, generator.activationRadius ?: this.config.activationRadius).isNotEmpty()

    private fun resolveProvider(): GeneratorHologramProvider {
        val textDisplay = TextDisplayHologramProvider(this.config)
        val provider = when (this.config.hologramProvider) {
            HologramProvider.TEXT_DISPLAY -> textDisplay
            HologramProvider.FANCY_HOLOGRAMS, HologramProvider.AUTO -> this.fancyHolograms() ?: textDisplay
        }
        this.logger.info("Using '{}' hologram provider for generators.", provider.id)
        return provider
    }

    private fun fancyHolograms(): GeneratorHologramProvider? {
        if (!FancyHologramsHook.isAvailable()) {
            return null
        }
        val provider = FancyHologramsHook.create(this.config)
        if (provider == null) {
            this.logger.warn("FancyHolograms is enabled but the hook could not be created - using the built in provider.")
        }
        return provider
    }
}
