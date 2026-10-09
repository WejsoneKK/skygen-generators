package eu.wejsonekk.skygen.skygengenerator.litecommand

import dev.rollczi.litecommands.handler.result.ResultHandlerChain
import dev.rollczi.litecommands.invalidusage.InvalidUsage
import dev.rollczi.litecommands.invalidusage.InvalidUsageHandler
import dev.rollczi.litecommands.invocation.Invocation
import eu.wejsonekk.skygen.skygengenerator.notice.NoticeService
import org.bukkit.command.CommandSender

class InvalidUsageHandlerImpl(
    private val noticeService: NoticeService
) : InvalidUsageHandler<CommandSender> {
    override fun handle(
        invocation: Invocation<CommandSender?>?,
        result: InvalidUsage<CommandSender?>?,
        chain: ResultHandlerChain<CommandSender?>?
    ) {

        val sender = invocation?.sender()
        val schema = result?.schematic

        if(schema!!.isOnlyFirst) {
            this.noticeService.create()
                .viewer(sender)
                .notice { msg -> msg.invalidUsage }
                .placeholder("{usage}", schema.first())
                .placeholder("{USAGE}", schema.first())
                .send()
            return;
        }

        this.noticeService.create()
            .viewer(sender)
            .notice { msg -> msg.invalidUsageHeader }
            .send()

        for(schema in schema.all()) {
            this.noticeService.create()
                .viewer(sender)
                .notice { msg -> msg.invalidUsageEntry }
                .placeholder("{usage}", schema)
                .placeholder("{entry}", schema)
                .placeholder("{USAGE}", schema)
                .placeholder("{ENTRY}", schema)
                .send()
        }
    }
}