package eu.wejsonekk.skygen.skygengenerator.database

import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.annotation.Comment

class DatabaseConfig(
) : OkaeriConfig(), DatabaseSettings {

    @Comment("Type of the database driver")
    override var databaseType = DatabaseType.SQLITE
    override var hostName = "localhost"
    override var port = 3306
    override var database = "skygen_generators"
    override var username = "generators"
    override var password = "Str0ngP4ssw0rd"
    override var ssl = false
    @Comment("Maksymalny czas oczekiwania na wolne połączenie z puli (ms) [Zalecane: 5000]")
    override var connectionTimeout = 5_000

    @Comment("Maksymalny czas bezczynności połączenia przed usunięciem z puli (ms) [Zalecane: 600 000 = 10 min]")
    override var idleTimeout = 600_000

    @Comment("Maksymalny czas życia połączenia w puli (ms) [Zalecane: 1 800 000 = 30 min]")
    @Comment("WAŻNE: Musi być o 2-3 minuty krótszy niż 'wait_timeout' w konfiguracji MySQL!")
    override var maxLifetime = 1_800_000

    @Comment("Maksymalna liczba połączeń w puli [Zalecane: 8 - 10 dla Proxy, 10 - 16 dla Paper]")
    override var poolSize = 10

    @Comment("Minimalna liczba bezczynnych połączeńutrzymywanych w tle [Zalecane: 2 - 4]")
    var minimumIdle = 2
}