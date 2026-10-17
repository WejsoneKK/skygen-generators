package eu.wejsonekk.skygen.skygengenerator.generator.repository.impl

import com.mongodb.client.model.Filters
import com.mongodb.client.model.IndexModel
import com.mongodb.client.model.Indexes
import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.database.mongo.AbstractRepositoryMongo
import eu.wejsonekk.skygen.skygengenerator.database.mongo.MongoManager
import eu.wejsonekk.skygen.skygengenerator.generator.adapter.GeneratorAdapter
import eu.wejsonekk.skygen.skygengenerator.generator.document.GeneratorDocument
import eu.wejsonekk.skygen.skygengenerator.generator.repository.GeneratorRepository
import org.bson.Document
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

/**
 * MongoDB implementation.
 */
class MongoGeneratorRepository(
    mongoManager: MongoManager,
    executor: Executor,
) : AbstractRepositoryMongo<Generator, String>(mongoManager, executor, GeneratorDocument.COLLECTION),
    GeneratorRepository {

    override fun toDocument(entity: Generator): Document =
        GeneratorAdapter.toDocument(entity).toDocument()

    override fun fromDocument(document: Document): Generator =
        GeneratorAdapter.toModel(GeneratorDocument.fromDocument(document))

    override fun idOf(entity: Generator): String = entity.id

    override fun idValue(id: String): Any = id

    override fun indexes(): List<IndexModel> = listOf(
        IndexModel(Indexes.ascending(GeneratorDocument.OWNER_ID)),
        IndexModel(Indexes.ascending(GeneratorDocument.ENABLED)),
        IndexModel(
            Indexes.ascending(
                GeneratorDocument.WORLD,
                GeneratorDocument.X,
                GeneratorDocument.Y,
                GeneratorDocument.Z,
            ),
        ),
    )

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun save(generator: Generator): CompletableFuture<Void> =
        super.save(generator)

    override fun findByOwner(ownerId: UUID): CompletableFuture<List<Generator>> =
        this.action { collection ->
            collection.find(Filters.eq(GeneratorDocument.OWNER_ID, ownerId.toString())).toList()
        }.thenApply { documents -> documents.map { GeneratorAdapter.toModel(GeneratorDocument.fromDocument(it)) } }
}
