# Config

!!! warning

    The config may be outdated!

Here is the whole config explained:

```yaml
locale: en                                    #selects language (language file)
debug: false                                  #(not important)
allow-crafting: false                         #(ability to craft)
keep-inventory-on-death: false                #(keeps items after death)
in-lobby-colored-leather-by-team: true        #(when they pick a team, their leather will be colored depending on what team they chose)
jointeam-entity-show-name: true               #(should the jointeam entity have a visible name?)
friendlyfire: false                           #(ability to hurt team mates)
player-drops: true                            #(item drops on death)
join-randomly-after-lobby-timeout: false      #/automaticly joins remaining players to teams after the timeout
prevent-killing-villagers: true               #(being able/not able to kill the Shop villager)
compass-enabled: true                         #(lobby compass)
join-randomly-on-lobby-join: false            #(join random team on joining)
add-wool-to-inventory-on-join: true           #(lobby wool)
prevent-spawning-mobs: true                   #(mob spawning)
spawner-holograms: true                       #(holo's above spawners)
spawner-disable-merge: true                   #(Maybe: disables items stacking on at spawners)
prevent-lobby-spawn-mobs-in-radius: 16        #(doesnt allow mob spawn in lobby of 16 block radious)
spawner-holo-height: 0.25                     #(height of holo)
spawner-holograms-countdown: true             #(countdown of how log left until resource spawns)
damage-when-player-is-not-in-arena: false     #(when a player is in the same world but has not joines the arena cannot get hurt)
remove-unused-target-blocks: true             #(removes the unused target blocks, like beds from non-occupied team bases)
allow-block-falling: true                     #(e.g. gravel falling)
game-start-items: false                       #(give start items)
player-respawn-items: false                   #(give respawn items)
gived-game-start-items: []                    #(see wiki page for start items)
gived-player-respawn-items: []                #(see wiki page for respawn items)
disable-hunger: false                         #(disables hunger)
automatic-coloring-in-shop: true              #(idk)
sell-max-64-per-click-in-shop: true           #(you can buy 64x in 1 click)
destroy-placed-blocks-by-explosion-except: '' #(blacklist for player placed blocks that dont get destroyed by explosions)
destroy-placed-blocks-by-explosion: true      #(can explosions destroy player placed blocks)
holo-above-bed: true                          #(holo above bed "Protect your Bed!")
allow-spectator-join: false                   #(allows people to join when match is in progress but they are in spectator mode)
disable-server-message:                       #(disables server messages)
  player-join: false
  player-leave: false
respawn-cooldown:                             #(how long it takes to respawn)
  enabled: true
  time: 5
stop-team-spawners-on-die: false             #(when a team gets eliminated, the team spawner stops working)
allowed-commands: []                         #(commands which are allowed to be used while in game (useful for moderation e.g /ban))
change-allowed-commands-to-blacklist: false  #turns the allowed-commands into a blacklist rather then a whitelist.
bungee:                                      #(bungeecord function)
  enabled: false                                #(true/false)                
  serverRestart: true                           #(restart server at the end by running the restart scriqpt configured in ?.yml (This is either bukkit.yml or spigot.yml))
  serverStop: false                             #(stops the server at the end of the game)
  server: hub                                   #(server to send players back to at the end of the game, for game lobbies)
  auto-game-connect: false                      #players join automaticly into the arena. Activates pre-game.
farmBlocks:                                  #(which blocks are allowed to be broken)
  enable: false                                 #(true/false)
  blocks: []                                    #(list the blocks)
scoreboard:                                  #(in-game scoreboeard (basicly non-editable))
  enable: true                                  #(true/false)
  title: §a%game%§r - %time%                    
  bedLost: §c✘
  bedExists: §a✔
  teamTitle: '%bed%%color%%team%'
title:                                       #(team bed destroy message)
  fadeIn: 0                                     #(fade in)
  stay: 20                                      #(how long it stays for)
  fadeOut: 0                                    #(fade out)
items:                                       #(lobby items and some shop items)
  jointeam: COMPASS                             #(team select)
  leavegame: SLIME_BALL                         #(leave arena)
  startgame: DIAMOND                            #(start game. (ADMIN ONLY))
  shopback: BARRIER                             #shop back
  shopcosmetic:                                 #shop cosmetics
    ==: org.bukkit.inventory.ItemStack
    type: STAINED_GLASS_PANE
    damage: 7
  pageback: ARROW
  pageforward: ARROW
  team-select:
    ==: org.bukkit.inventory.ItemStack
    type: WOOL
    damage: 1
vault:                                        #(vault support (economy))
  enable: true                                #(true/false)
  reward: 
    kill: 5                                   #(5 currency on kill)
    win: 20                                   #(20 currency on win)
resources:                                    #(resources used(if added new resources,remember to ))
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
    material: CLAY_BRICK
    color: DARK_RED
    name: Bronze
    interval: 1
    translate: resource_bronze
    spread: 1.0
respawn:                                      #(when respawned, you get invinsablilty)
  protection-enabled: true
  protection-time: 10
specials:                                     #(special items)
  action-bar-messages: true
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
    material: SANDSTONE
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
tnt:                                                      #(tnt settings)
  auto-ignite: false 
  explosion-time: 8
sounds:                                                   #(sounds played when an event happens)
  on_bed_destroyed: ENTITY_ENDER_DRAGON_GROWL
  on_countdown: UI_BUTTON_CLICK
  on_game_start: ENTITY_PLAYER_LEVELUP
  on_team_kill: ENTITY_PLAYER_LEVELUP
  on_player_kill: ENTITY_PLAYER_BIG_FALL
  on_item_buy: ENTITY_ITEM_PICKUP
  on_upgrade_buy: ENTITY_EXPERIENCE_ORB_PICKUP
  on_respawn_cooldown_wait: UI_BUTTON_CLICK
  on_respawn_cooldown_done: ENTITY_PLAYER_LEVELUP
game-effects:                                              #(effects when an event happens)
  end:
    effects:
    - ==: Firework
      flicker: false
      trail: false
      colors:
      - &id001
        ==: Color
        RED: 255
        BLUE: 255
        GREEN: 255
      fade-colors:
      - *id001
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
lobby-scoreboard:                                            #(lobby scoreboard)
  enabled: true
  title: §eBEDWARS
  content:
  - ' '
  - '§fMap: §2%arena%'
  - '§fPlayers: §2%players%§f/§2%maxplayers%'
  - ' '
  - §fWaiting ...
  - ' '
statistics:                                                  #(stats settings)
  enabled: true
  type: yaml                                                 #(yaml for local. database for mysql/mariadb)
  show-on-game-end: false
  bed-destroyed-kills: false                                 #(only count kills after team bed is destroyed)
  scores:
    kill: 10
    die: 0
    win: 50
    bed-destroy: 25
    lose: 0
    record: 100
database:                                                    #(database settings)
  host: localhost
  port: 3306
  db: databse
  user: root
  password: secret
  table-prefix: bw_
bossbar:                                                      #(bossbar settings)
  use-xp-bar: false                                           #( this is for 1.8 cuz bossbar is not available (also can be used in newer versions))
  lobby
    enable: true
    color: YELLOW
    style: SEGMENTED_20
  game:
    enable: true
    color: GREEN
    style: SEGMENTED_20
holograms:                                                    #(hologram settings)
  enabled: true
  headline: Your §eBEDWARS§f stats
chat:                                                         #(chat settings)
  override: true
  format: '<%teamcolor%%name%§r> '
  separate-chat:
    lobby: false
    game: false
  send-death-messages-just-in-game: true                      #(death messages in arena)
  send-custom-death-messages: true                            #(custom death messages)
  default-team-chat-while-running: true                       #(team chat as default)
  all-chat-prefix: '@a'                                       #(all chat prefix (e.g @a Good game guys! you shredded me lmao))
  team-chat-prefix: '@t'                                      #(team chat prefix (e.g @tRed is attacking us!))
  all-chat: '[ALL] '                                          #(all chat prefix when used (e.g [ALL] <iamceph> GG))
  team-chat: '[TEAM] '                                        #(team chat prefix when used (e.g [ALL] <iamceph> GREEN is in centre!))
  death-chat: '[DEATH] '                                      #(death chat prefix when used (e.g [DEATH] iamceph Died!))
rewards:                                                      #(Reward (VAULT IS REQUIRED))
  enabled: false
  player-win:
  - /example {player} 200
  player-end-game:
  - /example {player} {score}
  player-destroy-bed:
  - /example {player} {score}
  player-kill:
  - /example {player} 10
lore:
  generate-automatically: true
  text:
  - '§7Price:'
  - §7%price% %resource%
  - '§7Amount:'
  - §7%amount%
sign:                                                          #(Sign info when Arena Sign has been created)
- §c§l[BedWars]
- '%arena%'
- '%status%'
- '%players%' 
hotbar:                                                        #(lobby hotbar placements)
  selector: 0
  color: 1
  start: 2
  leave: 8
breakable:                                                     #(things that are breakable)
  enabled: false
  asblacklist: false
  blocks: []
leaveshortcuts:                                                #(leave settings)
  enabled: false
  list:
  - leave
mainlobby:                                                     #(main lobby settings)
  enabled: false
  location: ''
  world: ''
version: 2
```