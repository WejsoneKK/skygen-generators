# SkyGen SkyGenerators

#### LITTLE DESCRIPTION
SkyGen SkyGenerators is a plugin that allows you to create sky generators.

Generators periodically drop configurable items on the ground, can be toggled
on/off, require a nearby player within a configurable radius, and support
holograms (native TextDisplay or FancyHolograms). A full GenBoost system
(global and personal boosts, lottery, physical click-to-activate items and
bossbars) is included, together with a fully configurable inventory GUI and a
public API + event system for other plugins.

### FEATURES
- Sky generators with per-generator material, amount, interval and activation radius
- Optional requirement of a nearby player to run a generator
- Holograms: native TextDisplay by default, FancyHolograms when available
- GenBoost system: global and personal boosts, lottery, bossbars, physical items
- Fully customizable GUI (`gui.yml`) - every item, slot and text is configurable
- PlaceholderAPI support everywhere (GUI, items, holograms, bossbars, messages)
- Storage backends: flat files, SQL (MariaDB) and MongoDB
- Public API and custom events

### DEPENDENCIES
- PlaceholderAPI (optional)
- Brain (required)
- Vault (optional)
- WorldGuard (optional)
- FancyHolograms (optional)

### SUPPORT VERSION
- 1.21.8 (base version) (tested)
- 1.21.11 (not tested)
- 26.1 (not tested)
- 26.2 (not tested)
- 26.3 (not tested)


!!DISCLAIMER - THIS PLUGIN IS NOT FINISHED YET!!

### COMMANDS
/wskygen help - Shows all commands
/wskygen create [name] [material] [amount] [time] - Creates a sky generator
/wskygen delete [id] - Deletes a sky generator
/wskygen list - Lists all sky generators
/wskygen info [id] - Shows sky generator info
/wskygen edit [id] - Opens the generator editor GUI
/wskygen tp [id] - Teleports to a sky generator
/wskygen reload - Reloads the plugin
/wskygen genboost - Boosts sky generator generation
/wskygen genboost [type PERSONAL/GLOBAL] [tier] [time] - Boosts sky generator generation

### CONTRIBUTING
- Fork the repository
- Create a new branch
- Make your changes
- Create a pull request
- Wait for the pull request to be accepted
- Enjoy!

### AI USAGE
This project is developed with the assistance of AI. Parts of the code, the
documentation and the changelog are written or refined by an AI coding agent.

- **Agent:** [OpenCode](https://opencode.ai)
- **Model:** `opencode/deepseek-v4.1-flash-free` (DeepSeek V4.1 Flash)

All AI-generated changes are reviewed before being merged.

### LICENSE
- Let's go to [LICENSE](LICENSE.MD)


### LIBRARIES USED
- [LiteCommands](https://github.com/Rollczi/LiteCommands) - Amazing framework for commands
- [Okaeri-Configs](https://github.com/OkaeriPoland/okaeri-configs) - Library for configuration
- [xSeries](https://github.com/CryptoMorin/XSeries) - Amazing library for support multiple versions
- [TriumphGui](https://github.com/TriumphTeam/triumph-gui) - Library for Gui
- [EternalCodeCommons](https://github.com/EternalCodeTeam/EternalCodeCommons) - Amazing Library
- [FancyHolograms](https://github.com/FancyInnovations/FancyHolograms) - Hologram API
- [PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) - Placeholder support
