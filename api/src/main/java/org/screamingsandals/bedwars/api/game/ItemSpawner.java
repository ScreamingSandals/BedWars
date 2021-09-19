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

    void setItemSpawnerType(I spawnerType);

    /**
     * @return
     */
    E getLocation();

    /**
     * @return
     */
    @Nullable
    String getCustomName();

    void setCustomName(@Nullable String customName);

    /**
     * @return
     */
    double getBaseAmountPerSpawn();

    void setBaseAmountPerSpawn(double baseAmountPerSpawn);

    /**
     * @return
     */
    double getAmountPerSpawn();

    /**
     * @return
     */
    boolean isHologramEnabled();

    void setHologramEnabled(boolean enabled);

    /**
     * @return
     */
    boolean isFloatingBlockEnabled();

    void setFloatingBlockEnabled(boolean enabled);

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
    void setAmountPerSpawn(double level);

    HologramType getHologramType();

    void setHologramType(HologramType type);

    int getTier();

    void setTier(int tier);

    long getIntervalTicks();

    void setIntervalTicks(long ticks);

    default void addToCurrentLevel(double level) {
        setAmountPerSpawn(getAmountPerSpawn() + level);
    }

    default String getName() {
        return "spawner";
    }

    default String getInstanceName() {
        return getCustomName();
    }

    default double getLevel() {
        return getAmountPerSpawn();
    }

    default void setLevel(double level) {
        setAmountPerSpawn(level);
    }

    default void increaseLevel(double level) {
        addToCurrentLevel(level);
    }

    default double getInitialLevel() {
        return getBaseAmountPerSpawn();
    }

    enum HologramType {
        /**
         * Defaults to the setting of the game
         */
        DEFAULT,
        SCREAMING,
        HYPIXEL;
    }
}
