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

package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.special.Tracker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class TrackerListener implements Listener {
    private static final String TRACKER_PREFIX = "Module:Tracker:";

    @EventHandler
    public void onTrackerRegistered(BedwarsApplyPropertyToBoughtItem event) {
        if (event.getPropertyName().equalsIgnoreCase("tracker")) {
            ItemStack stack = event.getStack();

            APIUtils.hashIntoInvisibleString(stack, TRACKER_PREFIX);
        }

    }

    @EventHandler
    public void onTrackerUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Main.isPlayerInGame(player)) {
            return;
        }

        GamePlayer gamePlayer = Main.getPlayerGameProfile(player);
        Game game = gamePlayer.getGame();
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator) {
                if (event.getItem() != null) {
                    ItemStack stack = event.getItem();
                    String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, TRACKER_PREFIX);
                    if (unhidden != null) {
                        event.setCancelled(true);

                        Tracker tracker = new Tracker(game, player, game.getTeamOfPlayer(player));
                        tracker.runTask();
                    }
                }
            }
        }
    }
}
