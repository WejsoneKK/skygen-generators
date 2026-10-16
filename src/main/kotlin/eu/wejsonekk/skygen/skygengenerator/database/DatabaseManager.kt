package eu.wejsonekk.skygen.skygengenerator.database

import com.j256.ormlite.dao.Dao
import com.j256.ormlite.dao.DaoManager
import com.j256.ormlite.jdbc.DataSourceConnectionSource
import com.j256.ormlite.support.ConnectionSource
import com.zaxxer.hikari.HikariDataSource
import org.slf4j.Logger
import java.io.File
import java.util.concurrent.ConcurrentHashMap

class DatabaseManager(
    private val databaseConfig: DatabaseSettings,
    private val logger: Logger,
    private val dataFolder: File
) {

    private val cachedDao = ConcurrentHashMap<Class<*>, Dao<*, *>>()
    var dataSource: HikariDataSource? = null
        private set
    var connectionSource: ConnectionSource? = null
        private set

    fun connect() {
        if(dataSource?.isClosed == false) return

        val started = System.currentTimeMillis()
        dataSource = HikariDataSource().apply {
            addDataSourceProperty("cachePrepStmts", "true")
            addDataSourceProperty("prepStmtCacheSize", "250")
            addDataSourceProperty("prepStmtCacheSqlLimit", "2048")
            maximumPoolSize = databaseConfig.poolSize
            minimumIdle = databaseConfig.minimumIdle
            connectionTimeout = databaseConfig.connectionTimeout.toLong()
            idleTimeout = databaseConfig.idleTimeout.toLong()
            maxLifetime = databaseConfig.maxLifetime.toLong()
            username = databaseConfig.username
            password = databaseConfig.password

            when (databaseConfig.databaseType) {
                DatabaseType.MY_SQL -> {
                    driverClassName = DatabaseConnectionDriverConstant.MYSQL_DRIVER
                    jdbcUrl = String.format(DatabaseConnectionDriverConstant.MYSQL_JDBC_URL, databaseConfig.hostName, databaseConfig.port, databaseConfig.database, databaseConfig.ssl, databaseConfig.ssl)
                }
                DatabaseType.MARIA_DB -> {
                    driverClassName = DatabaseConnectionDriverConstant.MARIADB_DRIVER
                    jdbcUrl = String.format(DatabaseConnectionDriverConstant.MARIADB_JDBC_URL, databaseConfig.hostName, databaseConfig.port, databaseConfig.database, databaseConfig.ssl, databaseConfig.ssl)
                }
                DatabaseType.POSTGRE_SQL -> {
                    driverClassName = DatabaseConnectionDriverConstant.POSTGRESQL_DRIVER
                    jdbcUrl = String.format(DatabaseConnectionDriverConstant.POSTGRESQL_JDBC_URL, databaseConfig.hostName, databaseConfig.port, databaseConfig.ssl)
                }
                DatabaseType.H2 -> {
                    driverClassName = DatabaseConnectionDriverConstant.H2_DRIVER
                    jdbcUrl = String.format(DatabaseConnectionDriverConstant.H2_JDBC_URL, dataFolder)
                }
                DatabaseType.SQLITE -> {
                    driverClassName = DatabaseConnectionDriverConstant.SQLITE_DRIVER
                    jdbcUrl = String.format(DatabaseConnectionDriverConstant.SQLITE_JDBC_URL, dataFolder)
                }
            }
        }
        connectionSource = DataSourceConnectionSource(dataSource, dataSource!!.jdbcUrl)
        logger.info("Loaded database {}, in {}ms", databaseConfig.databaseType, System.currentTimeMillis() - started)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T, ID> getDao(type: Class<T>): Dao<T, ID> {
        return cachedDao.getOrPut(type) {
            DaoManager.createDao(connectionSource, type)
        } as Dao<T, ID>
    }

    val isConnected: Boolean
        get() = dataSource?.isRunning == true

    fun disconnect() {
        try {
            connectionSource?.close()
        } catch (e: Exception) {
            logger.warn("Failed to disconnect from database: {}", e.message)
        } finally {
            connectionSource = null
            dataSource?.close()
            dataSource = null
            cachedDao.clear()
        }
    }
}