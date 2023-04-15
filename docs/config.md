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
player-drops: false
# should the players be joined to a random team after the lobby waiting time passes?
# if false, the game won't start until everyone chooses a team
join-randomly-after-lobby-timeout: true
# should BedWars prevent players from killing the merchants?
prevent-killing-villagers: true
# should the compass for choosing teams be available in the lobby?
compass-enabled: true
# should BedWars join the player to a random team when he joins the lobby?
join-randomly-on-lobby-join: false
add-wool-to-inventory-on-join: true
prevent-spawning-mobs: true
spawner-holograms: false
spawner-disable-merge: true
prevent-lobby-spawn-mobs-in-radius: 16
spawner-holo-height: 0.25
spawner-holograms-countdown: true
damage-when-player-is-not-in-arena: true
remove-unused-target-blocks: true
allow-block-falling: true
game-start-items: true
player-respawn-items: false
gived-game-start-items:
- wooden_sword
- leather_helmet
- leather_boots
- leather_leggings
- leather_chestplate
disable-hunger: true
automatic-coloring-in-shop: true
sell-max-64-per-click-in-shop: true
enable-cheat-command-for-admins: false
shopkeepers-are-silent: true
destroy-placed-blocks-by-explosion-except:
- ''
destroy-placed-blocks-by-explosion: true
holo-above-bed: true
allow-spectator-join: false
disable-server-message:
  player-join: false
  player-leave: false
respawn-cooldown:
  enabled: true
  time: 5
stop-team-spawners-on-die: false
# Whether or not should 1.19.4 display entities be used or not. Does work only on 1.19.4+
prefer-1-19-4-display-entities: true
allowed-commands: []
change-allowed-commands-to-blacklist: false
bungee:
  enabled: false
  serverRestart: true
  serverStop: false
  server: hub
  auto-game-connect: false
  kick-when-proxy-too-slow: true
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
  enable: false
  title: &a%game%&r - %time%
  bedLost: &c✘
  bedExists: &a✔
  teamTitle: '%bed%%color%%team%'
  anchorEmpty: &e✘
title:
  enabled: true
  fadeIn: 0
  stay: 20
  fadeOut: 0
items:
  jointeam: COMPASS
  leavegame: RED_BED
  startgame: DIAMOND
  shopback: BARRIER
  shopcosmetic: GRAY_STAINED_GLASS_PANE
  pageback: ARROW
  pageforward: ARROW
  team-select: WHITE_WOOL
vault:
  enable: false
  reward:
    kill: 5
    win: 20
    final-kill: 5
    bed-destroy: 0
resources:
  emerald:
    material: EMERALD
    color: GREEN
    name: Emerald
    interval: 60
    translate: resource_emerald
    spread: 0.1
  diamond:
    material: DIAMOND
    color: BLUE
    name: Diamond
    interval: 30
    translate: resource_diamond
    spread: 0.1
  iron:
    material: IRON_INGOT
    color: WHITE
    name: Iron
    interval: 2.5
    translate: resource_iron
    spread: 1.0
  gold:
    material: GOLD_INGOT
    color: GOLD
    name: Gold
    interval: 8
    translate: resource_gold
    spread: 1.0
respawn:
  protection-enabled: true
  protection-time: 5
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
    speed: 2.0
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
    explosion-time: 3
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
  enabled: false
  title: &eBEDWARS
  content:
  - ' '
  - '&fMap: &2%arena%'
  - '&fPlayers: &2%players%&f/&2%maxplayers%'
  - ' '
  - &fWaiting ...
  - ' '
statistics:
  enabled: true
  type: yaml
  show-on-game-end: false
  bed-destroyed-kills: false
  scores:
    kill: 10
    die: 0
    win: 50
    bed-destroy: 25
    lose: 0
    record: 100
    final-kill: 0
database:
  host: localhost
  port: 3306
  db: database
  user: root
  password: secret
  table-prefix: bw_
  useSSL: false
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
lore:
  generate-automatically: true
  text:
  - '&7Cost: &f%price% %resource%'
  - ''
sign:
  lines:
  - &c&l[BedWars]
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
disableCakeEating: true
disableDragonEggTeleport: true
disable-flight: true
shop:
  rows: 4
  render-actual-rows: 6
  render-offset: 9
  render-header-start: 0
  render-footer-start: 45
  items-on-row: 9
  show-page-numbers: true
  inventory-type: CHEST
  citizens-enabled: true
gived-player-respawn-items: []
allow-fake-death: false
preventArenaFromGriefing: false
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
    - &a%spectators% are watching this match
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
# The following sections requires Parties plugin to be installed
party:
  enabled: false
  autojoin-members: false
  notify-when-warped: true
```

## Custom resources

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

## Game effects

In config.yml you can find section called `game-effects`. Here you can set some visual effects that will enhance your game experiences.

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

## Custom language

* Download the base language file from [here](https://github.com/ScreamingSandals/BedWars/tree/ver/0.2.x/plugin/src/main/resources/languages)
* Create folder named "**languages**" in your _BedWars_ folder. (_BedWars_ folder is in default _plugins_ folder)
* Paste your language here. For example, language_cs.yml
* Open your **config.yml** and configure variable "**locale**" to "**cs**"

And that's it, you have your own language!