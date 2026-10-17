# wSkyGenGenerators

Lightweight SkyGen generator plugin for Paper. It lets you create sky generators
that periodically drop configurable items, with optional holograms, a GenBoost
system and a fully configurable GUI.

> **DISCLAIMER** - This plugin is not finished yet.

## Features
- Sky generators with configurable material, amount, interval and activation radius
- Optional requirement of a nearby player for a generator to run
- Holograms: native TextDisplay by default, FancyHolograms when installed
- GenBoost system: global and personal boosts, lottery, bossbars, physical items
- Fully customizable GUI (`gui.yml`) - every item, slot and text is configurable
- PlaceholderAPI support everywhere (GUI, items, holograms, bossbars, messages)
- Storage backends: flat files, SQL (MariaDB/PostgreSQL) and MongoDB
- Public API and custom events for other plugins

## Requirements
- Paper 1.21.8 (base version, tested)
- Java 23+

### Optional dependencies
- [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI)
- [FancyHolograms](https://github.com/FancyInnovations/FancyHolograms)
- Vault
- WorldGuard

## Commands

Base command: `/wskygenerator` (aliases: `/skygen`, `/sg`)

| Command                                             | Description                            | Permission              |
| --------------------------------------------------- | -------------------------------------- | ----------------------- |
| `/generator`                                        | Shows the command list                 | -                       |
| `/generator create <id> <material> <amount> <time>` | Creates a generator at your location   | `skygen.command.create` |
| `/generator remove <id>`                            | Deletes a generator                    | `skygen.command.remove` |
| `/generator edit <id>`                              | Opens the generator editor GUI         | `skygen.command.edit`   |
| `/generator tp <id>`                                | Teleports you to a generator           | `skygen.command.tp`     |
| `/generator list`                                   | Opens a paginated list of generators   | `skygen.command.list`   |
| `/generator reload`                                 | Reloads the plugin configuration       | `skygen.command.reload` |

Base command: `/genboost` (aliases: `/gb`, `/genboosts`)

| Command                                       | Description                                 | Permission                         |
| --------------------------------------------- | ------------------------------------------- | ---------------------------------- |
| `/genboost`                                   | Shows the command list                      | -                                  |
| `/genboost give <player> <multiplier> <time>` | Gives a personal boost item to a player     | `skygen.command.genboost.give`     |
| `/genboost give-all <multiplier> <time>`      | Activates a global boost for everyone       | `skygen.command.genboost.giveall`  |
| `/genboost set-item <personal\|global>`       | Sets the physical boost item from your hand | `skygen.command.genboost.setitem`  |

## Permissions

| Permission                         | Description                                 |
|------------------------------------| ------------------------------------------- |
| `wskygenerator.command.create`            | Allows creating generators                  |
| `wskygenerator.command.remove`            | Allows removing generators                  |
| `wskygenerator.command.edit`              | Allows opening the generator editor         |
| `wskygenerator.command.tp`                | Allows teleporting to a generator           |
| `wskygenerator.command.list`              | Allows listing generators                   |
| `wskygenerator.command.reload`            | Allows reloading the plugin                 |
| `wskygenerator.command.genboost.give`     | Allows giving personal boosts               |
| `wskygenerator.command.genboost.giveall`  | Allows giving global boosts                 |
| `wskygenerator.command.genboost.setitem` | Allows changing the boost item template     |

## Building
The project uses the Gradle Shadow plugin and targets Java 23.

```
gradle build
```

The ready-to-use plugin jar is generated at:

```
build/libs/wSkyGenGenerators-<version>-all.jar
```

## Contributing
- Fork the repository
- Create a new branch
- Make your changes
- Open a pull request
- Wait for the pull request to be reviewed and merged
- Enjoy!

## AI usage
This project is developed with the help of AI. Parts of the code, the
documentation and the changelog are written or refined by an AI coding agent.

- **Agent:** [OpenCode](https://opencode.ai)
- **Model:** `opencode/deepseek-v4.1-flash-free` (DeepSeek V4.1 Flash)

All AI-generated changes are reviewed before being merged.

## Libraries used
- [LiteCommands](https://github.com/Rollczi/LiteCommands) - Command framework
- [Okaeri-Configs](https://github.com/OkaeriPoland/okaeri-configs) - Configuration library
- [xSeries](https://github.com/CryptoMorin/XSeries) - Cross-version compatibility
- [TriumphGui](https://github.com/TriumphTeam/triumph-gui) - GUI library
- [EternalCodeCommons](https://github.com/EternalCodeTeam/EternalCodeCommons) - Utilities
- [Multification](https://github.com/EternalCodeTeam/Multification) - Messages
- [ORMLite](https://ormlite.com/) - Database ORM
- [FancyHolograms](https://github.com/FancyInnovations/FancyHolograms) - Hologram API
- [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) - Placeholders

## License
See [LICENSE](LICENSE.MD).
