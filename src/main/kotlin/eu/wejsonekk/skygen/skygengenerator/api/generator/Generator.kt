package eu.wejsonekk.skygen.skygengenerator.api.generator

import com.cryptomorin.xseries.XMaterial
import java.time.Duration
import java.time.Instant
import java.util.UUID

/**
 * A placed generator instance.
 *
 * Implementations should be immutable. Use [eu.wejsonekk.skygen.skygengenerator.api.SkyGenGeneratorsApi]
 * to apply changes.
 */
interface Generator {

    /** Unique, admin defined id (e.g. `stone_1`). */
    val id: String

    /** Item dropped on every cycle. */
    val material: XMaterial

    /** Base amount dropped on every cycle (before boosts). */
    val amount: Int

    /** Interval between cycles. */
    val interval: Duration

    /** Optional owner used by personal boosts. */
    val ownerId: UUID?

    val location: GeneratorLocation
    val enabled: Boolean
    val createdAt: Instant
    val lastGenerationAt: Instant?
}
