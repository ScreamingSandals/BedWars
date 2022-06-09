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
    type: WHITE_WOOL
    amount: 2
```

## Upgrades
See the [Upgrades](upgrades.md) article.

## Language file
See the [Configuration](config.md#custom-language) article.

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
* `bw.admin` - Allows you to create/edit an arena, reload the plugin and use the cheat command if enabled.
* `bw.vip.startitem` - Allows you to start the game using the item. All players will be automatically distributed to teams.
* `bw.vip.forcejoin` - Allows you to join even if the game is full (player without this permission will be kicked).
* `bw.otherstats` - Allows you to see stats of other players.
* `bw.admin.alljoin` - Allows you to use /bw alljoin command.
* `bw.disable.joinall` - Protects you from effects of /bw alljoin command.
* `bw.cmd.join` - Allows you to use /bw join command.
* `bw.cmd.leave` - Allows you to use /bw leave command.
* `bw.cmd.stats` - Allows you to use /bw stats command.
* `bw.cmd.rejoin` - Allows you to use /bw rejoin command.
* `bw.cmd.autojoin` - Allows you to use /bw autojoin command.
* `bw.cmd.list` - Allows you to use /bw list command.
* `bw.cmd.leaderboard` - Allows you to use /bw leaderboard command.

Every permission can be prefixed with `misat11.`. Note that this egoism would eventually be removed in a future major release.

All permissions starting with `bw.cmd.` are allowed by default even if the permission is not explicitly given. This can be changed in `config.yml`.

## Adding start and respawn items
You can by doing the following

### Start game
```yaml
game-start-items: true
gived-game-start-items:
- LEATHER_HELMET
- LEATHER_BOOTS
- LEATHER_LEGGINGS
- LEATHER_CHESTPLATE
- WOODEN_SWORD
```

### Respawn
```yaml
player-respawn-items: true
gived-player-respawn-items: 
- LEATHER_HELMET
- LEATHER_BOOTS
- LEATHER_LEGGINGS
- LEATHER_CHESTPLATE
- WOODEN_SWORD
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
1. Install bedwars **on every game server**.
2. Open the config.yml file on every game server and modify `bungee.enabled: true` , `bungee.server: yourBungeeLobbyServerName` and `bungee.auto-game-connect: true`
3. Setup an arena on every game server.
4. On your lobby server you should setup some plugin for joining the arena. There are some recommended:
   * Get **BungeeSigns** on your lobby server to teleport players to your game servers with signs.
   * Get **SimpleInventories** on your lobby server to teleport players to your game server using menus. (You can also use any inventory plugin which supports sending players to other servers)
5. Configure every game server to your liking. 

## PvP does not work
Make sure:
* It is enabled in MultiVerse (or similar multi-world plugin) and if it is not do `/mvm set pvp true <world name>`.
* WorldGuard does not disallow fighting in the world where the arena is located (if yes do `/rg flag <region name> pvp allow`);
* You have `spawn-protection` disabled (set it to 0 in server.properties).

Keep in mind that BedWars **does not moderate PvP**. If PvP does not work for you, it is caused by a misconfiguration or another plugin. If tips above did not help you, please contact us on our Discord server.

## Players regenerate too quickly/swords deal very little damage
That is not a BedWars issue, increase your server's game difficulty (from easy to normal for example).

## Changing arena name
First, we will provide no support if you mess this up somehow, **arena files are not supposed to be edited by people**.
1. Go to your BedWars folder and then to arenas folder. The path should be `plugins/BedWars/arenas`
2. Open the file you want to change the name
3. First filled is name, enter your new one. The name cannot look like this `test arena` and always needs to be one string, that means `test-arena`. The name also must be unique, that means you cannot have two arenas with the same name.
4. Save the file
5. Restart or reload the server 

## Kicked from server with strange errors
That is not our fault. You are probably using version older than `1.9.4`. Nametags, scoreboars and others might be causing that.  
We ``don't support 1.8.x`` in any way.  
If you are still having this error on a newer version, feel free to contact us on our Discord server.

## Class version errors
`SomeClass has been compiled by a more recent version of the Java Runtime (class file version 55.0), this version of the Java Runtime only recognizes class file versions up to 52.0 (unable to load class SomeClass)`

This means you are using Java 8 (52.0), but the plugin requires at least Java 11 (55.0), if you want to know how to update, visit [this page](https://docs.papermc.io/java-install-update) (It tells you about updating to Java 17 which can run software for Java 11; similar methods can be used for Java 11)

Currently BedWars LATEST_VERSION_HERE does not require Java 11, however the most famous addon SBA requires at least Java 11. New version of BedWars (0.3.0+) will require at least Java 11.

## Using no (Norwegian) language
Well, Yaml specification says that literal `no` means `false`. To use `no` as `no`, you need to cast it to string `locale: "no"`

## Players get disconnected from the bedwars game when they die or players respawn in the arena after reconnecting
A plugin is overriding player spawns on your server, those might include (but not limited to) EssentialsSpawn, WorldGuard, etc.
Remove those plugins or disable them in bedwars worlds. 

