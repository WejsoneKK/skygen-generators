package eu.wejsonekk.skygen.skygengenerator.generator.command

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.argument.CaseInsensitive
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import com.cryptomorin.xseries.XMaterial
import dev.rollczi.litecommands.annotations.execute.ExecuteDefault
import dev.rollczi.litecommands.strict.StrictMode
import eu.wejsonekk.skygen.skygengenerator.config.ConfigService
import eu.wejsonekk.skygen.skygengenerator.config.impl.GeneratorConfig
import eu.wejsonekk.skygen.skygengenerator.generator.argument.GeneratorId
import eu.wejsonekk.skygen.skygengenerator.generator.gui.GeneratorEditGui
import eu.wejsonekk.skygen.skygengenerator.generator.gui.GeneratorListGui
import eu.wejsonekk.skygen.skygengenerator.generator.hologram.GeneratorHologramManager
import eu.wejsonekk.skygen.skygengenerator.generator.manager.GeneratorManager
import eu.wejsonekk.skygen.skygengenerator.generator.service.GeneratorService
import eu.wejsonekk.skygen.skygengenerator.genboost.manager.GenBoostManager
import eu.wejsonekk.skygen.skygengenerator.notice.NoticeService
import eu.wejsonekk.skygen.skygengenerator.util.DurationFormat
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import java.time.Duration

@Command(name = "wskygenerator", aliases = ["wsg","skygen", "generator", "wgen" ,"wgenerator", "wgens","sg", "wskygen"])
@Permission("wskygenerator.command.admin")
class GeneratorCommand(
    private val plugin: Plugin,
    private val service: GeneratorService,
    private val manager: GeneratorManager,
    private val hologramManager: GeneratorHologramManager,
    private val genBoostManager: GenBoostManager,
    private val noticeService: NoticeService,
    private val configService: ConfigService,
    private val generatorConfig: GeneratorConfig,
) {

    @Execute(name = "help", aliases = ["?", "pomoc"])
    fun help(@Context sender: CommandSender) {
        this.noticeService.create()
            .viewer(sender)
            .notice { it.generatorHelpCommand }
            .send()
    }
    @Execute(name = "create")
    @Permission("wskygenerator.command.create")
    fun create(
        @Context player: Player,
        @Arg("id") id: String,
        @Arg("material") @CaseInsensitive material: XMaterial,
        @Arg("amount") amount: Int,
        @Arg("time") time: Duration,
    ) {
        if (!material.isSupported() || material.parseItem() == null) {
            this.noticeService.create()
                .viewer(player)
                .notice { it.generatorMaterialInvalid }
                .placeholder("{material}", material.name)
                .send()
            return
        }

        this.service.createGenerator(id, material, amount, time, player.uniqueId, player.location)
            .thenAccept { generator -> this.runMain {
                this.noticeService.create()
                    .viewer(player)
                    .notice { it.generatorCreated }
                    .placeholder("{id}", generator.id)
                    .placeholder("{material}", generator.material.name)
                    .placeholder("{amount}", generator.amount.toString())
                    .placeholder("{time}", DurationFormat.format(generator.interval))
                    .send()
            } }
            .exceptionally { throwable -> this.runMain {
                this.noticeService.create()
                    .viewer(player)
                    .notice { it.generatorCreateFailed }
                    .placeholder("{error}", throwable.cause?.message ?: throwable.message ?: "unknown")
                    .send()
            }; null }
    }

    @Execute(name = "remove")
    @Permission("wskygenerator.command.remove")
    fun remove(@Context sender: CommandSender, @Arg("id") id: GeneratorId) {
        this.service.removeGenerator(id.value).thenAccept { removed -> this.runMain {
            val notice = if (removed) {
                this.noticeService.create().viewer(sender).notice { it.generatorRemoved }.placeholder("{id}", id.value)
            } else {
                this.noticeService.create().viewer(sender).notice { it.generatorNotFound }.placeholder("{id}", id.value)
            }
            notice.send()
        } }
    }

    @Execute(name = "edit")
    @Permission("wskygenerator.command.edit")
    fun edit(@Context player: Player, @Arg("id") id: GeneratorId) {
        this.service.generator(id.value).thenAccept { generator -> this.runMain {
            if (generator == null) {
                this.noticeService.create()
                    .viewer(player)
                    .notice { it.generatorNotFound }
                    .placeholder("{id}", id.value)
                    .send()
                return@runMain
            }
            GeneratorEditGui(this.service, generator, this.generatorConfig, this.noticeService).open(player)
        } }
    }

    @Execute(name = "tp")
    @Permission("wskygenerator.command.tp")
    fun teleport(@Context player: Player, @Arg("id") id: GeneratorId) {
        this.service.generator(id.value).thenAccept { generator -> this.runMain {
            val location = generator?.location?.toBukkitLocation()
            if (generator == null || location == null) {
                this.noticeService.create()
                    .viewer(player)
                    .notice { it.generatorNotFound }
                    .placeholder("{id}", id.value)
                    .send()
                return@runMain
            }

            player.teleportAsync(location.clone().add(0.5, 1.0, 0.5)).thenAccept { success ->
                if (success) {
                    this.runMain {
                        this.noticeService.create()
                            .viewer(player)
                            .notice { it.generatorTeleported }
                            .placeholder("{id}", generator.id)
                            .send()
                    }
                }
            }
        } }
    }

    @Execute(name = "list")
    @Permission("wskygenerator.command.list")
    fun list(@Context player: Player) {
        this.service.generators().thenAccept { generators -> this.runMain {
            if (generators.isEmpty()) {
                this.noticeService.create().viewer(player).notice { it.generatorListEmpty }.send()
                return@runMain
            }
            GeneratorListGui(this.service, generators.toList(), this.generatorConfig, this.noticeService).open(player)
        } }
    }

    @Execute(name = "reload")
    @Permission("wskygenerator.command.reload")
    fun reload(@Context sender: CommandSender) {
        this.configService.reload()
        this.manager.start()
        this.hologramManager.start()
        this.genBoostManager.start()
        this.noticeService.create().viewer(sender).notice { it.reloadSuccess }.send()
    }

    private fun runMain(block: () -> Unit) {
        Bukkit.getScheduler().runTask(this.plugin, Runnable(block))
    }
}
