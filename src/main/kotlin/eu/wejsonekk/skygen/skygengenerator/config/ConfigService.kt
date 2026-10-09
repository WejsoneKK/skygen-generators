package eu.wejsonekk.skygen.skygengenerator.config

import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults
import com.eternalcode.multification.notice.resolver.sound.SoundAdventureResolver
import com.eternalcode.multification.okaeri.MultificationSerdesPack
import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.serdes.OkaeriSerdes
import eu.okaeri.configs.serdes.SerdesRegistry
import eu.okaeri.configs.serdes.adventure.SerdesAdventure
import eu.okaeri.configs.serdes.commons.SerdesCommons
import eu.okaeri.configs.serdes.commons.serializer.InstantSerializer
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer
import java.io.File
import java.time.Instant

class ConfigService {
    private val configs = mutableSetOf<OkaeriConfig>()

    // Wygodny skrót dla Kotlina: service.create<MyConfig>(file)
    inline fun <reified T : OkaeriConfig> create(file: File): T =
        create(T::class.java, file)

    fun <T : OkaeriConfig> create(config: Class<T>, file: File): T {
        val noticeRegistry = NoticeResolverDefaults.createRegistry()
            .registerResolver(SoundAdventureResolver())

        val configFile = ConfigManager.create(config).apply {
            withConfigurer(
                YamlSnakeYamlConfigurer(),
                SerdesCommons(),
                SerdesAdventure(),
                DefaultSerdesPack,
                MultificationSerdesPack(noticeRegistry)
            )
            withBindFile(file)
            withRemoveOrphans(true)
            saveDefaults()
            load(true)
        }

        configs += configFile
        return configFile
    }

    fun reload() {
        configs.forEach { it.load() }
    }

    private object DefaultSerdesPack : OkaeriSerdes {
        override fun register(registry: SerdesRegistry) {
            registry.register(SerdesCommons())
            registry.register(SerdesAdventure())
            registry.registerExclusive(Instant::class.java, InstantSerializer(false))
        }
    }
}