package eu.wejsonekk.skygen.skygengenerator.database

interface DatabaseSettings {

    val databaseType: DatabaseType
    val hostName: String
    val port: Int
    val database: String
    val username: String
    val password: String
    val ssl: Boolean
    val connectionTimeout: Int
    val idleTimeout: Int
    val poolSize: Int
    val minimumIdle: Int
    val maxLifetime: Int
}