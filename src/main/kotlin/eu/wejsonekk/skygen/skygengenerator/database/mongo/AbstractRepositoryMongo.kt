package eu.wejsonekk.skygen.skygengenerator.database.mongo

import com.mongodb.client.ClientSession
import com.mongodb.client.FindIterable
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.Filters
import com.mongodb.client.model.IndexModel
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.result.DeleteResult
import com.mongodb.client.result.InsertOneResult
import com.mongodb.client.result.UpdateResult
import org.bson.Document
import org.bson.conversions.Bson
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.Executor

/**
 * Base class for MongoDB backed repositories. Mirrors
 * [eu.wejsonekk.skygen.skygengenerator.database.AbstractRepositoryOrmLite] but operates on
 * raw [Document]s.
 *
 * Subclasses only provide the entity <-> document mapping through [toDocument],
 * [fromDocument], [idOf] and [idValue]; the whole CRUD assortment is implemented
 * here. Index creation is a separate, explicit step ([createIndexes]) driven by
 * [indexes], so it can be run once on startup.
 */
abstract class AbstractRepositoryMongo<T : Any, ID>(
    protected val mongoManager: MongoManager,
    protected val executor: Executor,
    protected val collectionName: String,
) {

    private val currentSession = ThreadLocal<ClientSession?>()

    protected val collection: MongoCollection<Document>
        get() = this.mongoManager.collection(this.collectionName)

    /** Field used as the primary key inside the underlying document. */
    protected open val idField: String = "_id"

    // --- mapping hooks (implemented by subclasses) ---

    protected abstract fun toDocument(entity: T): Document

    protected abstract fun fromDocument(document: Document): T

    protected abstract fun idOf(entity: T): ID

    protected abstract fun idValue(id: ID): Any

    // --- indexes (implemented by subclasses) ---

    /** Indexes that should exist for this collection. */
    protected open fun indexes(): List<IndexModel> = emptyList()

    /**
     * Creates every index declared in [indexes] on the underlying collection.
     * Kept as a separate step so it can be executed once during startup.
     */
    fun createIndexes(): CompletableFuture<Void> =
        this.action { collection ->
            val indexes = this.indexes()
            if (indexes.isNotEmpty()) collection.applyIndexes(indexes, this.currentSession.get())
        }.thenAccept { }

    // --- create / update ---

    open fun save(entity: T): CompletableFuture<Void> =
        this.upsert(entity).thenAccept { }

    fun upsert(entity: T): CompletableFuture<Document> {
        val document = this.toDocument(entity)
        return this.action { collection ->
            collection.replaceOneSession(
                Filters.eq(this.idField, document[this.idField]),
                document,
                ReplaceOptions().upsert(true),
                this.currentSession.get(),
            )
            document
        }
    }

    fun insert(entity: T): CompletableFuture<Int> =
        this.action { collection ->
            collection.insertOneSession(this.toDocument(entity), this.currentSession.get())
            1
        }

    fun insertIfAbsent(entity: T): CompletableFuture<T> {
        val document = this.toDocument(entity)
        return this.action { collection ->
            collection.updateOneSession(
                Filters.eq(this.idField, document[this.idField]),
                Document("\$setOnInsert", document),
                UpdateOptions().upsert(true),
                this.currentSession.get(),
            )
            entity
        }
    }

    // --- read ---

    fun findById(id: ID): CompletableFuture<T?> =
        this.action { collection ->
            collection.findSession(Filters.eq(this.idField, this.idValue(id)), this.currentSession.get()).first()
        }.thenApply { document -> document?.let(this::fromDocument) }

    fun select(id: ID): CompletableFuture<T> =
        this.findById(id).thenApply { entity ->
            entity ?: throw NoSuchElementException("No document with id '$id' in collection '${this.collectionName}'")
        }

    fun selectSafe(id: ID): CompletableFuture<T?> =
        this.findById(id)

    fun findAll(): CompletableFuture<List<T>> =
        this.action { collection ->
            collection.findSession(Document(), this.currentSession.get()).toList()
        }.thenApply { documents -> documents.map(this::fromDocument) }

    fun selectAll(): CompletableFuture<List<T>> =
        this.findAll()

    fun existsById(id: ID): CompletableFuture<Boolean> =
        this.action { collection ->
            collection.countSession(Filters.eq(this.idField, this.idValue(id)), this.currentSession.get()) > 0
        }

    fun count(): CompletableFuture<Long> =
        this.action { collection ->
            collection.countSession(this.currentSession.get())
        }

    // --- delete ---

    fun delete(entity: T): CompletableFuture<Int> =
        this.deleteById(this.idOf(entity))

    fun deleteById(id: ID): CompletableFuture<Int> =
        this.action { collection ->
            collection.deleteOneSession(Filters.eq(this.idField, this.idValue(id)), this.currentSession.get())
                .deletedCount.toInt()
        }

    fun deleteAll(): CompletableFuture<Int> =
        this.action { collection ->
            collection.deleteManySession(Document(), this.currentSession.get()).deletedCount.toInt()
        }

    fun removeById(id: ID): CompletableFuture<Boolean> =
        this.deleteById(id).thenApply { deleted -> deleted > 0 }

    // --- transactions ---

    /**
     * Runs [action] inside a MongoDB session transaction. Nested repository calls
     * executed by [action] automatically join the transaction because [action]
     * runs synchronously while a session is bound to the current thread.
     */
    fun <R> transaction(action: () -> R): CompletableFuture<R> =
        CompletableFuture.supplyAsync({
            val session = this.mongoManager.client.startSession()
            try {
                session.startTransaction()
                this.currentSession.set(session)
                val result = action()
                session.commitTransaction()
                result
            } catch (exception: Exception) {
                session.abortTransaction()
                throw CompletionException(
                    RuntimeException("MongoDB transaction failed for collection: ${this.collectionName}", exception),
                )
            } finally {
                this.currentSession.remove()
                session.close()
            }
        }, this.executor)

    // --- internals ---

    protected fun <R> action(block: (MongoCollection<Document>) -> R): CompletableFuture<R> {
        if (this.currentSession.get() != null) {
            return try {
                CompletableFuture.completedFuture(block(this.collection))
            } catch (exception: Exception) {
                CompletableFuture.failedFuture(
                    CompletionException(
                        RuntimeException("MongoDB action failed for collection: ${this.collectionName}", exception),
                    ),
                )
            }
        }

        return CompletableFuture.supplyAsync({
            try {
                block(this.collection)
            } catch (exception: Exception) {
                throw CompletionException(
                    RuntimeException("MongoDB action failed for collection: ${this.collectionName}", exception),
                )
            }
        }, this.executor)
    }

    private fun MongoCollection<Document>.applyIndexes(indexes: List<IndexModel>, session: ClientSession?) {
        if (session == null) {
            this.createIndexes(indexes)
        } else {
            this.createIndexes(session, indexes)
        }
    }

    private fun MongoCollection<Document>.findSession(filter: Bson, session: ClientSession?): FindIterable<Document> =
        if (session == null) this.find(filter) else this.find(session, filter)

    private fun MongoCollection<Document>.replaceOneSession(
        filter: Bson,
        replacement: Document,
        options: ReplaceOptions,
        session: ClientSession?,
    ): UpdateResult =
        if (session == null) this.replaceOne(filter, replacement, options)
        else this.replaceOne(session, filter, replacement, options)

    private fun MongoCollection<Document>.insertOneSession(document: Document, session: ClientSession?): InsertOneResult =
        if (session == null) this.insertOne(document) else this.insertOne(session, document)

    private fun MongoCollection<Document>.updateOneSession(
        filter: Bson,
        update: Document,
        options: UpdateOptions,
        session: ClientSession?,
    ): UpdateResult =
        if (session == null) this.updateOne(filter, update, options)
        else this.updateOne(session, filter, update, options)

    private fun MongoCollection<Document>.deleteOneSession(filter: Bson, session: ClientSession?): DeleteResult =
        if (session == null) this.deleteOne(filter) else this.deleteOne(session, filter)

    private fun MongoCollection<Document>.deleteManySession(filter: Bson, session: ClientSession?): DeleteResult =
        if (session == null) this.deleteMany(filter) else this.deleteMany(session, filter)

    private fun MongoCollection<Document>.countSession(session: ClientSession?): Long =
        if (session == null) this.countDocuments() else this.countDocuments(session)

    private fun MongoCollection<Document>.countSession(filter: Bson, session: ClientSession?): Long =
        if (session == null) this.countDocuments(filter) else this.countDocuments(session, filter)
}
