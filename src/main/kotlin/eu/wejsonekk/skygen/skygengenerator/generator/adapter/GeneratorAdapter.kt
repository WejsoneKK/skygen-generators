package eu.wejsonekk.skygen.skygengenerator.generator.adapter

import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.api.generator.GeneratorLocation
import eu.wejsonekk.skygen.skygengenerator.generator.document.GeneratorDocument
import eu.wejsonekk.skygen.skygengenerator.generator.entity.GeneratorEntity
import eu.wejsonekk.skygen.skygengenerator.generator.model.GeneratorImpl
import eu.wejsonekk.skygen.skygengenerator.util.ItemStackBytesCodec
import eu.wejsonekk.skygen.skygengenerator.util.XItemStackCodec
import com.cryptomorin.xseries.XMaterial
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.time.Duration
import java.time.Instant
import java.util.UUID

/**
 * Converts between the domain model and the two persistence representations
 * ([GeneratorEntity] for SQL, [GeneratorDocument] for MongoDB).
 */
object GeneratorAdapter {

    private const val NO_TIMESTAMP = 0L

    // --- ORMLite (SQL) ---

    fun toModel(entity: GeneratorEntity): GeneratorImpl = GeneratorImpl(
        id = entity.id,
        item = resolveItem(entity.item, entity.material),
        amount = entity.amount,
        interval = Duration.ofMillis(entity.intervalMillis),
        activationRadius = entity.activationRadius,
        ownerId = entity.ownerId,
        location = GeneratorLocation(entity.world, entity.x, entity.y, entity.z),
        enabled = entity.enabled,
        createdAt = Instant.ofEpochMilli(entity.createdAt),
        lastGenerationAt = entity.lastGenerationAt
            .takeIf { it != NO_TIMESTAMP }
            ?.let(Instant::ofEpochMilli),
    )

    fun toEntity(generator: Generator): GeneratorEntity = GeneratorEntity(
        id = generator.id,
        material = generator.material.name,
        item = ItemStackBytesCodec.encode(generator.item),
        amount = generator.amount,
        intervalMillis = generator.interval.toMillis(),
        activationRadius = generator.activationRadius,
        ownerId = generator.ownerId,
        world = generator.location.world,
        x = generator.location.x,
        y = generator.location.y,
        z = generator.location.z,
        enabled = generator.enabled,
        createdAt = generator.createdAt.toEpochMilli(),
        lastGenerationAt = generator.lastGenerationAt?.toEpochMilli() ?: NO_TIMESTAMP,
    )

    // --- MongoDB ---

    fun toModel(document: GeneratorDocument): GeneratorImpl = GeneratorImpl(
        id = document.id,
        item = resolveItem(document.item, document.material),
        amount = document.amount,
        interval = Duration.ofMillis(document.intervalMillis),
        activationRadius = document.activationRadius,
        ownerId = document.ownerId?.let(UUID::fromString),
        location = GeneratorLocation(document.world, document.x, document.y, document.z),
        enabled = document.enabled,
        createdAt = Instant.ofEpochMilli(document.createdAt),
        lastGenerationAt = document.lastGenerationAt
            .takeIf { it != NO_TIMESTAMP }
            ?.let(Instant::ofEpochMilli),
    )

    fun toDocument(generator: Generator): GeneratorDocument = GeneratorDocument(
        id = generator.id,
        material = generator.material.name,
        item = ItemStackBytesCodec.encode(generator.item),
        amount = generator.amount,
        intervalMillis = generator.interval.toMillis(),
        activationRadius = generator.activationRadius,
        ownerId = generator.ownerId?.toString(),
        world = generator.location.world,
        x = generator.location.x,
        y = generator.location.y,
        z = generator.location.z,
        enabled = generator.enabled,
        createdAt = generator.createdAt.toEpochMilli(),
        lastGenerationAt = generator.lastGenerationAt?.toEpochMilli() ?: NO_TIMESTAMP,
    )

    /**
     * Decodes the stored item. New rows are Base64 encoded Bukkit item bytes,
     * older rows used an XItemStack YAML string and the oldest ones only stored
     * the `material` column - both fallbacks are supported here.
     */
    private fun resolveItem(serialized: String?, material: String): ItemStack =
        ItemStackBytesCodec.decode(serialized)
            ?: XItemStackCodec.decode(serialized)
            ?: XMaterial.matchXMaterial(material).orElse(XMaterial.STONE).parseItem()
            ?: ItemStack(Material.STONE)
}
