package eu.wejsonekk.skygen.skygengenerator.generator.entity

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable
import java.util.UUID

/**
 * ORMLite entity used by the relational ([eu.wejsonekk.skygen.skygengenerator.database.DatabaseManager])
 * storage. Kept separate from the domain model on purpose (adapter pattern).
 */
@DatabaseTable(tableName = "skygen_generators")
class GeneratorEntity(
    @field:DatabaseField(id = true, columnName = "id")
    var id: String = "",

    @field:DatabaseField(columnName = "material")
    var material: String = "STONE",

    @field:DatabaseField(columnName = "amount")
    var amount: Int = 1,

    @field:DatabaseField(columnName = "interval_millis")
    var intervalMillis: Long = 60_000L,

    @field:DatabaseField(columnName = "owner_id")
    var ownerId: UUID? = null,

    @field:DatabaseField(columnName = "world")
    var world: String = "",

    @field:DatabaseField(columnName = "x")
    var x: Int = 0,

    @field:DatabaseField(columnName = "y")
    var y: Int = 0,

    @field:DatabaseField(columnName = "z")
    var z: Int = 0,

    @field:DatabaseField(columnName = "enabled")
    var enabled: Boolean = true,

    @field:DatabaseField(columnName = "created_at")
    var createdAt: Long = 0L,

    @field:DatabaseField(columnName = "last_generation_at")
    var lastGenerationAt: Long = 0L,
)
