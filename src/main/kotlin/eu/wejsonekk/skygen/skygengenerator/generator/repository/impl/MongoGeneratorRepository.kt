package eu.wejsonekk.skygen.skygengenerator.generator.repository.impl

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.database.mongo.AbstractRepositoryMongo
import eu.wejsonekk.skygen.skygengenerator.database.mongo.MongoManager
import eu.wejsonekk.skygen.skygengenerator.generator.adapter.GeneratorAdapter
import eu.wejsonekk.skygen.skygengenerator.generator.document.GeneratorDocument
import eu.wejsonekk.skygen.skygengenerator.generator.repository.GeneratorRepository
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

/**
 * MongoDB implementation.
 */
class MongoGeneratorRepository(
    mongoManager: MongoManager,
    executor: Executor,
) : AbstractRepositoryMongo<GeneratorDocument, String>(mongoManager, executor, GeneratorDocument.COLLECTION),
    GeneratorRepository {

    override fun save(generator: Generator): CompletableFuture<Void> {
        val document = GeneratorAdapter.toDocument(generator)
        return this.action { collection ->
            collection.replaceOne(
                Filters.eq(GeneratorDocument.ID, document.id),
                document.toDocument(),
                ReplaceOptions().upsert(true),
            )
        }.thenAccept { }
    }

    override fun findById(id: String): CompletableFuture<Generator?> =
        this.action { collection ->
            collection.find(Filters.eq(GeneratorDocument.ID, id)).first()
        }.thenApply { document ->
            document?.let { GeneratorAdapter.toModel(GeneratorDocument.fromDocument(it)) }
        }

    override fun findAll(): CompletableFuture<List<Generator>> =
        this.action { collection -> collection.find().toList() }
            .thenApply { documents ->
                documents.map { GeneratorAdapter.toModel(GeneratorDocument.fromDocument(it)) }
            }

    override fun findByOwner(ownerId: UUID): CompletableFuture<List<Generator>> =
        this.action { collection ->
            collection.find(Filters.eq(GeneratorDocument.OWNER_ID, ownerId.toString())).toList()
        }.thenApply { documents ->
            documents.map { GeneratorAdapter.toModel(GeneratorDocument.fromDocument(it)) }
        }

    override fun removeById(id: String): CompletableFuture<Boolean> =
        this.action { collection ->
            collection.deleteOne(Filters.eq(GeneratorDocument.ID, id)).deletedCount > 0
        }

    override fun existsById(id: String): CompletableFuture<Boolean> =
        this.action { collection ->
            collection.countDocuments(Filters.eq(GeneratorDocument.ID, id)) > 0
        }
}
