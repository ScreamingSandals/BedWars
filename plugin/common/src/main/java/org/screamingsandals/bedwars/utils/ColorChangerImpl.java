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

package org.screamingsandals.bedwars.utils;

import org.screamingsandals.bedwars.api.TeamColor;
import org.screamingsandals.bedwars.api.utils.ColorChanger;
import org.screamingsandals.bedwars.game.TeamColorImpl;
import org.screamingsandals.lib.api.types.server.ItemStackHolder;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Objects;

@Service
public class ColorChangerImpl implements ColorChanger {
    @Override
    public ItemStack applyColor(TeamColor apiColor, ItemStackHolder item) {
        if (item == null) {
            return ItemStackFactory.getAir();
        }
        var color = (TeamColorImpl) apiColor;
        var newItem = item.as(ItemStack.class);
        if (newItem.getType().is("leather_boots", "leather_chestplate", "leather_helmet", "leather_leggings")) {
            newItem = Objects.requireNonNull(newItem.builder().color(color.getLeatherColor()).build());
        } else {
            newItem = newItem.withType(newItem.getMaterial().colorize(color.material1_13));
        }
        return newItem;
    }
}
