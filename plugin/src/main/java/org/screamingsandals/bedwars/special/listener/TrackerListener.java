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

import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class TrackerListener {
    private static final String TRACKER_PREFIX = "Module:Tracker:";

    @OnEvent
    public void onTrackerRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("tracker")) {
            event.setStack(ItemUtils.saveData(event.getStack(), TRACKER_PREFIX));
        }
    }

    @OnEvent
    public void onTrackerUse(SPlayerInteractEvent event) {
        var player = event.getPlayer();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gamePlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gamePlayer.getGame();
        if (event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game != null && game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator()) {
                if (event.getItem() != null) {
                    var stack = event.getItem();
                    var unhidden = ItemUtils.getIfStartsWith(stack, TRACKER_PREFIX);
                    if (unhidden != null) {
                        event.setCancelled(true);

                        Tasker.build(() -> {
                            var target = MiscUtils.findTarget(game, player, Double.MAX_VALUE);
                            if (target != null) {
                                player.setCompassTarget(target.getLocation());

                                int distance = (int) Math.sqrt(player.getLocation().getDistanceSquared(target.getLocation()));
                                MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_TRACKER_TARGET_FOUND).placeholder("target", target.getDisplayName()).placeholder("distance", distance));
                            } else {
                                MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_TRACKER_NO_TARGET_FOUND));
                                player.setCompassTarget(game.getTeamOfPlayer(gamePlayer).getTeamSpawn());
                            }
                        })
                        .afterOneTick()
                        .start();
                    }
                }
            }
        }
    }
}
