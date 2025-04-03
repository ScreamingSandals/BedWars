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
import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.LocalGame;
import org.screamingsandals.bedwars.api.game.ItemSpawner;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.lib.api.types.server.ItemStackHolder;
import org.screamingsandals.lib.api.types.server.LocationHolder;

import java.util.function.Consumer;

@ApiStatus.NonExtendable
public interface ResourceSpawnEvent extends BWCancellable {
    LocalGame getGame();

    ItemSpawner getItemSpawner();

    LocationHolder getLocation();

    ItemStackHolder getResource();

    ItemSpawnerType getType();

    void setResource(ItemStackHolder resource);

    static void handle(Object plugin, Consumer<ResourceSpawnEvent> consumer) {
        BedwarsAPI.getInstance().getEventUtils().handle(plugin, ResourceSpawnEvent.class, consumer);
    }
}
