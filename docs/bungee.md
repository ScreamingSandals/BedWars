# BungeeCord mode

Screaming BedWars supports a BungeeCord mode, which allows for single-arena servers with arena autojoin. This document provides instructions on how to configure this mode.

!!! note "Velocity Support"

    This mode is also compatible with Velocity. Ensure `bungee-plugin-message-channel` is enabled in the `velocity.toml` file on your proxy.

## Configuring Game Servers

To enable BungeeCord mode, locate the `bungee` section in `config.yml` and set `enabled` to `true`. Then, configure each option according to your requirements:

* `serverRestart` - Determines whether the server should restart after the game ends. This is not mandatory for the plugin; adjust this setting as needed. To enable server restarts, an existing startup script (`.sh` or `.bat`) must be defined in `spigot.yml`.
* `serverStop` - Similar to `serverRestart`, but only shuts down the server. Use this only if you have separate software managing automatic server startup.
* `server` - Specifies the hub server to which players are moved after the game ends.
* `auto-game-connect` - Determines whether users should automatically join the BedWars game upon server join. This should typically be enabled unless managed by an addon.
* `kick-when-proxy-too-slow` - If the proxy fails to move the player to the hub server or is too slow, the server will kick players who are waiting for too long.
* `random-game-selection` - Enables random selection of arenas if multiple arenas are on the same server. Set `enabled` to `true` to use this feature. The `preselect-games` option is useful for knowing the next selected game before any player joins (at server startup or after the previous game ends).
* `motd` - Useful for server selection plugins utilizing MOTD text, such as [BungeeSigns](https://www.spigotmc.org/resources/bungeesigns.6563/). If enabled, the plugin will update the MOTD based on the current state and player count. There are five states: `waiting`, `waiting_full`, `running`, `rebuilding`, and `disabled`. Use the placeholders `%name%` for arena name, `%current%` for current player count, and `%max%` for maximum player count. Messages can be colored using legacy color codes (`§<color code>`).

The configuration section might look like this:

```yaml
bungee:
  enabled: false
  serverRestart: true
  serverStop: false
  server: hub
  auto-game-connect: false
  kick-when-proxy-too-slow: true
  random-game-selection:
    enabled: true
    preselect-games: false
  motd:
    enabled: false
    waiting: '%name%: Waiting for players [%current%/%max%]'
    waiting_full: '%name%: Game is full [%current%/%max%]'
    running: '%name%: Game is running [%current%/%max%]'
    rebuilding: '%name%: Rebuilding...'
    disabled: '%name%: Game is disabled'
```

## Configuring Hub Servers

While there is no lobby plugin for Screaming BedWars, any server selection plugin can be used, such as [BungeeSigns](https://www.spigotmc.org/resources/bungeesigns.6563/). Plugins that can read MOTD are preferred as they can relay the game state to your hub server.

<!-- TODO: list possible options with little tutorials -->

!!! tip "Synchronizing Statistics"

    For BungeeCord networks, it is common to synchronize statistics to a database. Configure the `database` section as described [here](config.md#database-connection). Ensure all servers are connected to the same database.

    To access statistics in the lobby, the simplest option is to install Screaming BedWars on your hub server as well. Ensure this instance is NOT in BungeeCord mode. Alternatively, you can create an addon to retrieve statistics. The database structure is detailed at the end of the [database configuration section](config.md#database-connection).
