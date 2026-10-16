package eu.wejsonekk.skygen.skygengenerator.database.mongo

import com.mongodb.client.MongoCollection
import org.bson.Document
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.Executor

/**
 * Base class for MongoDB backed repositories. Mirrors [eu.wejsonekk.skygen.skygengenerator.database.AbstractRepositoryOrmLite]
 * but operates on raw [Document]s.
 */
abstract class AbstractRepositoryMongo<T : Any, ID>(
    protected val mongoManager: MongoManager,
    protected val executor: Executor,
    protected val collectionName: String,
) {

    protected val collection: MongoCollection<Document>
        get() = this.mongoManager.collection(this.collectionName)

    protected fun <R> action(block: (MongoCollection<Document>) -> R): CompletableFuture<R> =
        CompletableFuture.supplyAsync({
            try {
                block(this.collection)
            } catch (exception: Exception) {
                throw CompletionException(
                    RuntimeException("MongoDB action failed for collection: ${this.collectionName}", exception),
                )
            }
        }, this.executor)
}
