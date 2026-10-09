package eu.wejsonekk.skygen.skygengenerator.config.impl

import com.eternalcode.multification.notice.Notice
import eu.okaeri.configs.OkaeriConfig

class MessageConfig : OkaeriConfig() {

    var missingPermission: Notice? = Notice.chat(
        "<color:#bd0000>☹ <color:#ff1a21>Brak wymaganych uprawnień do tego polecenia <dark_gray>[<color:#bd0000>{permission}</color><dark_gray>]"
    )

    var invalidUsage: Notice? = Notice.chat(
        "<dark_gray>· <gold>Prawidłowe użycie: <white>{USAGE}"
    )

    var invalidUsageHeader: Notice? = Notice.chat(
        "<gold>Prawidłowe użycie:"
    )

    var invalidUsageEntry: Notice? = Notice.chat(
        "<dark_gray>·</dark_gray> <white>{USAGE}"
    )

    var commandCooldown: Notice? = Notice.chat(
        "<color:#bd0000>Błąd: <color:#ff1a21>Musisz odczekać <color:#bd0000>{time} <color:#ff1a21>przed ponownym użyciem tej komendy!"
    )

}