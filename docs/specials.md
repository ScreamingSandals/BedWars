# Specials

Special items can be sold in the shop and they have some special behaviour when used. For example, they spawn entity, reduce knockback, gives someone potion effects etc.

Addons can also add new special items.

Every special item in shop is configured using `properties`.

## Arrow Blocker

Arrow Blocker is an item which allows you to block arrows when used for a certain period of time.

There are two options for an arrow blocker:
* `protection-time` - A period of time (in seconds) for which a player cannot be damaged using projectiles.
* `delay` - The delay (in seconds) after usage for which the player cannot use another arrow blocker.  

Here is an example of the arrow blocker configured in shop:

```yaml
- price: 5 of gold
  properties:
  - name: "ArrowBlocker"
    protection-time: 10
    delay: 5
  stack:
    type: ENDER_EYE
    display-name: "Arrow Blocker"
    lore:
      - "Block arrows that are coming"
      - "for you with black magic."
      - "I mean, with this item."
```

Global configuration in `config.yml` can also be used instead of local configuration. The global configuration in `config.yml` looks like this:

```yaml
specials:
  arrow-blocker:
    protection-time: 10
    delay: 5
```

If you want to use the global configuration, you simply do not specify the options in the shop:

```yaml
- price: 5 of gold
  properties: "ArrowBlocker"
  stack:
    type: ENDER_EYE
    display-name: "Arrow Blocker"
    lore:
      - "Block arrows that are coming"
      - "for you with black magic."
      - "I mean, with this item."
```



## Auto Ignitable TNT

TODO: docs

## Golem

TODO: docs

## Lucky Block

Lucky Block is a special item inspired by [LuckyBlock Addon for BedWarsRel](https://www.spigotmc.org/resources/bedwarsrel-luckyblock-addon.8268/).
This block can be placed anywhere and when broken, a random configured event will occur. Lucky blocks are not in the shop by default; therefore, 
you have to add them to the shop by yourself.

Like other special items, lucky blocks use `properties` for their configuration. The property for these blocks is simply called `LuckyBlock` and have `data` section. This section is a list of events that may occur. 

There are 5 event types:  

* `nothing` - As the name implies, it does nothing.
* `teleport` - This event teleports the player relatively on Y-axis and a variable `height` is used to configure this relative teleportation. For example, if player is in height 60 and the configured height is 50, he will be teleported to Y 110.
* `tnt`- A primed TNT entity is spawned when this event is chosen. This TNT explodes immediately.
* `potion` - This event gives player a potion effect. The effect is configured using a variable `effect`.
* `item` - This event gives player an item. This item is configured using a variable `stack`.

Below is an example of fully-configured lucky block with all these events used. Additionally, each event can have a variable `message`. This message will then be send to the player who broke the block. 


!!! warning

    Unlike the rest of the plugin, lucky blocks still use Bukkit's ConfigurationSerializable format for items and potion effects. This is subject to change at any time.


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
      stack:
        ==: org.bukkit.inventory.ItemStack
        type: DIRT
        v: 1519
      message: "This sends you message, you can add it to all of these types."
    - type: potion
      effect:
        ==: org.bukkit.potion.PotionEffect
        effect: 15
        amplifier: 2
        duration: 100
        ambient: true
        has-particles: true
        has-icon: true
  stack: SPONGE;1;Lucky Block
```

## Magnet Shoes

TODO: docs

## Protection Wall

TODO: docs

## Rescue Platform

TODO: docs

## Magnet Shoes

TODO: docs

## TNT Sheep

TODO: docs

## Team Chest

TODO: docs

## Throwable Fireball

TODO: docs

## Tracker

TODO: docs

## Trap

TODO: docs

## Warp Powder

This special item teleports you to the team spawn when used. You have to wait few seconds and you must not move, otherwise you would cancel the teleportation.

There are two options for warp powder:

* `teleport-time` - Cooldown (in seconds) before the player gets teleported.
* `delay`- The delay (in seconds) after usage for which the player cannot use another warp powder. 

Here is an example of warp powder configured in shop:

```yaml
- price: 7 of iron
  properties:
  - name: "WarpPowder"
    teleport-time: 6
    delay: 0
  stack:
    type: GUNPOWDER
    display-name: "Warp Powder"
    lore:
      - "When using this powder you'll get"
      - "teleported to you spawn point within 6 seconds"
      - "Warning: Any movement will stop the process"
```

Global configuration in `config.yml` can also be used instead of local configuration. The global configuration in `config.yml` looks like this:

```yaml
specials:
  warp-powder:
    teleport-time: 6
    delay: 0
```

If you want to use the global configuration, you simply do not specify the options in the shop:

```yaml
- price: 7 of iron
  properties: "WarpPowder"
  stack:
    type: GUNPOWDER
    display-name: "Warp Powder"
    lore:
      - "When using this powder you'll get"
      - "teleported to you spawn point within 6 seconds"
      - "Warning: Any movement will stop the process"
```