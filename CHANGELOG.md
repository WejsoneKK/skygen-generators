# Changelog

Wszystkie istotne zmiany w projekcie. Format oparty o
[Keep a Changelog](https://keepachangelog.com/pl/1.1.0/),
wersjonowanie zgodne z [Semantic Versioning](https://semver.org/lang/pl/).

## [0.2.0-alpha.1] - 2026-10-17

### Added
- **Generator** – pełny system generatorów: domena (`Generator`, `GeneratorImpl`), repozytoria (SQL/MariaDB + MongoDB), `GeneratorService` i `GeneratorManager` generujący przedmioty na ziemi co interwał, tylko gdy gracz jest w pobliżu (promień aktywacji).
- **GenBoost** – system boostów globalnych i personalnych: fizyczne itemy do kliknięcia (PDC), komendy `give`/`give-all`, bossbary, automatyczne losowanie z wagami presetów oraz ogłoszenia.
- **GUI** – własny framework GUI (`InventoryHolder`) z paginacją, historią „wstecz”, layoutami (tło + obramowanie) oraz menu listy i edycji generatorów.
- **Hologramy** – hologram nad generatorem pokazujący ID/materiał i odliczanie do kolejnej generacji; wbudowany provider `TextDisplay` (domyślny) oraz opcjonalna integracja z FancyHolograms (`AUTO`/`TEXT_DISPLAY`/`FANCY_HOLOGRAMS`).
- **API** – publiczne interfejsy `SkyGenGeneratorsApi` i `SkyGenBoostApi` (rejestrowane w `ServicesManager`) oraz bogaty zestaw eventów: tworzenie/edycja/usuwanie/włączanie/wyłączanie/generowanie generatorów oraz aktywacja/wygaszenie/zakończenie/losowanie/otrzymanie boosta.
- **Komendy** – `/generator` (`create`, `remove`, `edit`, `tp`, `list`, `reload`) i `/genboost` (`give`, `give-all`, `set-item`).
- **Storage** – warstwa baz danych SQL (HikariCP + ORM Lite) oraz MongoDB, konfigurowalna przez `database.yml`.

### Changed
- Migracja materiałów na `XMaterial` (XSeries) – kompatybilność z wieloma wersjami serwera.
- Serializacja itemów przez `XItemStack` (niedeprecated API) i własny codec YAML.
- Item drop pojawia się na ziemi w miejscu utworzenia generatora.

### Fixed
- Poprawne sloty zawartości w paginowanym GUI (obszar wewnętrzny, bez nachodzenia na obramowanie).
- Usunięto kursywę z nazw i opisów (`lore`) przedmiotów GUI.
- Poprawione `setProperty` profilu głowy dla Paper 1.21.8.

## [0.1.0-alpha.4] - 2026-10-16

### Added
- Początkowa konfiguracja projektu (Paper, Kotlin, Gradle, LiteCommands, Okaeri, Multification).
