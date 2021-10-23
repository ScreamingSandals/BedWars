# How to make an arena

## Step 1 - Get maps

Firstly, get a map which you can use to play BedWars on. Don't have any maps? [Click me](https://www.planetminecraft.com/projects/tag/bedwars/) for some maps.

## step 2 - Add the arena

Create the arena by doing `/bw admin <arena name> add`

## Step 3 - Set the position

Set the positions of the arena. This is similar to using WorldEdit.
To set pos1, do `/bw admin <arena name> pos1`. To set pos2, do `/bw admin <arena name> pos2`.
pos1 is the bottom corner of the arena and pos2 is the top corner of the arena.

<img alt="Arena bounds" src="../../assets/images/arena.png" width="200"/>

## Step 4 - Add teams

Now it's time to add the teams. To add a team, do `/bw admin <arena> team add <team name> <team colour> <team size>`.

Available arguments - colors:
`RED`,
`BLUE`,
`GREEN`,
`YELLOW`,
`MAGENTA`,
`PINK`,
`LIME`,
`BLACK`,
`WHITE`,
`ORANGE`,
`LIGHT_GRAY`,
`GRAY`,
`LIGHT_BLUE`,
`CYAN`,
`BROWN`

!!! warning

    You must create at least 2 teams.

## Step 5 - Set the team spawn

Stand where you would like to have the team spawn. Also look at the direction as the yaw will also be taken into account. Do ``/bw admin <arena> team spawn <team>``.

## Step 6 - Set the team beds

Stand on top of the bed and look down on the HEAD of the bed (see image below). Do `/bw admin <arena name> team bed <team name>`

<img alt="Bed hed" src="../../assets/images/bed.png" width="200"/>

## Step 7 - Add the generators

Go to the place you would like to add a generator at. Stand there and execute this command: `/bw admin <arena name> spawner add <resource> <true/false>`.

Valid default resources: `bronze`, `iron` and `gold`.

The **true/false** part of the command means if there should **be** a hologram (true) or if there should be **no** hologram (false).

These resources can be changed by going to the `config.yml`. click [here](../config.md) for more.

## Step 8 - Add the shops

Now it's time to add the shops. Stand where you would like your shop entity to be and look forward. Do the following command: `/bw admin <arena> store add [name of villager entity] [file with shop] [use main shop]`

Example: `/bw admin <arena> store add &aStore shop.yml false` or just `/bw admin <arena> store add &aStore`

!!! note

    If you would like to have a different type of store, do the following:  
    `/bw admin <arena> store type <living entity>`  
    This sets entity type of store (Villager, Horse, Cow, etc.).  
    If you would like to have a player with a skin as the shop keeper, use this command: `/bw admin <arena> store type Player:skinname`.

## Step 9 - Final steps

Add the lobby location for the arena by doing `/bw admin <arena> lobby`.

Add the spectator location for the arena by doing `/bw admin <arena> spec`.

Last but not least, remember to save the arena with `/bw admin <arena> save`.