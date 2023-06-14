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

package org.screamingsandals.bedwars.player;

import lombok.Getter;
import lombok.Setter;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.item.meta.PotionEffectHolder;
import org.screamingsandals.lib.player.gamemode.GameModeHolder;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.world.LocationHolder;

import java.util.Collection;

@Getter
@Setter
public class StoredInventory {
    private Item[] armor;
    private Component displayName;
    private Collection<PotionEffectHolder> effects;
    private int foodLevel = 0;
    private Item[] inventory;
    private LocationHolder leftLocation;
    private int level = 0;
    private Component listName;
    private GameModeHolder mode;
    private float xp;
    private Object platformScoreboard;
}
