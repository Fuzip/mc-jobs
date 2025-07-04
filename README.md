# Jobs Plugin

The **Jobs Plugin** is a Bukkit/Spigot plugin for Minecraft servers that introduces player job functionality, allowing players to engage in specific jobs and earn rewards based on in-game actions.

## Features

- **Configurable Jobs**: Create custom jobs with specific tasks and rewards.
- **Event Integration**: Listeners for events such as block breaking and player join.
- **Command System**: Provides a `/jobs` command to interact with the plugin's functionality.
- **Data Management**: Manages player job data and configuration files (`config.yml`, `data.yml`).

## How It Works

The plugin runs as part of a Minecraft server and integrates with the Bukkit/Spigot API. Upon loading, it:

1. Initializes default configurations and resources.
2. Sets up two main managers:
   - **JobManager**: Handles the logic for jobs and their tasks.
   - **PlayerDataManager**: Manages player-specific job data.
3. Registers event listeners for tasks associated with jobs (e.g., breaking blocks).
4. Configures the `/jobs` command for players to interact with the system.

## Installation

1. Download the plugin's `.jar` file.
2. Place the `.jar` file into your server's `plugins` folder.
3. Start or restart your server.
4. The plugin will generate the default configuration files (`config.yml` and `data.yml`).
5. Customize your job settings in the `config.yml` file.

## Commands

### `/jobs`
This command serves as the main interaction for players. Examples of usage:

- `/jobs`: Show your jobs.
- `/jobs <username>`: Show the user jobs.

## Listeners

The plugin comes with event listeners that handle:

- **Block Breaking**: Tracks blocks broken by players and awards job-specific progress or rewards.
- **Player Join**: Loads player data when they join the server.

## Configuration

The default `config.yml` allows you to set up jobs and their respective tasks and rewards.

Example:

```yaml
jobs:
  miner:
    name: "Mineur"
    color: BLUE
    xp:
      break:
        coal_ore: 5
        copper_ore: 5
        iron_ore: 10
        gold_ore: 15
        lapis_ore: 20
        redstone_ore: 20
        emerald_ore: 30
        diamond_ore: 40
        nether_quartz_ore: 15
        nether_gold: 20
        ancient_debris: 50
    rewards:
      every_10_levels:
        item:
          - name: DIAMOND
            quantity: 1
      every_50_levels:
        item:
          - name: NETHERITE_INGOT
            quantity: 1

```

## Development

If you are a developer wanting to extend or modify this plugin:

- The main entry point is the `Jobs` class located in the `dev.fuzip.jobs` package.
- The plugin follows a modular design, with separate managers for jobs and player data.

## Requirements

- **Minecraft Server**: Built for Bukkit/Spigot API.
- **Java**: Requires Java 17+.
- Suitable for modern Minecraft server versions.

## Contributing

Feel free to create a pull request or submit an issue for feature requests or bugs. Contributions are welcome!

## License

This project follows the **MIT License**. You are free to modify and distribute this plugin while giving credit to the original authors.
