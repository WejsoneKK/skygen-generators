package eu.wejsonekk.skygen.skygengenerator.notice

import com.eternalcode.multification.adventure.AudienceConverter
import com.eternalcode.multification.paper.PaperMultification
import com.eternalcode.multification.translation.TranslationProvider
import eu.wejsonekk.skygen.skygengenerator.config.impl.MessageConfig
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.ComponentSerializer
import org.bukkit.command.CommandSender

class NoticeService(
    private val config: MessageConfig,
    private val miniMessage: MiniMessage
) : PaperMultification<MessageConfig>() {
    override fun translationProvider(): TranslationProvider<MessageConfig> =
        TranslationProvider { config }

    override fun audienceConverter(): AudienceConverter<CommandSender?> =
        AudienceConverter { it?.let { it as CommandSender } }

    override fun serializer(): ComponentSerializer<Component?, Component?, String?> =
        miniMessage
}
