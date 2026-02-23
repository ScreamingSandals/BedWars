# BedWars
[![Build and publish BedWars ver/0.2.x](https://github.com/ScreamingSandals/BedWars/actions/workflows/build-0-2-x.yml/badge.svg)](https://github.com/ScreamingSandals/BedWars/actions/workflows/build-0-2-x.yml)
[![Discord](https://img.shields.io/discord/582271436845219842?logo=discord)](https://discord.gg/4xB54Ts)

This branch includes bugfixes for Minecraft: Java Edition 1.17, 1.18, 1.19, 1.20 & 1.21.

Complete rewrite of the BedWarsRel plugin.
This project is now under complete rewrite and we don't have much time, so be patient :)

Supported versions: \[1.8.8 - 1.21.11\]. Recommended version: \[1.21.11\]

## Support
If you need any help, you can contact us on [Discord](https://discord.gg/4xB54Ts). Please make sure to look into older messages. There are many question already answered. It is really anoying to repeat the same thing over and over.

Make sure to check out our [Wiki](https://github.com/ScreamingSandals/BedWars/wiki) before contacting us on Discord. It will save our time, you know. O:)

If you have found any bug, feel free to report is into [Issues](https://github.com/ScreamingSandals/BedWars/issues), we will look into it.

## Features
-   All the basics of BedWars game (Beds, Teams and so on)
-   Other BedWars variants: CakeWars/EggWars/AnchorWars
-   Shop that supports multi-shop per arena!
-   BungeeCord
-   Vault rewards
-   Spectator mode (now, spectators can join running game!)
-   Arena rebuilding (fast af)
-   BossBar or XP bar in lobby countdown or with game time
-   Breakable (those are refreshing after arena rebuild) / ignored blocks
-   SpecialItems (RescuePlatform, TNTSheep and so on) - they can be configured in shop too!

### Customizable
-   Team selecting GUI
-   Auto coloring items (like armor and so)
-   Resource Spawners (you can have as much as you want)
-   Player statistics
-   In fact everything is customizable
-   And many more useful features..

## Compiling

This project uses **Gradle** and requires **JDK 17** or newer (the compiled JARs require JDK 8 or newer to run). To build it, clone the repository and run:

```bash
./gradlew clean build
```

On Windows, use:

```bat
gradlew.bat clean build
```

The compiled JAR file will be located in the `plugin/build/libs` directory.

## License

This project is licensed under the **GNU Lesser General Public License v3.0** License - see the [LICENSE](LICENSE) file for details.