package eu.wejsonekk.skygen.skygengenerator.api.genboost

import java.time.Duration
import java.util.UUID

/**
 * A gen boost definition. Boosts multiply the amount of items produced by generators.
 */
interface GenBoost {

    val type: GenBoostType

    /** Multiplier applied to the generator amount, e.g. 1.5. */
    val multiplier: Double

    /** How long the boost lasts. */
    val duration: Duration

    /** Target player for [GenBoostType.PERSONAL] boosts, `null` for global boosts. */
    val ownerId: UUID?

    /** Optional display name. */
    val name: String
}
