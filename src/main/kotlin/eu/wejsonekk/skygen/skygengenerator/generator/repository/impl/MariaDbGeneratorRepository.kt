package eu.wejsonekk.skygen.skygengenerator.generator.repository.impl

import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import eu.wejsonekk.skygen.skygengenerator.database.AbstractRepositoryOrmLite
import eu.wejsonekk.skygen.skygengenerator.database.DatabaseManager
import eu.wejsonekk.skygen.skygengenerator.generator.adapter.GeneratorAdapter
import eu.wejsonekk.skygen.skygengenerator.generator.entity.GeneratorEntity
import eu.wejsonekk.skygen.skygengenerator.generator.repository.GeneratorRepository
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

/**
 * MariaDB / MySQL (relational) implementation backed by ORMLite.
 */
class MariaDbGeneratorRepository(
    databaseManager: DatabaseManager,
    executor: Executor,
) : AbstractRepositoryOrmLite<GeneratorEntity, String>(databaseManager, executor, GeneratorEntity::class.java),
    GeneratorRepository {

    override fun save(generator: Generator): CompletableFuture<Void> =
        this.save(GeneratorAdapter.toEntity(generator))

    override fun findById(id: String): CompletableFuture<Generator?> =
        this.selectSafe(id).thenApply { optional -> optional.map { GeneratorAdapter.toModel(it) }.orElse(null) }

    override fun findAll(): CompletableFuture<List<Generator>> =
        this.selectAll().thenApply { entities -> entities.map { GeneratorAdapter.toModel(it) } }

    override fun findByOwner(ownerId: UUID): CompletableFuture<List<Generator>> =
        this.action { dao -> dao.queryForEq("owner_id", ownerId) }
            .thenApply { entities -> entities.map { GeneratorAdapter.toModel(it) } }

    override fun removeById(id: String): CompletableFuture<Boolean> =
        this.deleteById(id).thenApply { deleted -> deleted > 0 }

    override fun existsById(id: String): CompletableFuture<Boolean> =
        this.action { dao -> dao.idExists(id) }
}
