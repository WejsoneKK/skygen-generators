package eu.wejsonekk.skygen.skygengenerator.database.mongo

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment

class MongoConfig : OkaeriConfig(), MongoSettings {

    @Comment("Pelny connection string, np. mongodb://user:haslo@localhost:27017/?authSource=admin")
    override var connectionString = "mongodb://localhost:27017"

    override var database = "skygen_generators"

    @Comment("Maksymalna liczba polaczen w puli [Zalecane: 10]")
    override var poolSize = 10

    @Comment("Minimalna liczba bezczynnych polaczen [Zalecane: 2]")
    override var minimumIdle = 2
}
