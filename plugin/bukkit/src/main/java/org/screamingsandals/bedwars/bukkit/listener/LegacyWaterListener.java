/*
 * Copyright (C) 2026 ScreamingSandals
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

package org.screamingsandals.bedwars.bukkit.listener;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.utils.ArenaUtils;
import org.screamingsandals.lib.world.Location;

// TODO: Migrate to ScreamingLib as FluidLevelChangeEvent backport for MC <= 1.12.2
public class LegacyWaterListener implements Listener {
    private static final Material WATER = Material.valueOf("WATER");
    private static final Material STATIONARY_WATER = Material.valueOf("STATIONARY_WATER");
    private static final Material LAVA = Material.valueOf("LAVA");
    private static final Material STATIONARY_LAVA = Material.valueOf("STATIONARY_LAVA");

    @EventHandler(ignoreCancelled = true)
    public void onWaterDecay(BlockPhysicsEvent event) {
        var block = event.getBlock();
        var type = block.getType();

        if (type != WATER && type != STATIONARY_WATER && type != LAVA && type != STATIONARY_LAVA) {
            return;
        }

        var loc = Location.fromPlatform(block.getLocation());

        for (var game : GameManagerImpl.getInstance().getLocalGames()) {
            if (ArenaUtils.isInArea(loc, game.getPos1(), game.getPos2())) {
                if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                    if (!game.isBlockAddedDuringGame(loc)) {
                        game.getRegion().putOriginalBlockIfAbsent(loc, loc.getBlock().blockSnapshot());
                    }
                } else if (game.getStatus() != GameStatus.DISABLED) {
                    event.setCancelled(true);
                }
                break;
            }
        }
    }
}