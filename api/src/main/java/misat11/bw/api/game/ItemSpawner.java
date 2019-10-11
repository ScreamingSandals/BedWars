package misat11.bw.api.game;

import misat11.bw.api.Team;
import misat11.bw.api.upgrades.Upgrade;
import org.bukkit.Location;

/**
 * @author Bedwars Team
 */
public interface ItemSpawner extends Upgrade {
    /**
     * @return
     */
    ItemSpawnerType getItemSpawnerType();

    /**
     * @return
     */
    Location getLocation();

    /**
     * @return
     */
    boolean hasCustomName();

    /**
     * @return
     */
    String getCustomName();

    /**
     * @return
     */
    double getStartLevel();

    /**
     * @return
     */
    double getCurrentLevel();

    /**
     * @return
     */
    boolean getHologramEnabled();

    /**
     * Sets team of this upgrade
     *
     * @param team current team
     */
    void setTeam(Team team);

    /**
     *
     * @return registered team for this upgrade
     */
    Team getTeam();

    /**
     * @param level
     */
    void setCurrentLevel(double level);

    default void addToCurrentLevel(double level) {
        setCurrentLevel(getCurrentLevel() + level);
    }

    default String getName() {
        return "spawner";
    }

    default String getInstanceName() {
        return getCustomName();
    }

    default double getLevel() {
        return getCurrentLevel();
    }

    default void setLevel(double level) {
        setCurrentLevel(level);
    }

    default void increaseLevel(double level) {
        addToCurrentLevel(level);
    }

    default double getInitialLevel() {
        return getStartLevel();
    }
}
