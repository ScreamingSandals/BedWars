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

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GameCreator;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class Player19Listener implements Listener {
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            if (gPlayer.getGame().getStatus() == GameStatus.WAITING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        // This is already in 1.8.8, but in older 1.8.x versions not (need to check 1.8.8/9 version)
        if (event.isCancelled()) {
            return;
        }

        for (String s : Main.getGameNames()) {
            Game game = Main.getGame(s);
            if (game.getStatus() == GameStatus.RUNNING && game.getOriginalOrInheritedSpawnerDisableMerge()) {
                if (GameCreator.isInArea(event.getEntity().getLocation(), game.getPos1(), game.getPos2()) || GameCreator.isInArea(event.getTarget().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
