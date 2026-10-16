package eu.wejsonekk.skygen.skygengenerator.database

/**
 * Storage backend used by the generator repositories.
 */
enum class StorageType {

    /** Relational storage through JDBC / HikariCP (MariaDB, MySQL, ...). */
    SQL,

    /** Document storage through the official MongoDB driver. */
    MONGO,
}
