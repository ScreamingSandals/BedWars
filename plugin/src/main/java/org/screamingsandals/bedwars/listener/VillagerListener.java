/*
 * Copyright (C) 2023 ScreamingSandals
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

import org.bukkit.Bukkit;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GameStore;
import org.screamingsandals.bedwars.api.events.BedwarsOpenShopEvent;
import org.screamingsandals.bedwars.api.events.BedwarsOpenShopEvent.Result;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.screamingsandals.bedwars.utils.CitizensUtils;

public class VillagerListener implements Listener {

    @EventHandler
    public void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (Main.isPlayerInGame(event.getPlayer())) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
            Game game = gPlayer.getGame();
            if (event.getRightClicked().getType().isAlive() && !gPlayer.isSpectator
                    && gPlayer.getGame().getStatus() == GameStatus.RUNNING) {

                if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
                    // .equals doesn't work with Citizens
                    GameStore npcStore = CitizensUtils.getFromNPC(event.getRightClicked());
                    if (npcStore != null) {
                        event.setCancelled(true);
                        open(npcStore, event, game);
                        return;
                    }
                }

                for (GameStore store : game.getGameStoreList()) {
                    if (event.getRightClicked().equals(store.getEntity())) {
                        event.setCancelled(true);
                        open(store, event, game);
                    }
                }

            }
        }
    }

    public void open(GameStore store, PlayerInteractEntityEvent event, Game game) {
        BedwarsOpenShopEvent openShopEvent = new BedwarsOpenShopEvent(game,
                event.getPlayer(), store, event.getRightClicked());
        Main.getInstance().getServer().getPluginManager().callEvent(openShopEvent);

        if (openShopEvent.getResult() != Result.ALLOW) {
            return;
        }

        Main.openStore(event.getPlayer(), store);
    }
}
