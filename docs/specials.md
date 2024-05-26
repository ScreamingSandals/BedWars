# Specials

Special items can be sold in the shop and have unique behaviors when used. For example, they can spawn entities, reduce knockback, gives potion effects, etc.

Addons can also introduce new special items.

Every special item in the shop is configured using `properties`.

## Arrow Blocker

The Arrow Blocker is an item that allows you to block arrows for a certain period of time when used.

There are two configurable options for an Arrow Blocker:

* `protection-time` - The duration (in seconds) during which a player cannot be damaged by projectiles.
* `delay` - The cooldown period (in seconds) after usage during which the player cannot use another Arrow Blocker.

Here is an example of the Arrow Blocker configured in the shop:

```yaml
- price: 5 of gold
  properties:
  - name: "ArrowBlocker"
    protection-time: 10
    delay: 5
  stack:
    type: ender_eye
    display-name: "Arrow Blocker"
    lore:
      - "Block arrows that are coming"
      - "for you with black magic."
      - "I mean, with this item."
```

A global configuration in `config.yml` can also be used instead of a local configuration. The global configuration in `config.yml` looks like this:

```yaml
specials:
  # ...
  arrow-blocker:
    protection-time: 10
    delay: 5
  # ...
```

To use the global configuration, simply omit the options you do not wish to override in the shop configuration. If there is a single property with only a name and no overrides, you can use the following syntax:

```yaml
- price: 5 of gold
  properties: "ArrowBlocker"
  stack:
    type: ender_eye
    display-name: "Arrow Blocker"
    lore:
      - "Block arrows that are coming"
      - "for you with black magic."
      - "I mean, with this item."
```

## Auto Ignitable TNT

The Auto Ignitable TNT is a special item that spawns ignited TNT when placed.

There are three configurable options for the Auto Ignitable TNT:

* `explosion-time` - The time (in seconds) after which the spawned TNT explodes.
* `damage-placer` - Whether or not the player who placed the TNT should also take damage.
* `damage` - The damage dealt by the TNT.

!!! note "TNT jumping"

    Addons like SBA provide additional customization to this item related to jumping. Please refer to their documentation for more details.

Here is an example of the Auto Ignitable TNT configured in the shop:

```yaml
- price: 3 of iron
  properties:
  - name: "AutoIgniteableTnt"
    explosion-time: 5
    damage-placer: false
    damage: 4.0
  stack:
    type: tnt
    display-name: "Autoigniteable TNT"
```

A global configuration in `config.yml` can also be used instead of a local configuration. The global configuration in `config.yml` looks like this:

```yaml
specials:
  # ...
  auto-igniteable-tnt:
    explosion-time: 5
    damage-placer: false
    damage: 4.0
  # ...
```

To use the global configuration, simply omit the options you do not wish to override in the shop configuration. If there is a single property with only a name and no overrides, you can use the following syntax:

```yaml
- price: 3 of iron
  properties: "AutoIgniteableTnt"
  stack:
    type: tnt
    display-name: "Autoigniteable TNT"
```

## Golem

The Golem is a special item that spawns a golem to attack enemy teams.

There are multiple configurable options for this special item:

* `speed` - The golem's speed.
* `follow-range` - The range within which the golem will follow enemies.
* `health` - The golem's health.
* `show-name` - Whether the golem's name should be displayed above its head.
* `delay` - The cooldown period before another golem can be spawned.
* `collidable` - Whether players collide with the golem.
* `name-format` - The format of the name shown above the golem's head. There are two placeholders: `%teamcolor%` for the team's color and `%team%` for the team's name.

Here is an example of the Golem configured in the shop:

```yaml
- price: 24 of iron
  properties:
  - name: "Golem"
    speed: 0.25
    follow-range: 16.0
    health: 20
    show-name: true
    delay: 0
    collidable: true
    name-format: "%teamcolor%%team% Golem"
  stack:
    type: ghast_spawn_egg
    display-name: "Golem"
    lore:
      - "An iron golem that will protect"
      - "your team from the enemies."
```

A global configuration in `config.yml` can also be used instead of a local configuration. The global configuration in `config.yml` looks like this:

```yaml
specials:
  golem:
    speed: 0.25
    follow-range: 16.0
    health: 20
    show-name: true
    delay: 0
    collidable: true
    name-format: "%teamcolor%%team% Golem"
```

To use the global configuration, simply omit the options you do not wish to override in the shop configuration. If there is a single property with only a name and no overrides, you can use the following syntax:

```yaml
- price: 24 of iron
  properties: "Golem"
  stack:
    type: ghast_spawn_egg
    display-name: "Golem"
    lore:
      - "An iron golem that will protect"
      - "your team from the enemies."
```

## Lucky Block

The Lucky Block is a special item inspired by the [LuckyBlock Addon for BedWarsRel](https://www.spigotmc.org/resources/bedwarsrel-luckyblock-addon.8268/).
This block can be placed anywhere, and when broken, a random configured event will occur. Lucky Blocks are not available in the shop by default; therefore, you must add them to the shop manually.

Like other special items, Lucky Blocks use `properties` for their configuration. The property for these blocks is called `LuckyBlock` and includes a `data` section. This section is a list of events that may occur.

There are 5 event types:  

* `nothing` - As the name implies, it does nothing.
* `teleport` - This event teleports the player along the Y-axis. The variable `height` is used to configure this relative teleportation. For example, if a player is at height 60 and the configured height is 50, they will be teleported to Y 110.
* `tnt`- A primed TNT entity is spawned when this event occurs, which explodes immediately.
* `potion` - This event gives the player a potion effect. The effect is configured using the variable `effect`.
* `item` - This event gives the player an item. This item is configured using the variable `stack`.

Below is an example of a fully configured Lucky Block with all these events included. Additionally, each event can have a variable `message`. This message will be sent to the player who broke the block.

```yaml
- price: 7 of iron
  properties:
  - name: "LuckyBlock"
    data:
    - type: tnt
    - type: nothing
    - type: teleport
      height: 50
    - type: item
      stack: dirt
      message: "This sends you message. You can add it to any event type."
    - type: potion
      effect:
        effect: blindness
        amplifier: 2
        duration: 100
        ambient: true
        particles: true
        icon: true
  stack:
    type: sponge
    amount: 1
    display-name: "Lucky Block"
```

Unlike other special items, there is no global configuration for Lucky Blocks.

## Magnet Shoes

Wearing this special item randomly absorbs all received knockback, giving you an advantage in PvP.

There is a single configurable option for this special item:

* `probability` - A number (0-100) representing the percentage chance that the shoes will absorb the knockback.

Here is an example of Magnet Shoes configured in the shop:

```yaml
- price: 6 of iron
  properties:
  - name: "MagnetShoes"
    probability: 75
  stack:
    type: iron_boots
    display-name: "Magnet-Shoes"
    lore:
      - "Wear those shoes and have a 75%"
      - "chance of getting no knockback!"
```

A global configuration in `config.yml` can also be used instead of a local configuration. The global configuration in `config.yml` looks like this:

```yaml
specials:
  # ...
  magnet-shoes:
    probability: 75
  # ...
```

To use the global configuration, simply omit the options you do not wish to override in the shop configuration. If there is a single property with only a name and no overrides, you can use the following syntax:

```yaml
- price: 6 of iron
  properties: "MagnetShoes"
  stack:
    type: iron_boots
    display-name: "Magnet-Shoes"
    lore:
      - "Wear those shoes and have a 75%"
      - "chance of getting no knockback!"
```

## Protection Wall

The Protection Wall is a special item that builds a wall when used.

There are several configurable options for this item:

* `is-breakable` - Whether it is possible to break the blocks of the wall.
* `delay` - The cooldown period before another protection wall can be built.
* `break-time` - The time (in seconds) after which the wall disappears. Can be set to `0` to disable the automatic breaking of the wall.
* `width` - The width (in blocks) of the wall. This must be an odd number; the plugin will print a warning and add an extra block if the number is even.
* `height` - The height (in blocks) of the wall.
* `distance` - The distance (in blocks) from the player activating the item to where the wall appears, in the direction the player is facing.
* `material` - The material used to build the wall. If the material is colorable, it will automatically be converted to the team's color.

Here is an example of a Protection Wall configured in the shop:

```yaml
- price: 64 of bronze
  properties:
  - name: "protectionwall"
    is-breakable: false
    delay: 20
    break-time: 0
    width: 5
    height: 3
    distance: 2
    material: cut_sandstone
  stack:
    type: bricks
    display-name: "Protection Wall"
    lore:
      - "Instantly builds a wall that"
      - "can save your life!"
```

A global configuration in `config.yml` can also be used instead of a local configuration. The global configuration in `config.yml` looks like this:

```yaml
specials:
  # ...
  protection-wall:
    is-breakable: false
    delay: 20
    break-time: 0
    width: 5
    height: 3
    distance: 2
    material: cut_sandstone
  # ...
```

To use the global configuration, simply omit the options you do not wish to override in the shop configuration. If there is a single property with only a name and no overrides, you can use the following syntax:

```yaml
- price: 64 of bronze
  properties: "protectionwall"
  stack:
    type: bricks
    display-name: "Protection Wall"
    lore:
      - "Instantly builds a wall that"
      - "can save your life!"
```

## Rescue Platform

The Rescue Platform is similar to the Protection Wall, but instead of wall it builds a platform that the player can use when falling to prevent fall damage or falling off the map.

There are several configurable options for this item:

* `is-breakable` - Determines whether it is possible to break blocks of the platform.
* `delay` - The cooldown period before another rescue platform can be built.
* `break-time` - The time (in seconds) after which the platform disappears. Can be set to `0` to disable the automatic breaking of the platform.
* `distance` - The distance (in blocks) below the player activating the item where the platform appears.
* `material` - The name of the material used to build the platform. If the material is colorable, it will automatically be converted to the team's color.

Here is an example of a Rescue Platform configured in the shop:

```yaml
- price: 64 of bronze
  properties:
  - name: "rescueplatform"
    is-breakable: false
    delay: 0
    break-time: 10
    distance: 1
    material: glass
  stack:
    type: blaze_rod
    display-name: "Rescue Platform"
    lore:
      - "Protect yourself from falling into"
      - "the void with a Rescue Platform."
      - "This is your last hope!"
```

A global configuration in `config.yml` can also be used instead of a local configuration. The global configuration in `config.yml` looks like this:

```yaml
specials:
  # ...
  rescue-platform:
    is-breakable: false
    delay: 0
    break-time: 10
    distance: 1
    material: glass
  # ...
```

To use the global configuration, simply omit the options you do not wish to override in the shop configuration. If there is a single property with only a name and no overrides, you can use the following syntax:

```yaml
- price: 64 of bronze
  properties: "rescueplatform"
  stack:
    type: blaze_rod
    display-name: "Rescue Platform"
    lore:
      - "Protect yourself from falling into"
      - "void with a Rescue Platform."
      - "This is your last hope!"
```

## Team Chest

Placing this item creates a chest accessible only by the placing team. All of these chests share the same inventory, similarly to an ender chest.

For legacy reasons, any ender chest in the shop is turned into a team chest regardless of the given properties. To disable this behavior, you have to set `specials.teamchest.turn-all-enderchests-to-teamchests` to `false` in `config.yml`.

Explicitly created team chests in the shop look like this:

```yaml
- price: 1 of gold
  properties: "teamchest"
  stack: ender_chest
```

!!! warning

    The type needs to be `ender_chest` for the item to work!

## Throwable Fireball

The Throwable Fireball represents a fireball that can be thrown from the hand of a player.

There are several configurable options for this item:

* `damage` - The amount of damage dealt by the fireball.
* `incendiary` - Determines whether the explosion causes fire.
* `damage-thrower` - Determines whether the fireball should damage its thrower if it explodes near them.

!!! note "Fireball jumping"

    Addons like SBA provide additional customization to this item related to jumping. Please refer to their documentation for more details.

Here is an example of a Throwable Fireball configured in the shop:

```yaml
- price: 40 of iron
  properties:
  - name: "ThrowableFireball"
    damage: 3.0
    incendiary: true
    damage-thrower: true
  stack:
    type: fire_charge
    display-name: "Fireball"
    lore:
      - "Is it a bird? Is it a plane?"
      - "By the time you know"
      - "you are dead!"
```

A global configuration in `config.yml` can also be used instead of a local configuration. The global configuration in `config.yml` looks like this:

```yaml
specials:
  # ...
  throwable-fireball:
    damage: 3.0
    incendiary: true
    damage-thrower: true
  # ...
```

To use the global configuration, simply omit the options you do not wish to override in the shop configuration. If there is a single property with only a name and no overrides, you can use the following syntax:

```yaml
- price: 40 of iron
  properties: "ThrowableFireball"
  stack:
    type: fire_charge
    display-name: "Fireball"
    lore:
      - "Is it a bird? Is it a plane?"
      - "By the time you know"
      - "you are dead!"
```

## TNT Sheep

The TNT Sheep is a special item that summons an explosive sheep, following enemy players. Upon summoning, it targets the closest enemy.

There are several configurable options for this special item:

* `speed` - The sheep's speed.
* `follow-range` - The range within which the sheep will follow enemies.
* `max-target-distance` - The maximum distance (in blocks) from which the initial target can be. The sheep does not spawn if there is no target within this distance.
* `explosion-time` - The time (in seconds) after which the TNT explodes and the sheep disappears.

Here is an example of the TNT Sheep configured in the shop:

```yaml
- price: 10 of gold
  properties:
  - name: "TNTSheep"
    speed: 0.25
    follow-range: 10.0
    max-target-distance: 32
    explosion-time: 8
  stack:
    type: sheep_spawn_egg
    display-name: "TNT Sheep"
    lore:
      - "Use the TNT-Sheep! It will"
      - "walk towards your closest enemy"
      - "and explode within 8 seconds!"
```

A global configuration in `config.yml` can also be used instead of a local configuration. The global configuration in `config.yml` looks like this:

```yaml
specials:
  # ...
  tnt-sheep:
    speed: 0.25
    follow-range: 10.0
    max-target-distance: 32
    explosion-time: 8
  # ...
```

To use the global configuration, simply omit the options you do not wish to override in the shop configuration. If there is a single property with only a name and no overrides, you can use the following syntax:

```yaml
- price: 10 of gold
  properties: "TNTSheep"
  stack:
    type: sheep_spawn_egg
    display-name: "TNT Sheep"
    lore:
      - "Use the TNT-Sheep! It will"
      - "walk towards your closest enemy"
      - "and explode within 8 seconds!"
```

## Tracker

The Tracker is a simple special item that displays the distance and direction of the nearest enemy. It is recommended for the Tracker to be a compass since the item sets the compass target of its user. Unlike other special items, this item is permanent, and you can use it repeatedly.

Here is an example of the Tracker configured in the shop:

```yaml
- price: 5 of iron
  properties: "Tracker"
  stack:
    type: compass
    display-name: "Tracker"
    lore:
      - "Wanna know where your closest target is?"
      - "Let's try this out!"
```

## Trap

A Trap is a placeable special item. When an enemy steps on a trap, all configured events will occur. Additionally, the team will be notified that someone has triggered their trap.

Unlike other special items, there is no global configuration for traps. The only option here is `data`, which is a list containing all the events.

There are 3 event types:

* `sound` - Plays a specified sound.
* `effect` - Applies a potion effect to the victim. The effect is configured using the variable `effect`.
* `damage` - Deals a specified amount of damage to the player.

Here is an example of a fully configured trap:

```yaml
- price: 3 of iron
  properties:
   - name: "Trap"
     data:
      - damage: 2.0
      - sound: ENTITY_SHEEP_AMBIENT
      - effect:
          effect: blindness
          amplifier: 2
          duration: 100
          ambient: true
          particles: true
          icon: true
      - effect:
          effect: weakness
          amplifier: 2
          duration: 100
          ambient: true
          particles: true
          icon: true
      - effect:
          effect: slowness
          amplifier: 2
          duration: 100
          ambient: true
          particles: true
          icon: true
  stack:
    type: string
    display-name: "Trap"
    lore:
      - "Get informed if an enemy steps on your trap"
      - "and your enemy won't be able to move properly."
```

## Warp Powder

The Warp Powder is a special item that teleports you to the team spawn when used. However, you must wait a few seconds without moving, otherwise, the teleportation will be canceled.

There are two configurable options for Warp Powder:

* `teleport-time` - The cooldown (in seconds) before the player gets teleported.
* `delay`- The cooldown period (in seconds) after usage during which the player cannot use another Warp Powder.

Here is an example of Warp Powder configured in the shop:

```yaml
- price: 7 of iron
  properties:
  - name: "WarpPowder"
    teleport-time: 6
    delay: 0
  stack:
    type: gunpowder
    display-name: "Warp Powder"
    lore:
      - "When using this powder you'll get"
      - "teleported to you spawn point within 6 seconds"
      - "Warning: Any movement will stop the process"
```

A global configuration in `config.yml` can also be used instead of a local configuration. The global configuration in `config.yml` looks like this:

```yaml
specials:
  # ...
  warp-powder:
    teleport-time: 6
    delay: 0
  # ...
```

To use the global configuration, simply omit the options you do not wish to override in the shop configuration. If there is a single property with only a name and no overrides, you can use the following syntax:

```yaml
- price: 7 of iron
  properties: "WarpPowder"
  stack:
    type: gunpowder
    display-name: "Warp Powder"
    lore:
      - "When using this powder you'll get"
      - "teleported to you spawn point within 6 seconds"
      - "Warning: Any movement will stop the process"
```