package eu.wejsonekk.skygen.skygengenerator.genboost.api

import eu.wejsonekk.skygen.skygengenerator.api.SkyGenBoostApi
import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoost
import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoostType
import eu.wejsonekk.skygen.skygengenerator.generator.service.GeneratorService
import eu.wejsonekk.skygen.skygengenerator.genboost.manager.GenBoostManager
import eu.wejsonekk.skygen.skygengenerator.genboost.service.GenBoostService
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Duration
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * [SkyGenBoostApi] implementation. Mutations are executed on the main thread,
 * queries read the thread safe manager state directly.
 */
class GenBoostApiImpl(
    private val plugin: Plugin,
    private val manager: GenBoostManager,
    private val service: GenBoostService,
    private val generatorService: GeneratorService,
) : SkyGenBoostApi {

    override fun activeBoosts(): Collection<GenBoost> =
        this.manager.activeBoosts().map { it.boost }

    override fun activeBoostsFor(playerId: UUID): Collection<GenBoost> =
        this.manager.activeBoosts()
            .filter { it.type == GenBoostType.GLOBAL || it.ownerId == playerId }
            .map { it.boost }

    override fun isBoostActive(type: GenBoostType, ownerId: UUID?): Boolean =
        this.manager.activeBoosts().any { it.type == type && it.ownerId == ownerId }

    override fun multiplierFor(generatorId: String): Double {
        val generator = this.generatorService.cachedById(generatorId) ?: return 1.0
        return this.manager.multiplierFor(generator)
    }

    override fun activateBoost(
        type: GenBoostType,
        multiplier: Double,
        duration: Duration,
        ownerId: UUID?,
        name: String,
    ): CompletableFuture<GenBoost?> =
        this.sync { this.manager.activate(type, multiplier, duration, ownerId, name)?.boost }

    override fun endBoost(id: UUID): CompletableFuture<Boolean> =
        this.sync { this.manager.endBoost(id) }

    override fun giveBoostItem(
        player: Player,
        type: GenBoostType,
        multiplier: Double,
        duration: Duration,
        name: String,
    ): CompletableFuture<Boolean> =
        this.sync { this.service.giveItem(player, type, multiplier, duration, name) }

    private fun <T> sync(block: () -> T): CompletableFuture<T> {
        val future = CompletableFuture<T>()
        val task = Runnable {
            try {
                future.complete(block())
            } catch (throwable: Throwable) {
                future.completeExceptionally(throwable)
            }
        }

        if (Bukkit.isPrimaryThread()) {
            task.run()
        } else {
            Bukkit.getScheduler().runTask(this.plugin, task)
        }
        return future
    }
}
