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

import lombok.experimental.UtilityClass;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.lib.entity.EntityLiving;
import org.screamingsandals.lib.item.builder.ItemFactory;

@UtilityClass
public class ArmorStandUtils {
    public void equip(EntityLiving entity, TeamImpl team) {
        var helmet = ItemFactory.build("leather_helmet", builder -> builder.color(team.getColor().getLeatherColor())).orElseThrow();
        var chestplate = ItemFactory.build("leather_chestplate", builder -> builder.color(team.getColor().getLeatherColor())).orElseThrow();
        var leggings = ItemFactory.build("leather_leggings", builder -> builder.color(team.getColor().getLeatherColor())).orElseThrow();
        var boots = ItemFactory.build("leather_boots", builder -> builder.color(team.getColor().getLeatherColor())).orElseThrow();

        entity.setHelmet(helmet);
        entity.setChestplate(chestplate);
        entity.setLeggings(leggings);
        entity.setBoots(boots);
    }
}
