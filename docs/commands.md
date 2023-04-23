# Commands and Permissions

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

Every permission can be prefixed with `misat11.`. Note that this egoism is deprecated and will be removed in a future release.

All permissions starting with `bw.cmd.` are allowed by default even if the permission is not explicitly given. This can be changed in `config.yml` in section `default-permissions`

```yaml
default-permissions:
  join: true
  leave: true
  stats: true
  list: true
  rejoin: true
  autojoin: true
  leaderboard: true
```

## Commands

Every command has its own specified permission in brackets. 

`<argument>` is required argument.   
`[argument]` is optional argument, and the command can be executed if this argument is not specified. However, every optional argument before this optional argument must be specified in order to be able to use this argument.

### Player commands

* `/bw help` - Shows list of commands (no permission required)
* `/bw join [arena]` - Join the arena (permission: `bw.cmd.join`)
* `/bw leave` - Leave the arena (permission: `bw.cmd.leave`)
* `/bw list` - Lists all available arenas (permission: `bw.cmd.list`)
* `/bw stats` - Shows your statistics (permission: `bw.cmd.stats`)
* `/bw stats <player>` - Shows your statistics (permission: `bw.cmd.stats` together with `bw.otherstats` or `bw.admin`)
* `/bw leaderboard` - Shows top players on this server (permission: `bw.cmd.leaderboard`)
* `/bw rejoin` - Joins the latest game you have played (permission: `bw.cmd.rejoin`)
* `/bw autojoin` - Joins random game (permission: `bw.cmd.autojoin`)
* `/bw party warp` - Warps all players in your party to your game or towards you (permission: `bw.cmd.party`, requires [Parties](https://alessiodp.com/parties) plugin and `party` section in config.yml enabled)

### Admin Commands

All these commands require `bw.admin` permission if not specified.

#### Arena editing

* `/bw admin <arena> info [section]` - Shows all information about the arena
* `/bw admin <arena> add` - Creates a new arena
* `/bw admin <arena> lobby` - Sets lobby position to your position
* `/bw admin <arena> spec` - Sets specatator's spawn to your position
* `/bw admin <arena> pos1` - Sets the extreme point of the arena to your position
* `/bw admin <arena> pos2` - Sets the second extreme point of the arena to your position
* `/bw admin <arena> pausecountdown <seconds>` - Sets the pause duration before game starts
* `/bw admin <arena> minplayers <minimum>` - Sets minimum of players needed to start the game
* `/bw admin <arena> time <seconds>` - Sets the game's duration
* `/bw admin <arena> team add <team> <color> <max players>` - Add a team to the game
* `/bw admin <arena> team color <team> <color>` - Change team color
* `/bw admin <arena> team maxplayers <team> <max players>` - Change max team players
* `/bw admin <arena> team spawn <team>` - Sets team spawn to your position
* `/bw admin <arena> team bed <team> [looking_at|standing_on]` - Sets team target block to your target position (it can be any block, not just bed)
* `/bw admin <arena> jointeam <team>` - Sets the ability to connect to a team using an entity
* `/bw admin <arena> spawner add <bronze|gold|iron> [hologram] [first level] [name] [team] [max spawned resources]` - Add item spawner to your position (alternatively you can specify max spawned resources without specifying team)
* `/bw admin <arena> spawner reset` - Remove all spawners
* `/bw admin <arena> spawner remove` - Removes all spawners at location
* `/bw admin <arena> store add [name above dealer head] [file with shop] [use main shop]` - Add trading villager to your position
* `/bw admin <arena> store remove` - Remove trading villager from your position
* `/bw admin <arena> store type <living entity>` - Sets entity type of store (Villager, Horse, Cow, Pig etc.)
* `/bw admin <arena> store child` - Mark this shopkeeper as child
* `/bw admin <arena> store adult` - Mark this shopkeeper as adult
* `/bw admin <arena> config <constant> <value>` - Sets value of constant variable for game
* `/bw admin <arena> arenatime <type>` - Sets time in arena (DAY, NIGHT, etc.)
* `/bw admin <arena> arenaweather <type>` - Sets weather in arena (default, CLEAN, DOWNFALL)
* `/bw admin <arena> postgamewaiting <seconds>` - Sets the waiting time before player is teleported out of the arena
* `/bw admin <arena> customprefix <name|off>` - Enables custom prefix for arena
* `/bw admin <arena> save` - Saves the game and activates it!
* `/bw admin <arena> edit` - Turns the game off and switches the arena to edit mode
* `/bw admin <arena> remove` - Remove the arena

#### Cheat commands

These command have to be enabled manually using the config option `enable-cheat-command-for-admins`. The admin must be in the game (running or in waiting lobby) in order to execute cheat.

* `/bw cheat give <resource> [amount] [player]` - Gives player specified amount of resource
* `/bw cheat kill [player]` - Kills specified player
* `/bw cheat startemptygame` - Starts a game with only one team (useful for testing)
* `/bw cheat destroybed <team>` - Destroys bed of the specific team
* `/bw cheat destroyallbeds` - Destroys all beds in the game
* `/bw cheat jointeam [team]` - Joins an admin to the specified team or to current team ignoring its size (can also be used while the game is running, however the team must have some players)

#### Misc

* `/bw alljoin [arena]` - Joins all players without `bw.disable.alljoin` permission to the arena (permission: `bw.admin.alljoin`)
* `/bw reload` - Reloads the plugin
* `/bw mainlobby [enable|set]` - Enables main lobby or sets main lobby location
* `/bw dump [paste.gg|pastes.dev]` - Dumps information about the server and gives you link to the dump. This link can be useful for support.
* `/bw addholo [leaderboard|stats]` - Adds new hologram to your position
* `/bw removeholo` - After executing this command, the player can right-click the holographic statistic to remove it