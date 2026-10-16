package eu.wejsonekk.skygen.skygengenerator.database.mongo

/**
 * MongoDB connection settings.
 */
interface MongoSettings {

    val connectionString: String
    val database: String
    val poolSize: Int
    val minimumIdle: Int
}
