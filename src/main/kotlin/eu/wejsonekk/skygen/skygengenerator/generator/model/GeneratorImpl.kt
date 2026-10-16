package eu.wejsonekk.skygen.skygengenerator.generator.model

import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.api.generator.GeneratorLocation
import com.cryptomorin.xseries.XMaterial
import java.time.Duration
import java.time.Instant
import java.util.UUID

/**
 * Immutable implementation of [Generator]. Every mutation returns a new instance,
 * which makes the model safe to share between threads.
 */
data class GeneratorImpl(
    override val id: String,
    override val material: XMaterial,
    override val amount: Int,
    override val interval: Duration,
    override val ownerId: UUID?,
    override val location: GeneratorLocation,
    override val enabled: Boolean,
    override val createdAt: Instant,
    override val lastGenerationAt: Instant?,
) : Generator {

    fun withMaterial(material: XMaterial): GeneratorImpl = this.copy(material = material)

    fun withAmount(amount: Int): GeneratorImpl = this.copy(amount = amount.coerceAtLeast(1))

    fun withInterval(interval: Duration): GeneratorImpl =
        this.copy(interval = if (interval.isNegative || interval.isZero) Duration.ofSeconds(1) else interval)

    fun withEnabled(enabled: Boolean): GeneratorImpl = this.copy(enabled = enabled)

    fun withOwnerId(ownerId: UUID?): GeneratorImpl = this.copy(ownerId = ownerId)

    fun withLastGenerationAt(instant: Instant): GeneratorImpl = this.copy(lastGenerationAt = instant)
}
