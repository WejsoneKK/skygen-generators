package eu.wejsonekk.skygen.skygengenerator

import com.eternalcode.commons.bukkit.scheduler.BukkitLoomScheduler
import dev.rollczi.litecommands.LiteCommands
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory
import eu.wejsonekk.skygen.skygengenerator.config.ConfigService
import eu.wejsonekk.skygen.skygengenerator.config.impl.MessageConfig
import eu.wejsonekk.skygen.skygengenerator.config.impl.PluginConfig
import eu.wejsonekk.skygen.skygengenerator.database.DatabaseConfig
import eu.wejsonekk.skygen.skygengenerator.database.DatabaseManager
import eu.wejsonekk.skygen.skygengenerator.litecommand.InvalidUsageHandlerImpl
import eu.wejsonekk.skygen.skygengenerator.litecommand.MissingPermissionHandlerImpl
import eu.wejsonekk.skygen.skygengenerator.notice.NoticeService
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class SkyGenGeneratorPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: SkyGenGeneratorPlugin
    }
    lateinit var loomScheduler: BukkitLoomScheduler
    lateinit var litecommands: LiteCommands<CommandSender>
    lateinit var configService: ConfigService
    lateinit var noticeService: NoticeService
    lateinit var messageConfig: MessageConfig
    lateinit var pluginConfig: PluginConfig
    lateinit var miniMessage: MiniMessage
    lateinit var databaseManager: DatabaseManager
    lateinit var databaseConfig: DatabaseConfig
    var logger: Logger
    init {
        instance = this
        this.logger = LoggerFactory.getLogger(this.javaClass)
    }

    override fun onEnable() {

        val started = System.currentTimeMillis()
        this.loomScheduler = BukkitLoomScheduler.create(this)
        this.logger.info("Started loading plugin")

        this.configService = ConfigService()
        this.configService.create<PluginConfig>(this.dataFolder.resolve("config.yml"))
        this.configService.create<MessageConfig>(this.dataFolder.resolve("messages.yml"))
        this.databaseConfig = this.configService.create<DatabaseConfig>(this.dataFolder.resolve("database.yml"))
        this.noticeService = NoticeService(this.messageConfig, this.miniMessage)

        this.databaseManager = DatabaseManager(this.databaseConfig, this.logger, this.dataFolder)
        this.databaseManager.connect()

        this.litecommands = LiteBukkitFactory.builder("wSkyGen-Generators", this)
            .invalidUsage(InvalidUsageHandlerImpl(this.noticeService))
            .missingPermission(MissingPermissionHandlerImpl(this.noticeService))
            .build()

        this.logger.info("Enabled Plugin in {}ms", System.currentTimeMillis() - started)

    }

    override fun onDisable() {

        this.logger.info("Disabling Plugin Good bye \uD83D\uDC4B")

        if(this::litecommands.isInitialized) {
            this.litecommands.commandManager.unregisterAll()
        }

        if(this::databaseManager.isInitialized) {
            this.databaseManager.disconnect()
        }
        this.loomScheduler.shutdown(java.time.Duration.ofSeconds(5))

        this.logger.info("Disabled Plugin")
    }
}
