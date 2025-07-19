# Screaming BedWars
[![Build and publish BedWars master](https://github.com/ScreamingSandals/BedWars/actions/workflows/build-master.yml/badge.svg)](https://github.com/ScreamingSandals/BedWars/actions/workflows/build-master.yml)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/2b72901b108f4577a135faee054d0d6d)](https://www.codacy.com/gh/ScreamingSandals/BedWars/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ScreamingSandals/BedWars&amp;utm_campaign=Badge_Grade)
[![Translation status](http://weblate.screamingsandals.org/widgets/bedwars/-/0-3-x/svg-badge.svg)](https://github.com/ScreamingSandals/BedWarsLanguage/tree/0.3.x)
[![Discord](https://img.shields.io/discord/582271436845219842?logo=discord)](https://discord.gg/4xB54Ts)
[![Spigot](https://img.shields.io/spiget/downloads/63714)](https://www.spigotmc.org/resources/screaming-bedwars-1-9-4-1-17-1.63714/)

**If you are using version 0.2.x (which you probably are), you are probably looking for the [ver/0.2.x](https://github.com/ScreamingSandals/BedWars/tree/ver/0.2.x) branch instead. This version of readme does NOT apply to 0.2.x!**

A highly flexible BedWars plugin with wide Minecraft version support, originally inspired by BedwarsRel.

Supported versions: \[1.8.8 - 1.21.8\]. Recommended version: \[1.21.8\]

## Support
If you need any help, you can contact us on [Discord](https://discord.gg/4xB54Ts). Please make sure to look into older messages. There are many question already answered. It is really anoying to repeat the same thing over and over.

Make sure to check out our [Docs](https://docs.screamingsandals.org) before contacting us on Discord. It will save our time, you know. O:)

If you have found any bug, feel free to report it into [Issues](https://github.com/ScreamingSandals/BedWars/issues), we will look into it.

## Features
-   All the basics of BedWars game (Beds, Teams and so on)
-   Other BedWars variants: CakeWars/EggWars/AnchorWars
-   Shop that supports multi-shop per arena!
-   BungeeCord
-   Vault rewards
-   Spectator mode (now, spectators can join running game!)
-   Arena rebuilding (Incredibly quick!)
-   BossBar or XP bar in lobby countdown or with game time
-   Breakable (those are refreshing after arena rebuild) / ignored blocks
-   SpecialItems (RescuePlatform, TNTSheep and so on) - they can be configured in shop too!
-   and many other features

### Customizable
-   Team selecting GUI
-   Auto coloring items (like armor and so)
-   Resource Spawners (you can have as much as you want)
-   Player statistics
-   In fact everything is customizable
-   And many more useful features.

## Compiling

This project uses **Gradle** and requires **JDK 11** or newer. To build it, clone the repository and run:

```bash
./gradlew clean build
```

On Windows, use:

```bat
gradlew.bat clean build
```

The compiled JAR file will be located in the `plugin/{platform}/build/libs` directory (e.g. `plugin/bukkit/build/libs`).

## License

This project is licensed under the **GNU Lesser General Public License v3.0** License - see the [LICENSE](LICENSE) file for details.
