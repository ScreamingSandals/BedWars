# Making an Arena

!!! warning "World"
    
    This guide assumes you already have a world with your BedWars map.
    You should load it using a plugin like Multiverse or SlimeWorldManager, and you should already be inside that world.

## Creating the Arena

To create the arena, run this command:  
`/bw admin <arena name> add`

Replace `<arena name>` with the name you want for your arena. Each arena must have a unique name.

## Setting Arena Positions

First, go to one corner of your map and run: `/bw admin <arena name> pos1`    
Then go to the diagonally opposite corner and run: `/bw admin <arena name> pos2`

!!! note "Positions"
    
    These two points define a box around your arena.
    It is like setting a region with WorldEdit.

<img alt="Arena bounds" src="../assets/arena_bounds.png" width="400"/>

## Adding Teams

Now it is time to add the teams. To add a team, use this command:  
`/bw admin <arena name> team add <team name> <team color> <team size>`

Example: `/bw admin MyArena team add Red RED 2`

You can use these colors: `RED`, `BLUE`, `GREEN`, `YELLOW`, `MAGENTA`, `PINK`, `LIME`, `BLACK`, `WHITE`, `ORANGE`, `LIGHT_GRAY`, `GRAY`, `LIGHT_BLUE`, `CYAN` and `BROWN`.

!!! warning

    You must add **at least 2 teams** for the arena to work.

### Setting Team Spawns

Go to the place where players from a team should appear when they spawn.
Look in the direction you want players to look when they spawn, then run:  
 `/bw admin <arena name> team spawn <team name>`

 Repeat this for every team.

### Setting Team Beds

Look at the **head** of the team's bed (the side with the pillow), and run:  
 `/bw admin <arena name> team bed <team name>`

  Repeat this for every team.

!!! tip "Target blocks"

    Beds are just one of several target block options. You can also use blocks like the **Dragon Egg**, **Cake**, or **Respawn Anchor** to create alternative modes (EggWars, CakeWars, AnchorWars). Most of the vanilla blocks are supported, with the exception of container-like blocks and complex block entities.

## Adding Spawners (Resource Generators)

Stand where you want the spawner (also known as resource generator) to be and run:  
`/bw admin <arena name> spawner add <resource> <true/false>`

Default supported resources: `bronze`, `iron` and `gold`. For additional resources like `diamond` or `emerald`, you need to configure them manually in your [config file](config.md).

The `true/false` parameter controls whether a hologram is shown above the spawner.

## Adding Merchants

To add shop entities, stand where you want the shop to appear, face the direction you want the shopkeeper to look, and run:  
 `/bw admin <arena name> store add [name above shopkeeper's head] [file with shop]`

Example:  
`/bw admin <arena name> store add &aStore shop.yml`  
`/bw admin <arena name> store add &aStore`  
`/bw admin <arena name> store add`  

!!! info "Custom Shop Entities"

    To change the shop entity type, use:

    `/bw admin <arena name> store type <entity type>`

    Example:

    `/bw admin <arena name> store type Villager`

    To use a player skin:

    `/bw admin <arena name> store type Player:skinname`

    Citizens plugin must be installed to use a player skin.

## Final Steps

Set the lobby and spectator locations:

* `/bw admin <arena name> lobby`
* `/bw admin <arena name> spec`

**Finally, don’t forget to save the arena:**  
`/bw admin <arena name> save`

!!! note "Edit Mode"

    After saving the arena, you can switch to **edit mode** to make changes by running:

    `/bw admin <arena> edit`

    When you are done editing the arena, save it.

## Join Signs

To create a join sign, place a sign with the following content:

- **Line 1**: `[BedWars]` or `[BWGame]`
- **Line 2**: Name of the arena

After placing it, the plugin will update the sign.

To create a **leave sign**, simply write `leave` instead of an arena name.

### Team Entities

Team entities allow players to pick their team by right-clicking on a specific entity.

There are two ways to set this up:

### Using `jointeam` command

1. Go to your BedWars lobby and place the entity (e.g., Armor Stand).
2. Switch the arena to edit mode.
3. Run:  
   `/bw admin <arena name> jointeam <team name>`
4. Right-click the entity to bind it to the team.
    * Armor Stands will automatically receive leather armor in the team’s color.
    * The entity is made persistent automatically.

Save the arena afterward.

### Manual Creation

Place any **living entity** in your lobby with a **custom name matching the team name** (e.g., `Red`), and the plugin will recognize it as a team selector.