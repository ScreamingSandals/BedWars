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

package org.screamingsandals.bedwars.api.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.events.OpenShopEvent;
import org.screamingsandals.bedwars.api.player.BWPlayer;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface StoreManager {
    /**
     * Opens the default store using the built-in shop system. It does not fire any event.
     * <p>
     * The player should be in a game, otherwise the behaviour of this method is undefined.
     * This method does not verify whether the player is playing BedWars.
     * <p>
     * Unless you have a specific reason, you should use {@link #tryOpenDefaultStore(BWPlayer)}.
     *
     * @param player the player
     * @since 0.3.0
     */
    default void openDefaultStore(@NotNull BWPlayer player) {
        openCustomStore(player, null);
    }

    /**
     * Opens a custom store using the built-in shop system. It does not fire any event.
     * <p>
     * The player should be in a game, otherwise the behaviour of this method is undefined.
     * This method does not verify whether the player is playing BedWars.
     * <p>
     * Unless you have a specific reason, you should use {@link #tryOpenCustomStore(BWPlayer, String)}.
     *
     * @param player the player
     * @param fileName the file name
     * @see #tryOpenCustomStore(BWPlayer, String)
     * @since 0.3.0
     */
    void openCustomStore(@NotNull BWPlayer player, @Nullable String fileName);

    /**
     * Tries opening a store using the provided GameStore instance.
     * <p>
     * It fires the {@link org.screamingsandals.bedwars.api.events.OpenShopEvent}, giving plugins the ability to
     * cancel the request or replace the shop system.
     * <p>
     * The player must be in a game, otherwise this method simply returns null.
     *
     * @param player the player
     * @param gameStore the game store
     * @return result of the fired event or null on failure
     * @since 0.3.0
     */
    OpenShopEvent.@Nullable Result tryOpenStore(@NotNull BWPlayer player, OpenShopEvent.@NotNull StoreLike gameStore);

    /**
     * Tries opening a default store.
     * <p>
     * It fires the {@link OpenShopEvent}, giving plugins the ability to
     * cancel the request or replace the shop system.
     * <p>
     * The player must be in a game, otherwise this method simply returns null.
     *
     * @param player the player
     * @return result of the fired event or null on failure
     * @since 0.3.0
     */
    default OpenShopEvent.@Nullable Result tryOpenDefaultStore(@NotNull BWPlayer player) {
        return tryOpenCustomStore(player, null);
    }

    /**
     * Tries opening a custom store.
     * <p>
     * It fires the {@link OpenShopEvent}, giving plugins the ability to
     * cancel the request or replace the shop system.
     * <p>
     * The player must be in a game, otherwise this method simply returns null.
     *
     * @param player the player
     * @param fileName the file name
     * @return result of the fired event or null on failure
     * @since 0.3.0
     */
    OpenShopEvent.@Nullable Result tryOpenCustomStore(@NotNull BWPlayer player, @Nullable String fileName);
}
