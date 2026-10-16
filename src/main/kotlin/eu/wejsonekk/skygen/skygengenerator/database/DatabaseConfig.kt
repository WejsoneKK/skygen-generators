package eu.wejsonekk.skygen.skygengenerator.database

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment
import eu.wejsonekk.skygen.skygengenerator.database.mongo.MongoConfig

/**
 * Root database configuration. The active backend is selected through [storageType];
 * the matching nested section is then used to establish the connection.
 */
class DatabaseConfig : OkaeriConfig() {

    @Comment("Backend used to persist generators: SQL or MONGO")
    var storageType: StorageType = StorageType.SQL

    @Comment("Relational (MariaDB / MySQL / ...) settings")
    var sql: SqlDatabaseConfig = SqlDatabaseConfig()

    @Comment("MongoDB settings")
    var mongo: MongoConfig = MongoConfig()
}
