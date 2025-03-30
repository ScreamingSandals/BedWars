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

package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.events.PlayerBreakBlockEventImpl;
import org.screamingsandals.bedwars.events.PlayerBuildBlockEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.TrapImpl;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.PlayerMoveEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.List;
import java.util.Map;

@Service
public class TrapListener {
    private static final String TRAP_PREFIX = "Module:Trap:";

    @OnEvent
    @SuppressWarnings("unchecked")
    public void onTrapRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("trap")) {
            TrapImpl trap = new TrapImpl(event.getGame(), event.getPlayer(),
                    event.getGame().getPlayerTeam(event.getPlayer()),
                    (List<Map<String, Object>>) event.getProperty("data"));

            event.setStack(ItemUtils.saveData(event.getStack(), TRAP_PREFIX + System.identityHashCode(trap)));
        }
    }

    @OnEvent
    public void onTrapBuild(PlayerBuildBlockEventImpl event) {
        if (event.isCancelled()) {
            return;
        }

        var trapItem = event.getItemInHand();
        var unhidden = ItemUtils.getIfStartsWith(trapItem, TRAP_PREFIX);
        if (unhidden != null) {
            int classID = Integer.parseInt(unhidden.split(":")[2]);

            for (var special : event.getGame().getActiveSpecialItems(TrapImpl.class)) {
                if (System.identityHashCode(special) == classID) {
                    special.place(event.getBlock().location());
                    event.getPlayer().sendMessage(Message.of(LangKeys.SPECIALS_TRAP_BUILT).prefixOrDefault((event.getGame()).getCustomPrefixComponent()));
                    return;
                }
            }
        }
    }

    @OnEvent
    public void onTrapBreak(PlayerBreakBlockEventImpl event) {
        for (var special : event.getGame().getActiveSpecialItems(TrapImpl.class)) {
            var runningTeam = event.getTeam();

            if (special.isPlaced() && event.getBlock().location().equals(special.getLocation())) {
                event.setDrops(false);
                special.process(event.getPlayer(), runningTeam, true);
            }
        }
    }

    @OnEvent
    public void onMove(PlayerMoveEvent event) {
        var player = event.player();
        if (event.cancelled() || !PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var difX = Math.abs(event.currentLocation().getX() - event.newLocation().getX());
        var difZ = Math.abs(event.currentLocation().getZ() - event.newLocation().getZ());

        if (difX == 0.0 && difZ == 0.0) {
            return;
        }

        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gPlayer.getGame();
        if (game != null && game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator()) {
            for (var special : game.getActiveSpecialItems(TrapImpl.class)) {
                if (special.isPlaced()) {
                    if (game.getPlayerTeam(gPlayer) != special.getTeam()) {
                        if (event.newLocation().getBlock().equals(special.getLocation().getBlock())) {
                            special.process(gPlayer, game.getPlayerTeam(gPlayer), false);
                        }
                    }
                }
            }
        }
    }
}
