package eu.wejsonekk.skygen.skygengenerator.generator.repository

import eu.wejsonekk.skygen.skygengenerator.api.generator.Generator
import java.util.UUID
import java.util.concurrent.CompletableFuture

/**
 * Persistence contract for [Generator]s. Implemented for every supported
 * storage backend (MongoDB, MariaDB, ...).
 */
interface GeneratorRepository {

    fun save(generator: Generator): CompletableFuture<Void>

    fun findById(id: String): CompletableFuture<Generator?>

    fun findAll(): CompletableFuture<List<Generator>>

    fun findByOwner(ownerId: UUID): CompletableFuture<List<Generator>>

    fun removeById(id: String): CompletableFuture<Boolean>

    fun existsById(id: String): CompletableFuture<Boolean>
}
