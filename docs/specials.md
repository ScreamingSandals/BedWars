# Specials

This page is not finished yet, and does not contain information about all special items.

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