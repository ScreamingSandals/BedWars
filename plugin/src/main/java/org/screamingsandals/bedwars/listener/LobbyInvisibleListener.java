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

package org.screamingsandals.bedwars.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.api.events.BedwarsGameStartedEvent;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.utils.MiscUtils;

import java.util.Objects;

public class LobbyInvisibleListener implements Listener {

    @EventHandler
    public void onGameStarted(BedwarsGameStartedEvent event) {
        Game game = event.getGame();
        if (!game.getOriginalOrInheritedHideLobbyAfterGameStart()) {
            return;
        }
        if (game.getLobbyPos1() == null || game.getLobbyPos2() == null) {
            return;
        }

        MiscUtils.getLocationsBetween(game.getLobbyPos1(), game.getLobbyPos2()).forEach(loc -> {
            if (loc.getBlock().getType().isAir()) {
                return;
            }
            Block block = loc.getBlock();
            BlockState blockState = Objects.requireNonNull(block.getState());
            game.getRegion().putOriginalBlock(loc, blockState);
            block.setType(Material.AIR, false);
        });
    }
}
