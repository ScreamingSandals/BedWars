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

package org.screamingsandals.bedwars.listener;

import org.bukkit.Material;
import org.bukkit.block.data.type.Cake;
import org.bukkit.event.player.PlayerInteractEvent;
import org.screamingsandals.bedwars.game.Game;

public class Player113ListenerUtils {
    public static void yummyCake(PlayerInteractEvent event, Game game) {
        if (event.getClickedBlock().getBlockData() instanceof Cake) {
            Cake cake = (Cake) event.getClickedBlock().getBlockData();
            if (cake.getBites() == 0) {
                game.getRegion().putOriginalBlock(event.getClickedBlock().getLocation(), event.getClickedBlock().getState());
            }
            cake.setBites(cake.getBites() + 1);
            if (cake.getBites() >= cake.getMaximumBites()) {
                game.bedDestroyed(event.getClickedBlock().getLocation(), event.getPlayer(), false, false, true);
                event.getClickedBlock().setType(Material.AIR);
            } else {
                event.getClickedBlock().setBlockData(cake);
            }
        }
    }
}
