package eu.wejsonekk.skygen.skygengenerator.api.generator

import com.cryptomorin.xseries.XMaterial
import org.bukkit.inventory.ItemStack
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

    /** Full item template dropped on every cycle (amount is ignored, see [amount]). */
    val item: ItemStack

    /** Base amount dropped on every cycle (before boosts). */
    val amount: Int

    /** Interval between cycles. */
    val interval: Duration

    /**
     * Optional per generator activation radius (in blocks). When `null` the
     * global `generators.yml` value is used.
     */
    val activationRadius: Double?

    /** Optional owner used by personal boosts. */
    val ownerId: UUID?

    val location: GeneratorLocation
    val enabled: Boolean
    val createdAt: Instant
    val lastGenerationAt: Instant?

    /** Material derived from [item], kept for display / filtering / legacy data. */
    val material: XMaterial
        get() = XMaterial.matchXMaterial(this.item.type)
}
