# Upgrades

## Spawner resource quantity upgrade
This is how it should look in you shop.yml file:

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

Let's get into it.

* You **must** define the variables `upgrade`, `entities` and `type: spawner`. If any of these will be missing, upgrades won't work.
* We got these spawner modes: `spawner-name`, `spawner-type` and `team-upgrade`
* `add-levels` parameter defines how much levels we are going to add. (Default start level for the spawner is 1)
* `max-level` defines the maximum level you can upgrade to. (default is no limit)
* `shop-name` is displayed after you buy the upgrade. That's all.
* `notify-team` if this is true, information will be send to all the players of the team.


### Modes

* `spawner-name`: You need to have your spawner name defined in arena.yml file or while creating arena (`/bw admin arenaName spawner add <spawnerType> <holoEnabled> <startLevel (default: 1)> <customName> <team>`). 
* `spawner-type` & `team-upgrade`: You need to have team defined with the spawner.
