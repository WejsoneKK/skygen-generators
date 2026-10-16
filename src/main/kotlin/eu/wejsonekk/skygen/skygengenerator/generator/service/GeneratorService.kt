package eu.wejsonekk.skygen.skygengenerator.generator.service

import eu.wejsonekk.skygen.skygengenerator.api.SkyGenGeneratorsApi
import eu.wejsonekk.skygen.skygengenerator.api.event.GeneratorCreateEvent
import eu.wejsonekk.skygen.skygengenerator.api.event.GeneratorDisableEvent
import eu.wejsonekk.skygen.skygengenerator.api.event.GeneratorEditEvent
import eu.wejsonekk.skygen.skygengenerator.api.event.GeneratorEnableEvent
import eu.wejsonekk.skygen.skygengenerator.api.event.GeneratorEvent
import eu.wejsonekk.skygen.skygengenerator.api.event.GeneratorRemoveEvent
import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.api.generator.GeneratorLocation
import eu.wejsonekk.skygen.skygengenerator.generator.model.GeneratorImpl
import eu.wejsonekk.skygen.skygengenerator.generator.repository.GeneratorRepository
import com.cryptomorin.xseries.XMaterial
import org.bukkit.Bukkit
import org.bukkit.Location
import java.time.Duration
import java.time.Instant
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

/**
 * Business layer of the generator system. Owns the in-memory cache and exposes
 * the public [SkyGenGeneratorsApi]. Persistence is delegated to a
 * [GeneratorRepository].
 */
class GeneratorService(
    private val repository: GeneratorRepository,
) : SkyGenGeneratorsApi {

    private val cache = ConcurrentHashMap<String, Generator>()

    // --- lifecycle (used by the plugin / manager) ---

    fun load(): CompletableFuture<Void> =
        this.repository.findAll().thenAccept { generators ->
            this.cache.clear()
            generators.forEach { this.cache[it.id] = it }
        }

    fun cachedGenerators(): Collection<Generator> = this.cache.values.toList()

    fun cachedById(id: String): Generator? = this.cache[id]

    fun persist(generator: Generator): CompletableFuture<Void> =
        this.repository.save(generator).thenAccept { this.cache[generator.id] = generator }

    fun recordGeneration(generator: Generator, instant: Instant): CompletableFuture<Void> =
        this.persist(generator.withLastGeneration(instant))

    private fun Generator.withLastGeneration(instant: Instant): Generator =
        if (this is GeneratorImpl) this.withLastGenerationAt(instant)
        else GeneratorImpl(
            id = this.id,
            material = this.material,
            amount = this.amount,
            interval = this.interval,
            activationRadius = this.activationRadius,
            ownerId = this.ownerId,
            location = this.location,
            enabled = this.enabled,
            createdAt = this.createdAt,
            lastGenerationAt = instant,
        )

    private fun Generator.withEnabled(enabled: Boolean): Generator =
        if (this is GeneratorImpl) this.withEnabled(enabled)
        else GeneratorImpl(
            id = this.id,
            material = this.material,
            amount = this.amount,
            interval = this.interval,
            activationRadius = this.activationRadius,
            ownerId = this.ownerId,
            location = this.location,
            enabled = enabled,
            createdAt = this.createdAt,
            lastGenerationAt = this.lastGenerationAt,
        )

    /**
     * Updates an existing generator. Fires a [GeneratorEditEvent] and, when the
     * enabled state changed, a [GeneratorEnableEvent] / [GeneratorDisableEvent].
     *
     * @return the stored generator or `null` when the change was cancelled / not found
     */
    fun update(generator: Generator): CompletableFuture<Generator?> {
        val previous = this.cache[generator.id] ?: return CompletableFuture.completedFuture(null)

        val editEvent = GeneratorEditEvent(generator, previous)
        Bukkit.getPluginManager().callEvent(editEvent)
        if (editEvent.isCancelled) {
            return CompletableFuture.completedFuture(null)
        }

        if (previous.enabled != generator.enabled) {
            val toggleEvent: GeneratorEvent =
                if (generator.enabled) GeneratorEnableEvent(generator) else GeneratorDisableEvent(generator)
            Bukkit.getPluginManager().callEvent(toggleEvent)
            if (toggleEvent.isCancelled) {
                return CompletableFuture.completedFuture(null)
            }
        }

        return this.repository.save(generator).thenApply {
            this.cache[generator.id] = generator
            generator
        }
    }

    fun setEnabled(id: String, enabled: Boolean): CompletableFuture<Generator?> {
        val current = this.cache[id] ?: return CompletableFuture.completedFuture(null)
        if (current.enabled == enabled) {
            return CompletableFuture.completedFuture(current)
        }
        return this.update(current.withEnabled(enabled))
    }

    // --- public API ---

    override fun generator(id: String): CompletableFuture<Generator?> {
        this.cache[id]?.let { return CompletableFuture.completedFuture(it) }
        return this.repository.findById(id).thenApply { found -> found?.also { this.cache[it.id] = it } }
    }

    override fun generators(): CompletableFuture<Collection<Generator>> =
        CompletableFuture.completedFuture(this.cachedGenerators())

    override fun generatorsByOwner(ownerId: UUID): CompletableFuture<List<Generator>> =
        this.repository.findByOwner(ownerId)

    override fun generatorsAt(location: Location): CompletableFuture<List<Generator>> {
        val position = GeneratorLocation.from(location)
        return CompletableFuture.completedFuture(this.cache.values.filter { it.location == position })
    }

    override fun exists(id: String): CompletableFuture<Boolean> =
        if (this.cache.containsKey(id)) CompletableFuture.completedFuture(true)
        else this.repository.existsById(id)

    override fun createGenerator(
        id: String,
        material: XMaterial,
        amount: Int,
        interval: Duration,
        ownerId: UUID?,
        location: Location,
        activationRadius: Double?,
    ): CompletableFuture<Generator> {
        if (this.cache.containsKey(id)) {
            return CompletableFuture.failedFuture(
                IllegalArgumentException("Generator '$id' already exists"),
            )
        }

        val generator = GeneratorImpl(
            id = id,
            material = material,
            amount = amount.coerceAtLeast(1),
            interval = if (interval.isNegative || interval.isZero) Duration.ofSeconds(1) else interval,
            activationRadius = activationRadius,
            ownerId = ownerId,
            location = GeneratorLocation.from(location),
            enabled = true,
            createdAt = Instant.now(),
            lastGenerationAt = null,
        )

        val event = GeneratorCreateEvent(generator)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) {
            return CompletableFuture.failedFuture(
                IllegalStateException("Generator creation was cancelled"),
            )
        }

        return this.repository.save(generator).thenApply {
            this.cache[generator.id] = generator
            generator
        }
    }

    override fun removeGenerator(id: String): CompletableFuture<Boolean> {
        val generator = this.cache[id] ?: return CompletableFuture.completedFuture(false)

        val event = GeneratorRemoveEvent(generator)
        Bukkit.getPluginManager().callEvent(event)
        if (event.isCancelled) {
            return CompletableFuture.completedFuture(false)
        }

        return this.repository.removeById(id).thenApply { removed ->
            if (removed) {
                this.cache.remove(id)
            }
            removed
        }
    }

    override fun updateGenerator(generator: Generator): CompletableFuture<Generator?> =
        this.update(generator)

    override fun setGeneratorEnabled(id: String, enabled: Boolean): CompletableFuture<Generator?> =
        this.setEnabled(id, enabled)
}
