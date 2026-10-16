package eu.wejsonekk.skygen.skygengenerator.generator.manager

import eu.wejsonekk.skygen.skygengenerator.api.event.GeneratorGenerateEvent
import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.config.impl.GeneratorConfig
import eu.wejsonekk.skygen.skygengenerator.generator.adapter.GeneratorItemAdapter
import eu.wejsonekk.skygen.skygengenerator.generator.service.GeneratorService
import eu.wejsonekk.skygen.skygengenerator.genboost.manager.GenBoostManager
import org.bukkit.Bukkit
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitTask
import org.slf4j.Logger
import java.time.Duration
import java.time.Instant
import java.util.concurrent.CompletableFuture
import kotlin.math.roundToInt

/**
 * Runtime manager of the generator system. Runs the generation tick and drops
 * items on the ground when a player is nearby.
 */
class GeneratorManager(
    private val plugin: Plugin,
    private val service: GeneratorService,
    private val genBoostManager: GenBoostManager,
    private val generatorConfig: GeneratorConfig,
    private val itemAdapter: GeneratorItemAdapter,
    private val logger: Logger,
) {

    private var task: BukkitTask? = null

    fun start() {
        this.stop()
        val periodTicks = (this.generatorConfig.checkInterval.toMillis() / 50L).coerceAtLeast(1L)
        this.task = Bukkit.getScheduler().runTaskTimer(
            this.plugin,
            Runnable { this.tick() },
            periodTicks,
            periodTicks,
        )
    }

    fun stop() {
        this.task?.cancel()
        this.task = null
    }

    fun saveAll(): CompletableFuture<Void> {
        val futures = this.service.cachedGenerators().map { this.service.persist(it) }
        return CompletableFuture.allOf(*futures.toTypedArray())
    }

    private fun tick() {
        val now = Instant.now()
        for (generator in this.service.cachedGenerators()) {
            if (!generator.enabled) {
                continue
            }

            val lastGeneration = generator.lastGenerationAt ?: generator.createdAt
            if (Duration.between(lastGeneration, now) < generator.interval) {
                continue
            }

            if (this.generatorConfig.requireNearbyPlayer && !this.hasNearbyPlayer(generator)) {
                continue
            }

            this.generate(generator, now)
        }
    }

    private fun generate(generator: Generator, now: Instant) {
        val multiplier = this.genBoostManager
            .multiplierFor(generator)
            .coerceIn(1.0, this.generatorConfig.maxBoostMultiplier)

        val amount = (generator.amount * multiplier).roundToInt().coerceAtLeast(1)

        val event = GeneratorGenerateEvent(generator, generator.material, amount, multiplier)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) {
            return
        }

        this.service.recordGeneration(generator, now)

        val location = generator.location.toBukkitLocation() ?: return
        val world = location.world ?: return
        val item = this.itemAdapter.toItemStack(generator, event.amount)
        val dropLocation = location.clone().add(0.5, 0.0, 0.5)
        world.dropItemNaturally(dropLocation, item)

        if (this.logger.isDebugEnabled) {
            this.logger.debug(
                "Generator '{}' dropped {}x {} (multiplier x{})",
                generator.id,
                event.amount,
                generator.material,
                multiplier,
            )
        }
    }

    private fun hasNearbyPlayer(generator: Generator): Boolean {
        val location = generator.location.toBukkitLocation() ?: return false
        val world = location.world ?: return false

        val chunkX = location.blockX shr 4
        val chunkZ = location.blockZ shr 4
        if (!world.isChunkLoaded(chunkX, chunkZ)) {
            return false
        }

        val radius = generator.activationRadius ?: this.generatorConfig.activationRadius
        return world.getNearbyPlayers(location, radius).isNotEmpty()
    }
}
