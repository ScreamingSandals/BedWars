/*
 * Copyright (C) 2025 ScreamingSandals
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

package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.LocalGame;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.lib.api.types.server.EntityHolder;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface OpenShopEvent extends BWCancellable {

    LocalGame getGame();

    @Nullable EntityHolder getEntity();

    BWPlayer getPlayer();

    /**
     * An object representing an opened store. Can be an instance of {@link GameStore} if opened using an entity.
     *
     * @return opened store
     * @since 0.3.0
     */
    @NotNull StoreLike getGameStore();

    Result getResult();

    void setResult(Result result);

    @Deprecated
    @Override
    default boolean isCancelled() {
        return getResult() != Result.ALLOW;
    }

    @Deprecated
    @Override
    default void setCancelled(boolean cancelled) {
        setResult(cancelled ? Result.DISALLOW_UNKNOWN : Result.ALLOW);
    }

    /**
     * An interface representing a store.
     *
     * @since 0.3.0
     */
    interface StoreLike {
        /**
         * @return shop file
         * @since 0.3.0
         */
        @Nullable String getShopFile();

        /**
         * @return team linked to the store or null
         * @since 0.3.0
         */
        @Nullable Team getTeam();
    }

    enum Result {
        ALLOW,
        DISALLOW_THIRD_PARTY_SHOP,
        DISALLOW_LOCKED_FOR_THIS_PLAYER,
        DISALLOW_UNKNOWN,
        DISALLOW_WRONG_TEAM;
    }

    static void handle(Object plugin, Consumer<OpenShopEvent> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, OpenShopEvent.class, consumer);
    }
}
