package eu.wejsonekk.skygen.skygengenerator.litecommand

import dev.rollczi.litecommands.handler.result.ResultHandlerChain
import dev.rollczi.litecommands.invocation.Invocation
import dev.rollczi.litecommands.permission.MissingPermissions
import dev.rollczi.litecommands.permission.MissingPermissionsHandler
import eu.wejsonekk.skygen.skygengenerator.notice.NoticeService
import org.bukkit.command.CommandSender

class MissingPermissionHandlerImpl(
    private val noticeService: NoticeService
) : MissingPermissionsHandler<CommandSender> {
    override fun handle(
        invocation: Invocation<CommandSender?>?,
        missingPermissions: MissingPermissions?,
        chain: ResultHandlerChain<CommandSender?>?
    ) {

        val sender = invocation?.sender() ?: return

        this.noticeService.create()
            .viewer(sender)
            .notice { msg -> msg.missingPermission }
            .placeholder("{permission}", missingPermissions!!.asJoinedText())
            .placeholder("{PERMISSION}", missingPermissions.asJoinedText())
            .placeholder("{permissions}", missingPermissions.asJoinedText(", "))
            .placeholder("{PERMISSIONS}", missingPermissions.asJoinedText(", "))
            .send()

    }
}