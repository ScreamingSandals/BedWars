package org.screamingsandals.bedwars.api.game;

import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 */
public interface ItemSpawner<E extends Wrapper, I extends ItemSpawnerType<?,?,?>, T extends Team<?>> extends Upgrade {
    /**
     * @return
     */
    I getItemSpawnerType();

    /**
     * @return
     */
    E getLocation();

    /**
     * @return
     */
    @Nullable
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
    boolean isHologramEnabled();

    /**
     * @return
     */
    boolean isFloatingBlockEnabled();

    /**
     * Sets team of this upgrade
     *
     * @param team current team
     */
    void setTeam(T team);

    /**
     *
     * @return registered team for this upgrade in optional or empty optional
     */
    T getTeam();

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
