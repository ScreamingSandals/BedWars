# Upgrades

Upgrades allow teams to enhance specific in-game elements, giving them a competitive advantage.

!!! warning "Available upgrades"
    
    The base plugin includes only a spawner resource quantity upgrade.  
    To access additional upgrades, you will need an addon.  
    We recommend checking out [SBA](https://www.spigotmc.org/resources/sba-screaming-bedwars-addon-1-8-8-1-20-1.99149/).


## Spawner Resource Quantity Upgrade

This upgrade improves team spawners by increasing their level, which affects the number of resources generated per spawn.

The upgrade is configured under the `upgrade` -> `entities` section in the shop item definition.  
You can define multiple upgrades here, as the section accepts a list of entities.

### Configuration Fields

For this type of upgrade, the `type` must be set to `spawner`.

* **`add-levels`**  
  The number of levels to add when the upgrade is purchased once.  
  This value can be fractional, e.g. `0.2` means a 20% chance for an extra resource per spawn.  
  On the fifth purchase, this would guarantee one additional resource per spawn.
  By default, spawners start at level `1`, though this can be customized for individual spawners.  
  See [Arena Editing Commands](commands.md#arena-editing) (`/bw admin <arena> spawner add <type> [holo enabled] [start level] [name] [team]`).
* **`max-level`** (optional)  
  The maximum level a spawner can reach through upgrades. Once reached, the upgrade becomes unavailable.  
* **`shop-name`** (optional)  
  The name displayed in chat when the upgrade is purchased. Defaults to `UPGRADE`.
* **`notify-team`** (optional)  
  If `true`, all team members will be notified when the upgrade is purchased.


### Targeting Spawners

There are three ways to specify which spawners the upgrade affects:

1. **Using named spawners**
     * Assign a name when creating the spawner.
     * Use the `spawner-name` field to target it.
     * If multiple spawners share the same name, they will all be upgraded.
2. **Using type**
     * When creating a spawner, set its `team`.
     * Use `spawner-type` to define which spawner type to upgrade.
     * All matching spawners of that type belonging to the team purchasing the upgrade will be upgraded.
3. **Using all linked spawners**
     * Link the spawners as in the previous mode.
     * Set `team-upgrade` to `true`.
     * All team-linked spawners will be upgraded, regardless of name or type.

### Example

Below is an example of how this upgrade might be defined in your shop configuration:

```yaml
- price: 1
  price-type: bronze
  upgrade:
    entities:
    - type: spawner
      spawner-type: bronze
      add-levels: 0.2
      max-level: 2
      shop-name: "Bronze upgrade of the team %team%"
      notify-team: true
  stack:
    type: diamond_block
    amount: 1
    display-name: "Bronze upgrade of the team %team%"
```
