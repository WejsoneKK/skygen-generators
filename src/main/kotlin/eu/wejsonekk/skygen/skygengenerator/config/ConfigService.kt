package eu.wejsonekk.skygen.skygengenerator.config

import com.eternalcode.multification.notice.resolver.NoticeResolverDefaults
import com.eternalcode.multification.notice.resolver.sound.SoundAdventureResolver
import com.eternalcode.multification.okaeri.MultificationSerdesPack
import eu.okaeri.configs.ConfigManager
import eu.okaeri.configs.OkaeriConfig
import eu.okaeri.configs.serdes.adventure.SerdesAdventure
import eu.okaeri.configs.serdes.commons.SerdesCommons
import eu.okaeri.configs.yaml.bukkit.serdes.SerdesBukkit
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer
import eu.wejsonekk.skygen.skygengenerator.config.serdes.ItemStackSerdesPack
import java.io.File

/**
 * Central factory / registry for every [OkaeriConfig] used by the plugin.
 *
 * Each config is created with a fixed set of serdes packs (commons, adventure,
 * multification) so that [java.time.Duration], [java.time.Instant], MiniMessage
 * [com.eternalcode.multification.notice.Notice]s and nested sub-configs are all
 * serialized automatically. Call [saveAll] before shutdown to flush changes.
 */
class ConfigService {

    private val configs = LinkedHashMap<Class<out OkaeriConfig>, OkaeriConfig>()

    inline fun <reified T : OkaeriConfig> create(file: File): T = this.create(T::class.java, file)

    fun <T : OkaeriConfig> create(type: Class<T>, file: File): T {
        val noticeRegistry = NoticeResolverDefaults.createRegistry()
            .registerResolver(SoundAdventureResolver())

        val config = ConfigManager.create(type).apply {
            configure { options ->
                options.configurer(
                    YamlSnakeYamlConfigurer(),
                    SerdesCommons(),
                    SerdesBukkit(),
                    ItemStackSerdesPack(),
                    SerdesAdventure(),
                    MultificationSerdesPack(noticeRegistry),
                )
                options.bindFile(file)
                options.removeOrphans(true)
            }
            saveDefaults()
            load(true)
        }

        this.configs[type] = config
        return config
    }

    fun reload() {
        this.configs.values.forEach { it.load() }
    }

    fun saveAll() {
        this.configs.values.forEach { it.save() }
    }

    inline fun <reified T : OkaeriConfig> get(): T? = this.get(T::class.java)

    @Suppress("UNCHECKED_CAST")
    fun <T : OkaeriConfig> get(type: Class<T>): T? = this.configs[type] as T?
}
