package eu.wejsonekk.skygen.skygengenerator.generator.document

import org.bson.Document

/**
 * MongoDB document representation of a generator. Kept separate from the domain model
 * on purpose (adapter pattern).
 */
data class GeneratorDocument(
    val id: String,
    val material: String,
    val item: String?,
    val amount: Int,
    val intervalMillis: Long,
    val activationRadius: Double?,
    val ownerId: String?,
    val world: String,
    val x: Int,
    val y: Int,
    val z: Int,
    val enabled: Boolean,
    val createdAt: Long,
    val lastGenerationAt: Long,
) {

    fun toDocument(): Document = Document(ID, this.id)
        .append(MATERIAL, this.material)
        .append(ITEM, this.item)
        .append(AMOUNT, this.amount)
        .append(INTERVAL_MILLIS, this.intervalMillis)
        .append(ACTIVATION_RADIUS, this.activationRadius)
        .append(OWNER_ID, this.ownerId)
        .append(WORLD, this.world)
        .append(X, this.x)
        .append(Y, this.y)
        .append(Z, this.z)
        .append(ENABLED, this.enabled)
        .append(CREATED_AT, this.createdAt)
        .append(LAST_GENERATION_AT, this.lastGenerationAt)

    companion object {
        const val COLLECTION = "skygen_generators"

        const val ID = "_id"
        const val MATERIAL = "material"
        const val ITEM = "item"
        const val AMOUNT = "amount"
        const val INTERVAL_MILLIS = "intervalMillis"
        const val ACTIVATION_RADIUS = "activationRadius"
        const val OWNER_ID = "ownerId"
        const val WORLD = "world"
        const val X = "x"
        const val Y = "y"
        const val Z = "z"
        const val ENABLED = "enabled"
        const val CREATED_AT = "createdAt"
        const val LAST_GENERATION_AT = "lastGenerationAt"

        @JvmStatic
        fun fromDocument(document: Document): GeneratorDocument = GeneratorDocument(
            id = document.getString(ID),
            material = document.getString(MATERIAL) ?: "STONE",
            item = document.getString(ITEM),
            amount = document.getInteger(AMOUNT, 1),
            intervalMillis = document.getLong(INTERVAL_MILLIS) ?: 60_000L,
            activationRadius = document.getDouble(ACTIVATION_RADIUS),
            ownerId = document.getString(OWNER_ID),
            world = document.getString(WORLD) ?: "world",
            x = document.getInteger(X, 0),
            y = document.getInteger(Y, 0),
            z = document.getInteger(Z, 0),
            enabled = document.getBoolean(ENABLED, true),
            createdAt = document.getLong(CREATED_AT) ?: 0L,
            lastGenerationAt = document.getLong(LAST_GENERATION_AT) ?: 0L,
        )
    }
}
