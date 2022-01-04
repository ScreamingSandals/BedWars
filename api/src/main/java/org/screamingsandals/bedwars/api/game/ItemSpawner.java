/*
 * Copyright (C) 2022 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.api.game;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.lib.utils.Wrapper;

/**
 * @author Bedwars Team
 */
@ApiStatus.NonExtendable
public interface ItemSpawner<E extends Wrapper, I extends ItemSpawnerType<?,?,?>, T extends Team<?,?,?,?,?>> extends Upgrade {
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
        CERTAIN_POPULAR_SERVER
    }
}
