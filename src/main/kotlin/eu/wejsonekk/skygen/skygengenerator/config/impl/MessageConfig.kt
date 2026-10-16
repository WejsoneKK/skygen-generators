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

    var playerOnly: Notice? = Notice.chat(
        "<color:#bd0000>Błąd: <color:#ff1a21>Ta komenda jest dostępna wyłącznie dla graczy."
    )

    var reloadSuccess: Notice? = Notice.chat(
        "<green>Przeładowano konfigurację pluginu."
    )

    // --- generators ---

    var generatorCreated: Notice? = Notice.chat(
        "<green>Utworzono generator <white>{id}<green> (<white>{material} x{amount}<green>, co <white>{time}<green>)."
    )

    var generatorCreateFailed: Notice? = Notice.chat(
        "<color:#bd0000>Nie udało się utworzyć generatora: <color:#ff1a21>{error}"
    )

    var generatorAlreadyExists: Notice? = Notice.chat(
        "<color:#bd0000>Generator o ID <white>{id}<color:#bd0000> już istnieje."
    )

    var generatorRemoved: Notice? = Notice.chat(
        "<green>Usunięto generator <white>{id}<green>."
    )

    var generatorNotFound: Notice? = Notice.chat(
        "<color:#bd0000>Nie znaleziono generatora o ID <white>{id}<color:#bd0000>."
    )

    var generatorListEmpty: Notice? = Notice.chat(
        "<color:#ff1a21>Brak generatorów do wyświetlenia."
    )

    var generatorTeleported: Notice? = Notice.chat(
        "<green>Przeteleportowano do generatora <white>{id}<green>."
    )

    var generatorInfo: Notice? = Notice.chat(
        "<gold>Generator <white>{id}<gold>\n" +
            "<gray>Item: <white>{material} <gray>x<white>{amount}\n" +
            "<gray>Interwał: <white>{time}\n" +
            "<gray>Lokalizacja: <white>{world} {x},{y},{z}\n" +
            "<gray>Właściciel: <white>{owner}\n" +
            "<gray>Włączony: <white>{enabled}"
    )

    var generatorMaterialInvalid: Notice? = Notice.chat(
        "<color:#bd0000>Nieprawidłowy materiał: <white>{material}<color:#bd0000>."
    )

    var generatorDurationInvalid: Notice? = Notice.chat(
        "<color:#bd0000>Nieprawidłowy czas: <white>{time}<color:#bd0000>."
    )

    // --- gen boosts ---

    var genBoostItemReceived: Notice? = Notice.chat(
        "<green>Otrzymałeś <white>{type}<green> GenBoost <white>{name}<green> <gray>(<white>x{multiplier}<gray>, <white>{duration}<gray>). <yellow>Kliknij prawym, aby aktywować."
    )

    var genBoostActivated: Notice? = Notice.chat(
        "<green>Aktywowano GenBoost <white>{name}<green> <gray>(<white>x{multiplier}<gray>, <white>{duration}<gray>)."
    )

    var genBoostExpired: Notice? = Notice.chat(
        "<red>GenBoost <white>{name}<red> wygasł."
    )

    var genBoostLotteryDraw: Notice? = Notice.chat(
        "<gradient:#ffb300:#ff6f00>Losowanie GenBoost</gradient> <dark_gray>»</dark_gray> <white>{name} <gray>x{multiplier} <dark_gray>|</dark_gray> <yellow>{duration}"
    )

    var genBoostGiveAll: Notice? = Notice.chat(
        "<green>Aktywowano globalny GenBoost <white>{name}<green> <gray>(<white>x{multiplier}<gray>, <white>{duration}<gray>)."
    )

    var genBoostSetItem: Notice? = Notice.chat(
        "<green>Ustawiono item reprezentujący <white>{type}<green> GenBoost."
    )

    var genBoostSetItemNoItem: Notice? = Notice.chat(
        "<color:#bd0000>Musisz trzymać item w dłoni."
    )

    var genBoostMultiplierInvalid: Notice? = Notice.chat(
        "<color:#bd0000>Mnożnik musi być większy lub równy <white>1.0<color:#bd0000>."
    )
}
