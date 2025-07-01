# Configuration

```yaml
# the language of the plugin's messages
locale: en
# enables debug messages, may help with resolving certain problems
debug: false
# is crafting in the arenas allowed?
allow-crafting: false
# should items be kept when the player dies in the arena?
keep-inventory-on-death: false
# should player's armor be colored after choosing their team in the lobby?
in-lobby-colored-leather-by-team: true
# should the entity for joining teams have a visible name?
jointeam-entity-show-name: true
# are teammates allowed to damage themselves?
friendlyfire: false
# do items drop from players when killed in the arena?
player-drops: true
# should the players be joined to a random team after the lobby waiting time passes?
# if false, the game won't start until everyone chooses a team
join-randomly-after-lobby-timeout: false
# should BedWars prevent players from killing the merchants?
prevent-killing-villagers: true
# should the compass for choosing teams be available in the lobby?
compass-enabled: true
# should BedWars join the player to a random team when he joins the lobby?
join-randomly-on-lobby-join: false
add-wool-to-inventory-on-join: true
prevent-spawning-mobs: true
spawner-holograms: true
spawner-disable-merge: true
prevent-lobby-spawn-mobs-in-radius: 16
spawner-holo-height: 0.25
spawner-holograms-countdown: true
damage-when-player-is-not-in-arena: false
remove-unused-target-blocks: true
allow-block-falling: true
game-start-items: false
player-respawn-items: false
# When game-start-items is enabled, the player is going to get all items listed in the following list.
# Example:
# gived-game-start-items:
# - wooden_sword
# - leather_helmet
# - leather_boots
# - leather_leggings
# - leather_chestplate
gived-game-start-items: []
gived-player-respawn-items: []
disable-hunger: false
automatic-coloring-in-shop: true
sell-max-64-per-click-in-shop: true
enable-cheat-command-for-admins: false
shopkeepers-are-silent: true
destroy-placed-blocks-by-explosion-except: []
destroy-placed-blocks-by-explosion: true
holo-above-bed: true
allow-spectator-join: false
disable-server-message:
  player-join: false
  player-leave: false
disable-flight: true
respawn-cooldown:
  enabled: true
  time: 5
stop-team-spawners-on-die: false
allow-fake-death: false
# Whether or not should 1.19.4 display entities be used or not. Does work only on 1.19.4+
prefer-1-19-4-display-entities: true
remember-what-scoreboards-players-had-before: false
use-chunk-tickets-if-available: true
reset-full-spawner-countdown-after-picking: true
players-can-win-game-only-after-seconds: 0
disable-locator-bars-in-arena-worlds: true
disable-opening-stores-of-other-teams: false
kick-players-upon-final-death:
  enabled: false
  delay: 5
allowed-commands: []
change-allowed-commands-to-blacklist: false
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
farmBlocks:
  enable: false
  blocks: []
scoreboard:
  enable: true
  title: '&a%game%&r - %time%'
  bedLost: '&c✘'
  anchorEmpty: '&e✘'
  bedExists: '&a✔'
  teamTitle: '%bed%%color%%team%'
title:
  enabled: true
  fadeIn: 0
  stay: 20
  fadeOut: 0
shop:
  rows: 4
  render-actual-rows: 6
  render-offset: 9
  render-header-start: 0
  render-footer-start: 45
  items-on-row: 9
  show-page-numbers: true
  inventory-type: CHEST
  citizens-enabled: false
  allow-execution-of-console-commands: true
items:
  jointeam: COMPASS
  leavegame: SLIME_BALL
  startgame: DIAMOND
  shopback: BARRIER
  shopcosmetic: GRAY_STAINED_GLASS_PANE
  pageback: ARROW
  pageforward: ARROW
  team-select: WHITE_WOOL
vault:
  enable: true
  reward:
    kill: 5
    win: 20
    final-kill: 5
    bed-destroy: 0
resources:
  gold:
    material: GOLD_INGOT
    color: GOLD
    name: Gold
    interval: 20
    translate: resource_gold
    spread: 1.0
  iron:
    material: IRON_INGOT
    color: GRAY
    name: Iron
    interval: 10
    translate: resource_iron
    spread: 1.0
  bronze:
    material: BRICK
    color: DARK_RED
    name: Bronze
    interval: 1
    translate: resource_bronze
    spread: 1.0
respawn:
  protection-enabled: true
  protection-time: 10
  show-messages: true
specials:
  action-bar-messages: true
  dont-show-success-messages: false
  rescue-platform:
    is-breakable: false
    delay: 0
    break-time: 10
    distance: 1
    material: GLASS
  protection-wall:
    is-breakable: false
    delay: 20
    break-time: 0
    width: 5
    height: 3
    distance: 2
    material: CUT_SANDSTONE
  tnt-sheep:
    speed: 0.25
    follow-range: 10.0
    max-target-distance: 32
    explosion-time: 8
  arrow-blocker:
    protection-time: 10
    delay: 5
  warp-powder:
    teleport-time: 6
    delay: 0
  magnet-shoes:
    probability: 75
  golem:
    speed: 0.25
    follow-range: 10
    health: 20
    name-format: '%teamcolor%%team% Golem'
    show-name: true
    delay: 0
    collidable: false
  teamchest:
    turn-all-enderchests-to-teamchests: true
  throwable-fireball:
    damage: 3.0
    incendiary: true
    damage-thrower: true
  auto-igniteable-tnt:
    explosion-time: 8
    damage-placer: true
    damage: 4.0
sounds:
  bed_destroyed:
    sound: ENTITY_ENDER_DRAGON_GROWL
    volume: 1
    pitch: 1
  my_bed_destroyed:
    sound: ENTITY_ENDER_DRAGON_GROWL
    volume: 1
    pitch: 1
  countdown:
    sound: UI_BUTTON_CLICK
    volume: 1
    pitch: 1
  game_start:
    sound: ENTITY_PLAYER_LEVELUP
    volume: 1
    pitch: 1
  team_kill:
    sound: ENTITY_PLAYER_LEVELUP
    volume: 1
    pitch: 1
  player_kill:
    sound: ENTITY_PLAYER_BIG_FALL
    volume: 1
    pitch: 1
  item_buy:
    sound: ENTITY_ITEM_PICKUP
    volume: 1
    pitch: 1
  upgrade_buy:
    sound: ENTITY_EXPERIENCE_ORB_PICKUP
    volume: 1
    pitch: 1
  respawn_cooldown_wait:
    sound: UI_BUTTON_CLICK
    volume: 1
    pitch: 1
  respawn_cooldown_done:
    sound: ENTITY_PLAYER_LEVELUP
    volume: 1
    pitch: 1
game-effects:
  end:
    effects:
    - ==: Firework
      flicker: false
      trail: false
      colors:
      - ==: Color
        RED: 255
        BLUE: 255
        GREEN: 255
      fade-colors:
      - ==: Color
        RED: 255
        BLUE: 255
        GREEN: 255
      type: BALL
    power: 1
    type: Firework
  start: {}
  kill: {}
  teamkill: {}
  lobbyjoin: {}
  lobbyleave: {}
  respawn: {}
  beddestroy: {}
  warppowdertick: {}
lobby-scoreboard:
  enabled: true
  title: '&eBEDWARS'
  content:
  - ' '
  - '&fMap: &2%arena%'
  - '&fPlayers: &2%players%&f/&2%maxplayers%'
  - ' '
  - '&fWaiting ...'
  - ' '
statistics:
  enabled: true
  type: yaml
  show-on-game-end: false
  bed-destroyed-kills: false
  scores:
    kill: 10
    final-kill: 0
    die: 0
    win: 50
    bed-destroy: 25
    lose: 0
    record: 100
database:
  host: localhost
  port: 3306
  db: database
  user: root
  password: secret
  table-prefix: bw_
  type: mysql
  driver: default
  params:
    useSSL: false
    serverTimezone: Europe/Prague
    autoReconnect: true
    cachePrepStmts: true
    prepStmtCacheSize: 250
    prepStmtCacheSqlLimit: 2048
bossbar:
  use-xp-bar: false
  lobby:
    enable: true
    color: YELLOW
    style: SEGMENTED_20
  game:
    enable: true
    color: GREEN
    style: SEGMENTED_20
  # The backend-entity field is present only if the server is running on 1.8.8. Allowed values: wither, dragon
  backend-entity: dragon
  # The allow-via-hooks field is present only if the server is running on 1.8.8. 
  # The plugin tries to hook to ViaVersion API to make colored and segmented bossbars for 1.9+ players. 
  # As the ViaVersion API constantly changes, this may occasionally cause protocol issues.
  allow-via-hooks: true
holograms:
  enabled: true
  headline: Your &eBEDWARS&f stats
  leaderboard:
    headline: '&6Bedwars Leaderboard'
    format: '&l%order%. &7%name% - &a%score%'
    size: 10
chat:
  override: true
  format: '<%teamcolor%%name%&r> '
  separate-chat:
    lobby: false
    game: false
  send-death-messages-just-in-game: true
  send-custom-death-messages: true
  default-team-chat-while-running: true
  all-chat-prefix: '@a'
  team-chat-prefix: '@t'
  all-chat: '[ALL] '
  team-chat: '[TEAM] '
  death-chat: '[DEATH] '
  disable-all-chat-for-spectators: false
rewards:
  enabled: false
  player-win:
  - /example {player} 200
  player-win-run-immediately:
  - /example {player} 200
  player-end-game:
  - /example {player} {score}
  player-destroy-bed:
  - /example {player} {score}
  player-kill:
  - /example {player} 10
  player-final-kill:
  - /example {player} 10
  player-game-start:
  - /example {player} 10
  player-early-leave:
  - /example {player} {death} 10
  team-win:
  - /example {team} 10
  player-team-win:
  - /example {team} {death} 10
  game-start:
  - /example Hello World!
lore:
  generate-automatically: true
  text:
  - '&7Price:'
  - '&7%price% %resource%'
  - '&7Amount:'
  - '&7%amount%'
sign:
  lines:
  - '&c&l[BedWars]'
  - '%arena%'
  - '%status%'
  - '%players%'
  block-behind:
    enabled: false
    waiting: ORANGE_STAINED_GLASS
    rebuilding: BROWN_STAINED_GLASS
    in-game: GREEN_STAINED_GLASS
    game-disabled: RED_STAINED_GLASS
hotbar:
  selector: 0
  color: 1
  start: 2
  leave: 8
breakable:
  enabled: false
  asblacklist: false
  explosions: false
  blocks: []
leaveshortcuts:
  enabled: false
  list:
  - leave
mainlobby:
  enabled: false
  location: ''
  world: ''
turnOnExperimentalGroovyShop: false
preventSpectatorFlyingAway: false
removePurchaseMessages: false
removePurchaseFailedMessages: false
removeUpgradeMessages: false
disableCakeEating: true
disableDragonEggTeleport: true
preventArenaFromGriefing: true
update-checker:
  zero:
    console: true
    admins: true
  one:
    console: true
    admins: true
target-block:
  allow-destroying-with-explosions: false
  respawn-anchor:
    fill-on-start: true
    enable-decrease: true
    sound:
      charge: BLOCK_RESPAWN_ANCHOR_CHARGE
      used: BLOCK_GLASS_BREAK
      deplete: BLOCK_RESPAWN_ANCHOR_DEPLETE
  cake:
    destroy-by-eating: true
event-hacks:
  damage: false
  destroy: false
  place: false
tab:
  enable: false
  header:
    enabled: true
    contents:
    - '&aMy awesome BedWars server'
    - '&bMap: %map%'
    - '&cPlayers: %respawnable%/%max%'
  footer:
    enabled: true
    contents:
    - '&eexample.com'
    - '&fWow!!'
    - '&a%spectators% are watching this match'
  hide-spectators: true
  hide-foreign-players: false
default-permissions:
  join: true
  leave: true
  stats: true
  list: true
  rejoin: true
  autojoin: true
  leaderboard: true
  party: true
# The following sections requires Parties plugin to be installed
party:
  enabled: false
  autojoin-members: false
  notify-when-warped: true
```

## Custom resources

!!! tip
    If you want to switch to `emerald`, `diamond`, `iron` and `gold` specifically, you may want to have another Hypixel features on your server, like upgrades. 
    In that case, we would recommend you checking out [SBA](https://www.spigotmc.org/resources/sba-screaming-bedwars-addon-1-8-8-1-20-1.99149/).

Open the config (`plugins/BedWars/config.yml`) and scroll down, until you find a section called `resources`. Using `Ctrl+F` helps you to search for it.

Here is an example on how to add `emerald`, `diamond`, `iron` and `gold`.

```yaml
resources:
  emerald: # resource name
    material: EMERALD # resource material (https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html)
    color: GREEN # resource color (https://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html)
    name: Emerald # resource in-game name
    interval: 60 # resource spawn time in seconds
    translate: resource_emerald # resource translation key
    spread: 1.0 # resource spread radius
  diamond:
    material: DIAMOND
    color: BLUE
    name: Diamond
    interval: 30
    translate: resource_diamond
    spread: 1.0
  iron:
    material: IRON_INGOT
    color: WHITE
    name: Iron
    interval: 2
    translate: resource_iron
    spread: 1.0
  gold:
    material: GOLD_INGOT
    color: GOLD
    name: Gold
    interval: 8
    translate: resource_gold
    spread: 1.0
```

## Database connection

In config.yml you can find a section called `database`. Under this section, you can configure the database connection. Currently only MySQL and MariaDB are officially supported, yet the configuration allows you to provide a custom driver for a newer version of the database system or a driver for a different SQL-like database system like PostgreSQL. Pull requests fixing support with different database systems are welcome :)

To enable the database connection, you have to set something to be saved in database. Currently only statistics can be saved to the database. To enable that, locate the `statistics` section and switch `type` from `yaml` to `database`.

There are following fields in the database section:

* `host` is the hostname or IP address of the database server, defaults to `localhost`.
* `port` is the port of the database server, defaults to `3306` which is the default port for MySQL/MariaDB.
* `db` is the name of the database, defaults to `database`
* `user` is the user of the database system with access to database specified `db`. The user needs to be able to change the structure of the database. Defaults to `root`, though applications should not have root access.
* `password` is the password of the database user. Defaults to `secret`, though you should choose better password (possibly generated).
* `table-prefix` is a string which is prepended to table names, defaults to `bw_`.
* `type` is type of the database system, defaults to `mysql` (valid for both MySQL and MariaDB when the MySQL driver is used).
* `driver` is the driver, which is going to be used. There are two possible options:
  * You can set it to `default`. The driver will be chosen based on the type. Both Spigot and Paper servers provide driver for type `mysql`. Other plugins may provide other drivers to the classpath.
  * To use a third-party driver, specify the path, for example `mysql-connector-j-8.0.0.jar`. The path is always relative to the `plugins/BedWars` folder, so we recommend putting the jar to this folder. The driver needs to be JDBC 4-compatible. The driver is not available to other plugins, and does not affect them in any way.
* `params` is a map containing specific options for the driver. You should check documentation of the chosen database system before modifying it. By default, following parameters are set:
  ```yaml
  params:
    useSSL: false  # change this to true if your database server requires SSL or runs on a different machine and has SSL enabled
    serverTimezone: Europe/Prague  # default value is based on your system
    autoReconnect: true
    cachePrepStmts: true
    prepStmtCacheSize: 250
    prepStmtCacheSqlLimit: 2048
  ```

!!! warning "Driver version"
    
    If you use an ancient Minecraft version like 1.8.8, but you have new version of MySQL/MariaDB, the driver bundled in Spigot 1.8.8 may be incompatible. Get a new version of MySQL Connector J [here](https://dev.mysql.com/downloads/connector/j/): select Platform Independent, download the archive and extract the mysql-connector-j-8.x.x.jar file from it (other files from the archive are not relevant). Put the JAR file in `plugins/BedWars` folder, and change `driver` from `default` to the name of the file, eg. `mysql-connector-j-8.4.0.jar`.

!!! info "stats_players table structure"
    
    In specific cases, the automatic creation of the database table may fail. In that case you may need to create it manually, using the following code or its variation.

    ```sql
    CREATE TABLE IF NOT EXISTS `bw_stats_players` (
      `kills` int(11) NOT NULL DEFAULT '0',
      `wins` int(11) NOT NULL DEFAULT '0',
      `score` int(11) NOT NULL DEFAULT '0',
      `loses` int(11) NOT NULL DEFAULT '0',
      `name` varchar(255) NOT NULL,
      `destroyedBeds` int(11) NOT NULL DEFAULT '0',
      `uuid` varchar(255) NOT NULL,
      `deaths` int(11) NOT NULL DEFAULT '0',
      PRIMARY KEY (`uuid`)
    );
    ```

## Game effects

In config.yml you can find a section called `game-effects`. Here you can set some visual effects that will enhance your game experiences.

### Events
* `end` - This effect is called when game ends.
* `start` - This effect is called when game starts.
* `kill` - This effect is called when someone kills someone.
* `teamkill` - This effect is called when someone kills someone and bed or other target block is destroyed.
* `lobbyjoin` - This effect is called when someone enters the lobby.
* `lobbyleave` - This effect is called when someone leaves the lobby.
* `respawn` - This effect is called when someone is respawned.
* `beddestroy` - This effect is called when someone destroys bed or other target block.
* `warppowdertick` - This effect is caled when someone is teleported by Warp Powder.

### Effect types
* `Particle` - [Particle effect (click here for list)](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Particle.html)
* `Effect` - [Effect (click here for list)](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Effect.html)
* `Firework` - Launches a firework
* `List` - Multiple effects on one event

#### Particle
```yaml
game-effects:
  start:
    type: Particle
    value: LAVA # uppercase key from the list
    # options below are optional
    count: 2 # how many particles will be spawned (default - 1)
    offsetX: 1 # offset from the event location (default - 0 for each coordinate)
    offsetY: 1
    offsetZ: 1
    extra: 1 # extra data, depends on each particle (default - 1)
```

#### Effect
```yaml
game-effects:
  start:
    type: Effect
    value: DOOR_CLOSE # uppercase key from the list
```

#### Firework
Firework effect types are listed [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/FireworkEffect.Type.html)
```yaml
game-effects:
  end:
    type: Firework
    power: 1 # power of the firework
    effects: # list of firework effects
    - ==: Firework
      flicker: false
      trail: false
      colors: # list of colors
      - ==: Color
        RED: 255
        BLUE: 255
        GREEN: 255
      fade-colors:
      - ==: Color
        RED: 255
        BLUE: 255
        GREEN: 255
      type: BALL # effect type
```

#### List
```yaml
game-effects:
  end:
    type: List
    list: # list of effects
    - type: Particle
      value: LAVA
      count: 2
      extra: 1
    - type: Effect
      value: DOOR_CLOSE # uppercase key from the list
    - type: Firework
      power: 1 # power of the firework
      effects: # list of firework effects
      - ==: Firework
        flicker: false
        trail: false
        colors: # list of colors
        - ==: Color
          RED: 255
          BLUE: 255
          GREEN: 255
        fade-colors:
        - ==: Color
          RED: 255
          BLUE: 255
          GREEN: 255
        type: BALL # effect type
```