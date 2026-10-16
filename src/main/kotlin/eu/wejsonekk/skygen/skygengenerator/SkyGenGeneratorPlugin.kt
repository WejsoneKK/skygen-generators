package eu.wejsonekk.skygen.skygengenerator

import com.eternalcode.commons.bukkit.scheduler.BukkitLoomScheduler
import dev.rollczi.litecommands.LiteCommands
import dev.rollczi.litecommands.bukkit.LiteBukkitFactory
import eu.wejsonekk.skygen.skygengenerator.api.SkyGenBoostApi
import eu.wejsonekk.skygen.skygengenerator.api.SkyGenGeneratorsApi
import eu.wejsonekk.skygen.skygengenerator.config.ConfigService
import eu.wejsonekk.skygen.skygengenerator.config.impl.GenBoostConfig
import eu.wejsonekk.skygen.skygengenerator.config.impl.GeneratorConfig
import eu.wejsonekk.skygen.skygengenerator.config.impl.GuiConfig
import eu.wejsonekk.skygen.skygengenerator.config.impl.MessageConfig
import eu.wejsonekk.skygen.skygengenerator.config.impl.PluginConfig
import eu.wejsonekk.skygen.skygengenerator.database.DatabaseConfig
import eu.wejsonekk.skygen.skygengenerator.database.DatabaseManager
import eu.wejsonekk.skygen.skygengenerator.database.StorageType
import eu.wejsonekk.skygen.skygengenerator.database.mongo.MongoManager
import eu.wejsonekk.skygen.skygengenerator.generator.adapter.GeneratorItemAdapter
import eu.wejsonekk.skygen.skygengenerator.generator.argument.GeneratorId
import eu.wejsonekk.skygen.skygengenerator.generator.argument.GeneratorIdArgument
import eu.wejsonekk.skygen.skygengenerator.generator.command.GeneratorCommand
import eu.wejsonekk.skygen.skygengenerator.generator.hologram.GeneratorHologramManager
import eu.wejsonekk.skygen.skygengenerator.generator.manager.GeneratorManager
import eu.wejsonekk.skygen.skygengenerator.generator.repository.GeneratorRepository
import eu.wejsonekk.skygen.skygengenerator.generator.repository.impl.MariaDbGeneratorRepository
import eu.wejsonekk.skygen.skygengenerator.generator.repository.impl.MongoGeneratorRepository
import eu.wejsonekk.skygen.skygengenerator.generator.service.GeneratorService
import eu.wejsonekk.skygen.skygengenerator.genboost.adapter.GenBoostItemAdapter
import eu.wejsonekk.skygen.skygengenerator.genboost.api.GenBoostApiImpl
import eu.wejsonekk.skygen.skygengenerator.genboost.bossbar.GenBoostBossBarService
import eu.wejsonekk.skygen.skygengenerator.genboost.command.GenBoostCommand
import eu.wejsonekk.skygen.skygengenerator.genboost.listener.GenBoostItemListener
import eu.wejsonekk.skygen.skygengenerator.genboost.listener.GenBoostJoinListener
import eu.wejsonekk.skygen.skygengenerator.genboost.manager.GenBoostManager
import eu.wejsonekk.skygen.skygengenerator.genboost.service.GenBoostService
import eu.wejsonekk.skygen.skygengenerator.gui.GuiItems
import eu.wejsonekk.skygen.skygengenerator.gui.GuiListener
import eu.wejsonekk.skygen.skygengenerator.litecommand.InvalidUsageHandlerImpl
import eu.wejsonekk.skygen.skygengenerator.litecommand.MissingPermissionHandlerImpl
import eu.wejsonekk.skygen.skygengenerator.notice.NoticeService
import eu.wejsonekk.skygen.skygengenerator.util.Text
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.command.CommandSender
import org.bukkit.plugin.ServicePriority
import org.bukkit.plugin.java.JavaPlugin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class SkyGenGeneratorPlugin : JavaPlugin() {

    companion object {
        lateinit var instance: SkyGenGeneratorPlugin
            private set
    }

    val logger: Logger = LoggerFactory.getLogger(SkyGenGeneratorPlugin::class.java)

    lateinit var loomScheduler: BukkitLoomScheduler
    lateinit var litecommands: LiteCommands<CommandSender>
    lateinit var configService: ConfigService
    lateinit var noticeService: NoticeService
    lateinit var messageConfig: MessageConfig
    lateinit var pluginConfig: PluginConfig
    lateinit var generatorConfig: GeneratorConfig
    lateinit var genBoostConfig: GenBoostConfig
    lateinit var guiConfig: GuiConfig
    lateinit var databaseConfig: DatabaseConfig
    lateinit var miniMessage: MiniMessage
    lateinit var databaseManager: DatabaseManager
    lateinit var mongoManager: MongoManager
    lateinit var databaseExecutor: ExecutorService
    lateinit var generatorService: GeneratorService
    lateinit var generatorManager: GeneratorManager
    lateinit var generatorHologramManager: GeneratorHologramManager
    lateinit var itemAdapter: GeneratorItemAdapter
    lateinit var genBoostBossBarService: GenBoostBossBarService
    lateinit var genBoostItemAdapter: GenBoostItemAdapter
    lateinit var genBoostManager: GenBoostManager
    lateinit var genBoostService: GenBoostService

    override fun onEnable() {
        val started = System.currentTimeMillis()
        instance = this

        this.loomScheduler = BukkitLoomScheduler.create(this)
        this.miniMessage = MiniMessage.miniMessage()
        this.databaseExecutor = Executors.newFixedThreadPool(4) { runnable ->
            Thread(runnable, "wSkyGen-Generators-DB").apply { isDaemon = true }
        }

        this.loadConfigurations()
        this.setupStorage()
        this.setupSystems()
        this.registerListeners()
        this.registerApi()
        this.registerCommands()

        this.logger.info("Enabled Plugin in {}ms", System.currentTimeMillis() - started)
    }

    override fun onDisable() {
        this.logger.info("Disabling Plugin...")

        if (this::generatorManager.isInitialized) {
            this.generatorManager.stop()
            if (this.generatorConfig.saveOnShutdown) {
                try {
                    this.generatorManager.saveAll().join()
                } catch (exception: Exception) {
                    this.logger.warn("Failed to save generators: {}", exception.message)
                }
            }
        }

        if (this::generatorHologramManager.isInitialized) {
            this.generatorHologramManager.stop()
        }

        if (this::genBoostManager.isInitialized) {
            this.genBoostManager.stop()
        }

        if (this::configService.isInitialized) {
            try {
                this.configService.saveAll()
            } catch (exception: Exception) {
                this.logger.warn("Failed to save configuration: {}", exception.message)
            }
        }

        if (this::litecommands.isInitialized) {
            this.litecommands.commandManager.unregisterAll()
        }

        if (this::databaseManager.isInitialized) {
            this.databaseManager.disconnect()
        }

        if (this::mongoManager.isInitialized) {
            this.mongoManager.disconnect()
        }

        if (this::databaseExecutor.isInitialized) {
            this.databaseExecutor.shutdown()
        }

        if (this::loomScheduler.isInitialized) {
            this.loomScheduler.shutdown(Duration.ofSeconds(5))
        }

        this.logger.info("Disabled Plugin")
    }

    private fun loadConfigurations() {
        this.configService = ConfigService()
        this.pluginConfig = this.configService.create(this.dataFolder.resolve("config.yml"))
        this.messageConfig = this.configService.create(this.dataFolder.resolve("messages.yml"))
        this.generatorConfig = this.configService.create(this.dataFolder.resolve("generators.yml"))
        this.genBoostConfig = this.configService.create(this.dataFolder.resolve("genboost.yml"))
        this.guiConfig = this.configService.create(this.dataFolder.resolve("gui.yml"))
        this.databaseConfig = this.configService.create(this.dataFolder.resolve("database.yml"))

        this.noticeService = NoticeService(this.messageConfig, this.miniMessage)
        GuiItems.init(this.guiConfig)
        Text.init(this.miniMessage)
    }

    private fun setupStorage() {
        this.databaseManager = DatabaseManager(this.databaseConfig.sql, this.logger, this.dataFolder)
        this.mongoManager = MongoManager(this.databaseConfig.mongo, this.logger)

        this.itemAdapter = GeneratorItemAdapter()
    }

    private fun createRepository(): GeneratorRepository =
        when (this.databaseConfig.storageType) {
            StorageType.SQL -> {
                this.databaseManager.connect()
                MariaDbGeneratorRepository(this.databaseManager, this.databaseExecutor)
            }

            StorageType.MONGO -> {
                this.mongoManager.connect()
                MongoGeneratorRepository(this.mongoManager, this.databaseExecutor)
            }
        }

    private fun setupSystems() {
        val repository = this.createRepository()
        this.generatorService = GeneratorService(repository)

        this.genBoostBossBarService = GenBoostBossBarService()
        this.genBoostItemAdapter = GenBoostItemAdapter(this, this.genBoostConfig)
        this.genBoostManager = GenBoostManager(
            this,
            this.generatorService,
            this.genBoostConfig,
            this.genBoostBossBarService,
            this.noticeService,
            this.logger,
        )
        this.genBoostService = GenBoostService(
            this.genBoostManager,
            this.genBoostItemAdapter,
            this.noticeService,
        )

        this.generatorManager = GeneratorManager(
            this,
            this.generatorService,
            this.genBoostManager,
            this.generatorConfig,
            this.itemAdapter,
            this.logger,
        )

        this.generatorHologramManager = GeneratorHologramManager(
            this,
            this.generatorService,
            this.generatorConfig,
            this.logger,
        )

        this.generatorService.load().join()
        this.generatorManager.start()
        this.generatorHologramManager.start()
        this.genBoostManager.start()
    }

    private fun registerListeners() {
        this.server.pluginManager.registerEvents(GuiListener(), this)
        this.server.pluginManager.registerEvents(GenBoostItemListener(this.genBoostService, this.genBoostConfig), this)
        this.server.pluginManager.registerEvents(GenBoostJoinListener(this.genBoostManager), this)
    }

    private fun registerApi() {
        this.server.servicesManager.register(
            SkyGenGeneratorsApi::class.java,
            this.generatorService,
            this,
            ServicePriority.Normal,
        )

        this.server.servicesManager.register(
            SkyGenBoostApi::class.java,
            GenBoostApiImpl(this, this.genBoostManager, this.genBoostService, this.generatorService),
            this,
            ServicePriority.Normal,
        )
    }

    private fun registerCommands() {
        this.litecommands = LiteBukkitFactory.builder("wSkyGen-Generators", this)
            .argument(GeneratorId::class.java, GeneratorIdArgument(this.generatorService))
            .commands(
                GeneratorCommand(
                    this,
                    this.generatorService,
                    this.generatorManager,
                    this.generatorHologramManager,
                    this.genBoostManager,
                    this.noticeService,
                    this.configService,
                    this.generatorConfig,
                ),
                GenBoostCommand(
                    this.genBoostService,
                    this.noticeService,
                    this.configService,
                ),
            )
            .invalidUsage(InvalidUsageHandlerImpl(this.noticeService))
            .missingPermission(MissingPermissionHandlerImpl(this.noticeService))
            .build()
    }
}
