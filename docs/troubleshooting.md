# Troubleshooting and FAQ

## Non-OP players cannot use signs
Move the sign out of vanilla spawn protection (for example, if you have the spawn protection in server.properties set to 16, move 34 blocks away from spawn location) or change spawn protection to zero. Non-OP players can't do some things in spawn protection.

## Position 1 and Position 2
These two positions serve to designate an area to be considered an arena. (such as axe in WorldEdit) 

See the [Arena](arena.md#setting-the-arena-positions)

<img alt="Arena bounds" src="../assets/arena_bounds.png" width="400"/>

## Bed cannot be destroyed
BedWars allows you to use any block as the target block, so you won't see any error when you set it to, for example, floor. So check it, and make you sure you're looking at the bed head when setting the team target block.

## Adding a sign
Place a sign and write `[BedWars]` or `[BWGame]` (case-sensitive) as the first line, and the name of your arena as the second line. Make sure that your sign is not in range of vanilla's spawn protection.

## Automatically coloring a shop item
Use the `applycolorbyteam` property, for example:
```yaml
- price: 1
  price-type: bronze
  properties:
    - name: "applycolorbyteam"
  stack:
    type: white_wool
    amount: 2
```

## Upgrades
See the [Upgrades](upgrades.md) article.

## Language file
See the [Language](language.md) article.

## PlaceholderAPI placeholders
See the [Placeholder API](placeholderapi.md) article.

## Changing the message prefix
Prefix is changeable globally in the language file or per arena with this command: `/bw admin <arena> customprefix &6My Awesome Prefix `.

## Editing resources
See the [Configuration](config.md) article.

## Broken shop
Make sure your shop is YAML valid with [yamlchecker](https://yamlchecker.com/), has proper [materials](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html) and [format](https://github.com/ScreamingSandals/SimpleInventories/wiki).

## Compass teleports the player
Well, you probably have WorldEdit or FastAsyncWorldEdit. The compass is used to teleport through walls. However it works only for people with permission or with OP. You can disable this tool or bound it to another item in WorldEdit configuration. You can also change the team selection item to another one in BedWars configuration.

## Permissions

See the [Commands and Permissions](commands.md#permissions) page.

## Adding start and respawn items
You can by doing the following

### Start game
```yaml
game-start-items: true
gived-game-start-items:
- leather_helmet
- leather_boots
- leather_leggings
- leather_chestplate
- wooden_sword
```

### Respawn
```yaml
player-respawn-items: true
gived-player-respawn-items: 
- leather_helmet
- leather_boots
- leather_leggings
- leather_chestplate
- wooden_sword
```

## Villagers do not spawn
Here is some info on how to troubleshoot this error:

* Make sure that Mob Spawning is **enabled**
* You are **NOT** protecting the arena with worldguard
* Make sure that NPC's are **enabled**

## Are there any visual/particle effects?
Yes, there are. Check out [this page](config.md#game-effects)

## Changing damage or explosion power of Fireball
Go in your config.yml and there are settings for the Fireball.
It is located under specials, it is not that hard to find 😛

Same goes for basically any other special item. 🙂

## Setting up the plugin with Bungeecord

The plugin has a single-arena bungeecord mode. Check out [this page](bungee.md).

## PvP does not work
Make sure:

* It is enabled in MultiVerse (or similar multi-world plugin) and if it is not do `/mvm set pvp true <world name>`.
* WorldGuard does not disallow fighting in the world where the arena is located (if yes do `/rg flag <region name> pvp allow`);
* You have `spawn-protection` disabled (set it to 0 in server.properties).

Keep in mind that BedWars **does not moderate PvP**. If PvP does not work for you, it is caused by a misconfiguration or another plugin. If tips above did not help you, please contact us on our Discord server.

If you have WorldGuard installed, you can also use a command to check which plugin blocks PvP. The command is `/wg debug testdamage -t <player_name>` and you need a second player to test this out. Click [here](https://worldguard.enginehub.org/en/latest/commands/#event-simulation) to read more about this command.

## Players regenerate too quickly/swords deal very little damage
That is not a BedWars issue, increase your server's game difficulty (from easy to normal for example).

## Changing arena name
First, we will provide no support if you mess this up somehow, **arena files are not supposed to be edited by people**.

1. Go to your BedWars folder and then to arenas folder. The path should be `plugins/BedWars/arenas`
2. Open the file you want to change the name
3. First filled is name, enter your new one. The name cannot look like this `test arena` and always needs to be one string, that means `test-arena`. The name also must be unique, that means you cannot have two arenas with the same name.
4. Save the file
5. Restart or reload the server 

## Class version errors
`SomeClass has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0 (unable to load class SomeClass)`

This means you are using Java 8 (52.0), but the plugin requires at least Java 11 (55.0), if you want to know how to update, visit [this page](https://docs.papermc.io/java-install-update) (It tells you about updating to Java 17 which can run software for Java 11; similar methods can be used for Java 11)

Currently BedWars LATEST_VERSION_HERE does not require Java 11, however the most famous addon SBA requires at least Java 11. New version of BedWars (0.3.0+) will require at least Java 11.

## Using no (Norwegian) language
Well, Yaml specification says that literal `no` means `false`. To use `no` as `no`, you need to cast it to string `locale: "no"`

## Players get disconnected from the bedwars game when they die or players respawn in the arena after reconnecting
A plugin is overriding player spawns on your server, those might include (but not limited to) EssentialsSpawn, WorldGuard, etc.
Remove those plugins or disable them in bedwars worlds. 

