package eu.wejsonekk.goathub.proxycore.goathubProxyCore.database

object DatabaseConnectionDriverConstant {
    const val MYSQL_DRIVER = "com.mysql.cj.jdbc.Driver"
    const val MYSQL_JDBC_URL = "jdbc:mysql://%s:%s/%s?useSSL=%b&requireSSL=%b"
    const val MARIADB_DRIVER = "org.mariadb.jdbc.Driver"
    const val MARIADB_JDBC_URL = "jdbc:mariadb://%s:%s/%s?useSSL=%b&requireSSL=%b"
    const val H2_DRIVER = "org.h2.Driver"
    const val H2_JDBC_URL = "jdbc:h2:./%s/database"
    const val SQLITE_DRIVER = "org.sqlite.JDBC"
    const val SQLITE_JDBC_URL = "jdbc:sqlite:%s/database.db"
    const val POSTGRESQL_DRIVER = "org.postgresql.Driver"
    const val POSTGRESQL_JDBC_URL = "jdbc:postgresql://%s:%s/?ssl=%b"
}