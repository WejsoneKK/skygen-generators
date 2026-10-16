package eu.wejsonekk.skygen.skygengenerator.genboost.model

import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoost
import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoostType
import java.time.Duration
import java.util.UUID

/**
 * Immutable [GenBoost] definition.
 */
data class GenBoostImpl(
    override val type: GenBoostType,
    override val multiplier: Double,
    override val duration: Duration,
    override val ownerId: UUID?,
    override val name: String,
) : GenBoost
