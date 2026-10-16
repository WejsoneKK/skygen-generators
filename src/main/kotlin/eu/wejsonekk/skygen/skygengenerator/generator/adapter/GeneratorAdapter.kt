package eu.wejsonekk.skygen.skygengenerator.generator.adapter

import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.api.generator.GeneratorLocation
import eu.wejsonekk.skygen.skygengenerator.generator.document.GeneratorDocument
import eu.wejsonekk.skygen.skygengenerator.generator.entity.GeneratorEntity
import eu.wejsonekk.skygen.skygengenerator.generator.model.GeneratorImpl
import com.cryptomorin.xseries.XMaterial
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
        material = XMaterial.matchXMaterial(entity.material).orElse(XMaterial.STONE),
        amount = entity.amount,
        interval = Duration.ofMillis(entity.intervalMillis),
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
        amount = generator.amount,
        intervalMillis = generator.interval.toMillis(),
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
        material = XMaterial.matchXMaterial(document.material).orElse(XMaterial.STONE),
        amount = document.amount,
        interval = Duration.ofMillis(document.intervalMillis),
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
        amount = generator.amount,
        intervalMillis = generator.interval.toMillis(),
        ownerId = generator.ownerId?.toString(),
        world = generator.location.world,
        x = generator.location.x,
        y = generator.location.y,
        z = generator.location.z,
        enabled = generator.enabled,
        createdAt = generator.createdAt.toEpochMilli(),
        lastGenerationAt = generator.lastGenerationAt?.toEpochMilli() ?: NO_TIMESTAMP,
    )
}
