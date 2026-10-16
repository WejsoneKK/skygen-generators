package eu.wejsonekk.skygen.skygengenerator.api

import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoost
import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoostType
import org.bukkit.entity.Player
import java.time.Duration
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * Public entry point for the gen boost system. Obtain it through the Bukkit
 * [org.bukkit.plugin.ServicesManager] (see [SkyGenGeneratorsApi]).
 *
 * Query methods are cheap and can be called from any thread. Mutation methods
 * are marshalled onto the main thread and therefore return a
 * [CompletableFuture].
 */
interface SkyGenBoostApi {

    /** Every boost that is currently running (expired ones are excluded). */
    fun activeBoosts(): Collection<GenBoost>

    /** Boosts visible to the player with [playerId] (all global + their personal). */
    fun activeBoostsFor(playerId: UUID): Collection<GenBoost>

    fun isBoostActive(type: GenBoostType, ownerId: UUID?): Boolean

    /**
     * Compound multiplier that would be applied to the generator with
     * [generatorId] right now (1.0 when no boost is active).
     */
    fun multiplierFor(generatorId: String): Double

    /**
     * Activates a boost. Fires
     * [eu.wejsonekk.skygen.skygengenerator.api.event.GenBoostActivateEvent].
     *
     * @return the activated boost or `null` when cancelled / invalid duration
     */
    fun activateBoost(
        type: GenBoostType,
        multiplier: Double,
        duration: Duration,
        ownerId: UUID?,
        name: String,
    ): CompletableFuture<GenBoost?>

    /**
     * Stops a running boost early. Fires
     * [eu.wejsonekk.skygen.skygengenerator.api.event.GenBoostEndEvent].
     *
     * @return `true` when the boost was stopped
     */
    fun endBoost(id: UUID): CompletableFuture<Boolean>

    /**
     * Gives a physical boost item to [player]. Fires
     * [eu.wejsonekk.skygen.skygengenerator.api.event.GenBoostReceiveEvent].
     *
     * @return `true` when the item was given
     */
    fun giveBoostItem(
        player: Player,
        type: GenBoostType,
        multiplier: Double,
        duration: Duration,
        name: String,
    ): CompletableFuture<Boolean>
}
