package eu.wejsonekk.skygen.skygengenerator.database.mongo

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import org.bson.Document
import org.slf4j.Logger

/**
 * Owns the lifecycle of the MongoDB client and exposes collections to repositories.
 */
class MongoManager(
    private val settings: MongoSettings,
    private val logger: Logger,
) {

    private var mongoClient: MongoClient? = null
    private var database: MongoDatabase? = null

    val isConnected: Boolean
        get() = this.database != null

    /** The active [MongoClient]; used for session based transactions. */
    val client: MongoClient
        get() = this.mongoClient ?: error("MongoDB is not connected")

    fun connect() {
        if (this.isConnected) {
            return
        }

        val started = System.currentTimeMillis()

        val clientSettings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(this.settings.connectionString))
            .applyToConnectionPoolSettings { pool ->
                pool.maxSize(this.settings.poolSize)
                pool.minSize(this.settings.minimumIdle)
            }
            .build()

        val mongoClient = MongoClients.create(clientSettings)
        val mongoDatabase = mongoClient.getDatabase(this.settings.database)

        mongoDatabase.runCommand(Document("ping", 1))

        this.mongoClient = mongoClient
        this.database = mongoDatabase

        this.logger.info(
            "Loaded MongoDB database {}, in {}ms",
            this.settings.database,
            System.currentTimeMillis() - started,
        )
    }

    fun collection(name: String): MongoCollection<Document> {
        val mongoDatabase = this.database ?: error("MongoDB is not connected")
        return mongoDatabase.getCollection(name)
    }

    fun disconnect() {
        try {
            this.mongoClient?.close()
        } catch (exception: Exception) {
            this.logger.warn("Failed to disconnect from MongoDB: {}", exception.message)
        } finally {
            this.mongoClient = null
            this.database = null
        }
    }
}
