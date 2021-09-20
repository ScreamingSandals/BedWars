package org.screamingsandals.bedwars.api.upgrades;

import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.ItemSpawner;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Bedwars Team
 */
public final class UpgradeStorage {
    private final String upgradeName;
    private final Class<? extends Upgrade> upgradeClass;

    private final Map<Game<?,?,?,?,?,?,?,?,?>, List<Upgrade>> upgradeRegistry = new HashMap<>();

    /**
     * @param upgradeName  Upgrade Name
     * @param upgradeClass Upgrade Class
     */
    UpgradeStorage(String upgradeName, Class<? extends Upgrade> upgradeClass) {
        this.upgradeName = upgradeName;
        this.upgradeClass = upgradeClass;
    }

    /**
     * @return upgrade name
     */
    public String getUpgradeName() {
        return upgradeName;
    }

    /**
     * @return upgrade class type
     */
    public Class<? extends Upgrade> getUpgradeClass() {
        return upgradeClass;
    }

    /**
     * Register active upgrade in game
     *
     * @param game    Game
     * @param upgrade Upgrade
     */
    public void addUpgrade(Game<?,?,?,?,?,?,?,?,?> game, Upgrade upgrade) {
        if (!upgradeClass.isInstance(upgrade)) {
            return;
        }

        if (!upgradeRegistry.containsKey(game)) {
            upgradeRegistry.put(game, new ArrayList<>());
        }
        if (!upgradeRegistry.get(game).contains(upgrade)) {
            upgrade.onUpgradeRegistered(game);
            BedwarsAPI.getInstance().getEventUtils().fireUpgradeRegisteredEvent(game, this, upgrade);
            upgradeRegistry.get(game).add(upgrade);
        }
    }

    /**
     * Unregister active upgrade
     *
     * @param game    Game
     * @param upgrade Upgrade
     */
    public void removeUpgrade(Game<?,?,?,?,?,?,?,?,?> game, Upgrade upgrade) {
        if (!upgradeClass.isInstance(upgrade)) {
            return;
        }

        if (upgradeRegistry.containsKey(game)) {
            if (upgradeRegistry.get(game).contains(upgrade)) {
                upgrade.onUpgradeUnregistered(game);
                BedwarsAPI.getInstance().getEventUtils().fireUpgradeUnregisteredEvent(game, this, upgrade);
                upgradeRegistry.get(game).remove(upgrade);
            }
        }
    }

    /**
     * @param game    Game
     * @param upgrade Upgrade
     * @return true if upgrade is registered
     */
    public boolean isUpgradeRegistered(Game<?,?,?,?,?,?,?,?,?> game, Upgrade upgrade) {
        if (!upgradeClass.isInstance(upgrade)) {
            return false;
        }

        return upgradeRegistry.containsKey(game) && upgradeRegistry.get(game).contains(upgrade);
    }

    /**
     * This is automatically used while game is ending
     *
     * @param game Game
     */
    public void resetUpgradesForGame(Game<?,?,?,?,?,?,?,?,?> game) {
        if (upgradeRegistry.containsKey(game)) {
            for (Upgrade upgrade : upgradeRegistry.get(game)) {
                upgrade.onUpgradeUnregistered(game);
                BedwarsAPI.getInstance().getEventUtils().fireUpgradeUnregisteredEvent(game, this, upgrade);
            }
            upgradeRegistry.get(game).clear();
            upgradeRegistry.remove(game);
        }
    }

    /**
     * Get all upgrades of this type that is registered in game as active
     *
     * @param game Game
     * @return ĺist of registered upgrades of game
     */
    public List<Upgrade> getAllUpgradesOfGame(Game<?,?,?,?,?,?,?,?,?> game) {
        List<Upgrade> upgrade = new ArrayList<>();
        if (upgradeRegistry.containsKey(game)) {
            upgrade.addAll(upgradeRegistry.get(game));
        }
        return upgrade;
    }

    /**
     * Find active upgrades with this instanceName
     *
     * @param game         Game
     * @param instanceName name of spawner
     * @return list of upgrades with same name
     */
    @Deprecated
    public List<Upgrade> findUpgradeByName(Game<?,?,?,?,?,?,?,?,?> game, String instanceName) {
        List<Upgrade> upgrades = new ArrayList<>();

        if (upgradeRegistry.containsKey(game)) {
            for (Upgrade upgrade : upgradeRegistry.get(game)) {
                if (instanceName.equals(upgrade.getInstanceName())) {
                    upgrades.add(upgrade);
                }
            }
        }

        return upgrades;
    }

    public List<Upgrade> findItemSpawnerUpgrades(Game<?,?,?,?,?,?,?,?,?> game, String spawnerInstanceName) {
        List<Upgrade> upgrades = new ArrayList<>();

        if (upgradeRegistry.containsKey(game)) {
            for (Upgrade upgrade : upgradeRegistry.get(game)) {
                if (upgrade instanceof ItemSpawner) {
                    var itemSpawner = (ItemSpawner<?,?,?>) upgrade;

                    if (spawnerInstanceName.equals(itemSpawner.getInstanceName())) {
                        upgrades.add(itemSpawner);
                    }
                }
            }
        }
        return upgrades;
    }

    public List<Upgrade> findItemSpawnerUpgrades(Game<?,?,?,?,?,?,?,?,?> game, Team<?,?,?,?,?> team) {
        List<Upgrade> upgrades = new ArrayList<>();

        if (upgradeRegistry.containsKey(game)) {
            for (Upgrade upgrade : upgradeRegistry.get(game)) {
                if (upgrade instanceof ItemSpawner) {
                    var itemSpawner = (ItemSpawner<?,?,?>) upgrade;
                    if (itemSpawner.getTeam() == null) {
                        continue;
                    }

                    final var name = itemSpawner.getTeam();
                    if (name != null && team.getName().equals(name.getName())) {
                        upgrades.add(upgrade);
                    }
                }
            }
        }
        return upgrades;
    }

    public List<Upgrade> findItemSpawnerUpgrades(Game<?,?,?,?,?,?,?,?,?> game, Team<?,?,?,?,?> team, ItemSpawnerType<?,?,?> itemSpawnerType) {
        List<Upgrade> upgrades = new ArrayList<>();

        if (upgradeRegistry.containsKey(game)) {
            for (Upgrade upgrade : upgradeRegistry.get(game)) {
                if (upgrade instanceof ItemSpawner) {
                    var itemSpawner = (ItemSpawner<?,?,?>) upgrade;
                    if (itemSpawner.getTeam() == null) {
                        continue;
                    }

                    final var name = itemSpawner.getTeam();
                    if (name != null && team.getName().equals(name.getName()) && itemSpawnerType.getName().equals(itemSpawner.getItemSpawnerType().getName())) {
                        upgrades.add(upgrade);
                    }
                }
            }
        }
        return upgrades;
    }
}
