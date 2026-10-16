package eu.wejsonekk.skygen.skygengenerator.api

import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import com.cryptomorin.xseries.XMaterial
import org.bukkit.Location
import java.time.Duration
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * Public entry point of the plugin. Other plugins should obtain an instance through
 * the Bukkit services manager ([org.bukkit.plugin.ServicesManager]).
 *
 * All operations are asynchronous and return a [CompletableFuture].
 */
interface SkyGenGeneratorsApi {

    fun generator(id: String): CompletableFuture<Generator?>

    fun generators(): CompletableFuture<Collection<Generator>>

    fun generatorsByOwner(ownerId: UUID): CompletableFuture<List<Generator>>

    fun generatorsAt(location: Location): CompletableFuture<List<Generator>>

    fun exists(id: String): CompletableFuture<Boolean>

    /**
     * Creates and persists a new generator.
     *
     * @throws IllegalArgumentException when a generator with [id] already exists
     */
    fun createGenerator(
        id: String,
        material: XMaterial,
        amount: Int,
        interval: Duration,
        ownerId: UUID?,
        location: Location,
        activationRadius: Double? = null,
    ): CompletableFuture<Generator>

    /**
     * Updates an existing generator. Fires a
     * [eu.wejsonekk.skygen.skygengenerator.api.event.GeneratorEditEvent] which may
     * cancel the change.
     *
     * @return the stored generator or `null` when cancelled / not found
     */
    fun updateGenerator(generator: Generator): CompletableFuture<Generator?>

    /**
     * Enables or disables a generator. Fires
     * [eu.wejsonekk.skygen.skygengenerator.api.event.GeneratorEnableEvent] or
     * [eu.wejsonekk.skygen.skygengenerator.api.event.GeneratorDisableEvent].
     *
     * @return the updated generator or `null` when cancelled / not found
     */
    fun setGeneratorEnabled(id: String, enabled: Boolean): CompletableFuture<Generator?>

    fun removeGenerator(id: String): CompletableFuture<Boolean>
}
