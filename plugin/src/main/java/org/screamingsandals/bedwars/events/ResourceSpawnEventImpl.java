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

package org.screamingsandals.bedwars.events;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.events.ResourceSpawnEvent;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.ItemSpawnerImpl;
import org.screamingsandals.bedwars.game.ItemSpawnerTypeImpl;
import org.screamingsandals.lib.event.SCancellableEvent;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.world.LocationHolder;

@Data
public class ResourceSpawnEventImpl implements ResourceSpawnEvent<GameImpl, ItemSpawnerImpl, ItemSpawnerTypeImpl, Item, LocationHolder>, SCancellableEvent {
    private final GameImpl game;
    private final ItemSpawnerImpl itemSpawner;
    private final ItemSpawnerTypeImpl type;
    @NotNull
    private Item resource;
    private boolean cancelled;

    @Override
    public LocationHolder getLocation() {
        return itemSpawner.getLocation();
    }

    @Override
    public void setResource(Object resource) {
        if (resource instanceof Item) {
            this.resource = (Item) resource;
        } else {
            this.resource = ItemFactory.build(resource).orElseThrow();
        }
    }

    @Override
    public boolean cancelled() {
        return cancelled;
    }

    @Override
    public void cancelled(boolean cancel) {
        cancelled = cancel;
    }
}
