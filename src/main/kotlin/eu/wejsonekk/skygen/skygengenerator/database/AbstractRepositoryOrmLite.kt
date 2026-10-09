package eu.wejsonekk.skygen.skygengenerator.database

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.misc.TransactionManager
import com.j256.ormlite.table.TableUtils
import java.sql.SQLException
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionException
import java.util.concurrent.Executor

abstract class AbstractRepositoryOrmLite<T : Any, ID>(
    protected val databaseManager: DatabaseManager,
    protected val executor: Executor,
    protected val entityClass: Class<T>,
){

    init {
        createTable()
    }

    private fun createTable() {
        try {
            TableUtils.createTableIfNotExists(databaseManager.connectionSource, entityClass)
        }catch (e: Exception) {
            throw RuntimeException("Failed to create table ${entityClass.simpleName}", e)
        }
    }

    protected fun upsert(entity: T): CompletableFuture<Dao.CreateOrUpdateStatus> =
        action { it.createOrUpdate(entity) }

    protected fun save(entity: T): CompletableFuture<Void> =
        upsert(entity).thenApply { null }

    protected fun insert(entity: T): CompletableFuture<Int> =
        action { it.create(entity) }

    protected fun insertIfAbsent(entity: T): CompletableFuture<T> =
        action { it.createIfNotExists(entity) }

    protected fun select(id: ID): CompletableFuture<T> =
        action { it.queryForId(id) }

    protected fun selectSafe(id: ID): CompletableFuture<Optional<T>> =
        action { Optional.ofNullable(it.queryForId(id)) }

    protected fun delete(entity: T): CompletableFuture<Int> =
        action { it.delete(entity) }

    protected fun deleteAll(): CompletableFuture<Int> =
        action { it.deleteBuilder().delete() }

    protected fun deleteById(id: ID): CompletableFuture<Int> =
        action { it.deleteById(id) }

    protected fun selectAll(): CompletableFuture<List<T>> =
        action { it.queryForAll() }

    protected fun <R> transaction(action: () -> R): CompletableFuture<R> =
        CompletableFuture.supplyAsync({
            try {
                TransactionManager.callInTransaction(databaseManager.connectionSource) { action() }
            } catch (exception: Exception) {
                throw CompletionException(RuntimeException("Database transaction failed", exception))
            }
        }, executor)

    protected fun <R> action(action: (Dao<T, ID>) -> R): CompletableFuture<R> =
        CompletableFuture.supplyAsync({
            val dao: Dao<T, ID> = try {
                databaseManager.getDao(entityClass)
            } catch (exception: Exception) {
                throw CompletionException(
                    RuntimeException(
                        "Failed to get DAO for class: ${entityClass.name}",
                        exception
                    )
                )
            }
            try {
                action(dao)
            } catch (exception: SQLException) {
                throw CompletionException(RuntimeException("Database action failed for: ${entityClass.simpleName}", exception))
            }
        }, executor)}