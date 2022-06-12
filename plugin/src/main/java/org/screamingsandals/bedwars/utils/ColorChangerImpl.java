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

package org.screamingsandals.bedwars.utils;

import org.screamingsandals.bedwars.api.TeamColor;
import org.screamingsandals.bedwars.api.utils.ColorChanger;
import org.screamingsandals.bedwars.game.TeamColorImpl;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class ColorChangerImpl implements ColorChanger {
    @Override
    public Item applyColor(TeamColor apiColor, Object item) {
        var color = (TeamColorImpl) apiColor;
        var newItem = item instanceof Item ? ((Item) item).clone() : ItemFactory.build(item).orElse(ItemFactory.getAir());
        if (newItem.getMaterial().is("LEATHER_BOOTS", "LEATHER_CHESTPLATE", "LEATHER_HELMET", "LEATHER_LEGGINGS")) {
            newItem = newItem.builder().color(color.getLeatherColor()).build().orElseThrow();
        } else {
            newItem = newItem.withType(newItem.getMaterial().colorize(color.material1_13));
        }
        return newItem;
    }
}
