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
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.item.meta.PotionEffect;
import org.screamingsandals.lib.player.gamemode.GameMode;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.world.Location;

import java.util.Collection;

@Getter
@Setter
public class StoredInventory {
    private ItemStack[] armor;
    private Component displayName;
    private Collection<PotionEffect> effects;
    private int foodLevel = 0;
    private ItemStack[] inventory;
    private Location leftLocation;
    private int level = 0;
    private Component listName;
    private GameMode mode;
    private float xp;
    private Object platformScoreboard;
}
