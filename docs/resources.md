# How to add custom resources

Open the config (`plugins/BedWars/config.yml`) and go down, until you find a section called `resources`. Using `Ctrl+F` helps you to search for it.

It’s easy to add a new resource into your game/server. Here is an example on how to add `emeralds`, `diamonds`, `iron` and `gold`.

```yaml
resources:
  emerald:
    material: EMERALD
    color: GREEN
    name: Emerald
    interval: 60
    translate: resource_emerald
    spread: 1.0
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
    interval: 2.5
    translate: resource_iron
    spread: 1.0
  gold:
    material: GOLD_INGOT
    color: GOLD
    name: Gold
    interval: 8
    translate: res
```


`material:` This is the material which is going to be used to represent the resource. e.g `APPLE`.

`color:` This is the color of the text of the material. e.g `RED`. Valid colors are available [here](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/ChatColor.html).

`name:` This is the name of the material in-game. e.g `Nobba Coin`.

`interval:` This is the amount of seconds that it takes to spawn in the resource. e.g `5`. every 5 seconds it will spawn that resource.

`translate:` This is the translate key that is located in the language file.

`spread:` This is the spread of the material. e.g `1` spread is going to make it only spawn in a 1x1 area. If the spread is `3`, then its going to spawn in a 3x3 area