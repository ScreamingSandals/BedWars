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

package org.screamingsandals.bedwars.api.events;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.ItemSpawner;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface ResourceSpawnEvent<G extends Game, S extends ItemSpawner, T extends ItemSpawnerType, I extends Wrapper, L extends Wrapper> extends BWCancellable {
    G getGame();

    S getItemSpawner();

    L getLocation();

    I getResource();

    T getType();

    /**
     *
     * @param resource wrapper or platform ItemStack
     */
    void setResource(Object resource);

    @SuppressWarnings("unchecked")
    static void handle(Object plugin, Consumer<ResourceSpawnEvent<Game, ItemSpawner, ItemSpawnerType, Wrapper, Wrapper>> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, ResourceSpawnEvent.class, (Consumer) consumer);
    }
}
