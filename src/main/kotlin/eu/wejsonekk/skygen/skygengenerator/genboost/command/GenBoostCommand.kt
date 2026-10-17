package eu.wejsonekk.skygen.skygengenerator.genboost.command

import dev.rollczi.litecommands.annotations.argument.Arg
import dev.rollczi.litecommands.annotations.argument.CaseInsensitive
import dev.rollczi.litecommands.annotations.command.Command
import dev.rollczi.litecommands.annotations.context.Context
import dev.rollczi.litecommands.annotations.execute.Execute
import dev.rollczi.litecommands.annotations.permission.Permission
import eu.wejsonekk.skygen.skygengenerator.api.genboost.GenBoostType
import eu.wejsonekk.skygen.skygengenerator.config.ConfigService
import eu.wejsonekk.skygen.skygengenerator.genboost.service.GenBoostService
import eu.wejsonekk.skygen.skygengenerator.notice.NoticeService
import eu.wejsonekk.skygen.skygengenerator.util.DurationFormat
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.time.Duration

/**
 * `/genboost` command tree.
 */
@Command(name = "genboost", aliases = ["gb", "genboosts", "wgb", "wgenboosts", "genboosty", "wgenboost"])
@Permission("wskygenerator.command.genboost")
class GenBoostCommand(
    private val service: GenBoostService,
    private val noticeService: NoticeService,
    private val configService: ConfigService,
) {

    @Execute(name = "help", aliases = ["?", "pomoc"])
    fun help(@Context sender: CommandSender) {

        this.noticeService.create()
            .viewer(sender)
            .notice { it.genBoostHelpCommand }
            .send()
    }

    @Execute(name = "give")
    @Permission("wskygenerator.command.genboost.give")
    fun give(
        @Context sender: CommandSender,
        @Arg("player") target: Player,
        @Arg("multiplier") multiplier: Double,
        @Arg("time") time: Duration,
    ) {
        if (!this.valid(sender, multiplier, time)) {
            return
        }

        this.service.giveItem(target, GenBoostType.PERSONAL, multiplier, time, "GenBoost")
    }

    @Execute(name = "give-all")
    @Permission("wskygenerator.command.genboost.giveall")
    fun giveAll(
        @Context sender: CommandSender,
        @Arg("multiplier") multiplier: Double,
        @Arg("time") time: Duration,
    ) {
        if (!this.valid(sender, multiplier, time)) {
            return
        }

        if (this.service.giveAll(multiplier, time, "GenBoost")) {
            this.noticeService.create()
                .viewer(sender)
                .notice { it.genBoostGiveAll }
                .placeholder("{name}", "GenBoost")
                .placeholder("{multiplier}", multiplier.toString())
                .placeholder("{duration}", DurationFormat.format(time))
                .send()
        }
    }

    @Execute(name = "set-item")
    @Permission("wskygenerator.command.genboost.setitem")
    fun setItem(
        @Context player: Player,
        @Arg("type") @CaseInsensitive() type: GenBoostType,
    ) {
        val item = player.inventory.itemInMainHand
        if (item.type.isAir) {
            this.noticeService.create().viewer(player).notice { it.genBoostSetItemNoItem }.send()
            return
        }

        this.service.setTemplate(type, item)
        this.configService.saveAll()

        this.noticeService.create()
            .viewer(player)
            .notice { it.genBoostSetItem }
            .placeholder("{type}", type.name)
            .send()
    }

    private fun valid(sender: CommandSender, multiplier: Double, time: Duration): Boolean {
        if (multiplier < 1.0) {
            this.noticeService.create()
                .viewer(sender)
                .notice { it.genBoostMultiplierInvalid }
                .placeholder("{multiplier}", multiplier.toString())
                .send()
            return false
        }
        if (time.isNegative || time.isZero) {
            this.noticeService.create()
                .viewer(sender)
                .notice { it.generatorDurationInvalid }
                .placeholder("{time}", time.toString())
                .send()
            return false
        }
        return true
    }
}
