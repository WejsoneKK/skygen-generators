package eu.wejsonekk.skygen.skygengenerator.genboost.model

import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoost
import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoostType
import java.time.Duration
import java.time.Instant
import java.util.UUID

/**
 * A currently running boost with its start time.
 */
data class ActiveGenBoost(
    val id: UUID,
    val boost: GenBoost,
    val startedAt: Instant,
) {

    val type: GenBoostType get() = this.boost.type
    val multiplier: Double get() = this.boost.multiplier
    val duration: Duration get() = this.boost.duration
    val ownerId: UUID? get() = this.boost.ownerId
    val name: String get() = this.boost.name

    fun expiresAt(): Instant = this.startedAt.plus(this.boost.duration)

    fun isExpired(now: Instant): Boolean = !now.isBefore(this.expiresAt())

    fun remaining(now: Instant): Duration =
        Duration.between(now, this.expiresAt()).coerceAtLeast(Duration.ZERO)

    /** 1.0 at start, 0.0 at expiry. */
    fun progress(now: Instant): Float {
        val total = this.duration.toMillis().toDouble().coerceAtLeast(1.0)
        return (this.remaining(now).toMillis() / total).coerceIn(0.0, 1.0).toFloat()
    }

    companion object {
        fun of(boost: GenBoost, startedAt: Instant = Instant.now()): ActiveGenBoost =
            ActiveGenBoost(UUID.randomUUID(), boost, startedAt)
    }
}
