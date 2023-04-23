# Shop

Screaming BedWars plugin uses our own lib called SimpleInventories to create and render inventory-based guis, and therefore shops. This article will show you some basics of this format. Check [this wiki](https://github.com/ScreamingSandals/SimpleInventories/wiki) for more advanced variables.

## Creating a new item

To create a new item, first you need to know internal item's name. You can either use Minecraft Wiki or [this page](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html) to get the name. If you use Minecraft Wiki, the name will usually start with `minecraft:`, this part can be omitted from the final name and for compatibility reasons it is better to omit it (so `minecraft:stone` will be just `stone` in the shop file). 

You can also use modern names in legacy environments (1.8.8-1.12.2). If the modern name does not work, check [this page](https://helpch.at/docs/1.12.2/org/bukkit/Material.html) for old names. Old names are deprecated and they may not be supported in future releases of BedWars.

There are two supported formats of items. We call them `short stack` and `long stack`.

### Using short stack

This format can describe only material name, amount, display name and lore. Except for material name, every part is optional. Each part is divided using semicolon.

```yaml
items:
- stone
- dirt;2
- diamond_pickaxe;;Super Sword
- tnt;3;Trinitrotoluene;Does explode
```

To specify price and make the item buyable, suffix this format with `for <amount> <resource>`:

```yaml
items:
- stone for 1 bronze
- dirt;2 for 3 iron
- diamond_pickaxe;;Super Sword for 5 gold
- tnt;3;Trinitrotoluene;Does explode for 9 iron
```

In order to be able to specify other attributes (for example `properties`), we have to convert this from string to map. This is done by splitting the string into variables `stack` and `price`:

```yaml
items:
- stack: stone
  price: 1 bronze
- stack: dirt;2
  price: 3 iron
- stack: diamond_pickaxe;;Super Pickaxe
  price: 5 gold
- stack: tnt;3;Trinitrotoluene;Does explode
  price: 9 iron
```

### Using long stack

Long stack allows you to create items with enchantments and other attributes. Let's see the previous example rewritten in this format:

```yaml
items:
- stack: 
    type: stone
  price: 1 bronze
- stack: 
    type: dirt
    amount: 2
  price: 3 iron
- stack: 
    type: diamond_pickaxe
    display-name: Super Pickaxe
  price: 5 gold
- stack: 
    type: tnt
    amount: 3
    display-name: Trinitrotoluene
    lore:
    - Does explode
  price: 9 iron
```

As we can see, the `stack` attribute is now map, which allows more attributes to be present. For example, we can enchant our Super Pickaxe with Fortune III.

```yaml
- stack: 
    type: diamond_pickaxe
    display-name: Super Pickaxe
    enchants:
      fortune: 3
  price: 5 gold
```

The enchantment names can be found [here](https://www.digminecraft.com/lists/enchantment_list_pc.php). You can also use [Bukkit names](https://hub.spigotmc.org/javadocs/spigot/org/bukkit/enchantments/Enchantment.html) to specify enchants.

For list of all available options visit [this page](https://github.com/ScreamingSandals/SimpleInventories/wiki/Variable:-stack).

## Using item as a category

You can use any item as a category (even if the item is in another category). Players can then click on that item to open the category. The format is very similar, the only thing you have to specify is a list called `items`, where you put items of that specific category. Format of items in `items` is same as format in the base `data` list.

```yaml
- stack: 
    type: diamond_pickaxe
    display-name: Super Pickaxes
    lore:
     - You will never mine with anything else!
  items:
    - wooden_pickaxe for 10 gold
    - golden_pickaxe for 20 gold
```
