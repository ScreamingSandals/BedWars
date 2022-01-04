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

import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.events.PlayerBreakBlockEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.ProtectionWallImpl;
import org.screamingsandals.bedwars.utils.DelayFactoryImpl;
import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class ProtectionWallListener {
    private static final String PROTECTION_WALL_PREFIX = "Module:ProtectionWall:";

    @OnEvent
    public void onProtectionWallRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("protectionwall")) {
            event.setStack(ItemUtils.saveData(event.getStack(), applyProperty(event)));
        }
    }

    @OnEvent
    public void onPlayerUseItem(SPlayerInteractEvent event) {
        var player = event.getPlayer();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gPlayer.getGame();

        if (event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game != null && game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator() && event.getItem() != null) {
                var stack = event.getItem();
                var unhidden = ItemUtils.getIfStartsWith(stack, PROTECTION_WALL_PREFIX);

                if (unhidden != null) {
                    if (!game.isDelayActive(gPlayer, ProtectionWallImpl.class)) {
                        event.setCancelled(true);

                        var propertiesSplit = unhidden.split(":");
                        var isBreakable = Boolean.parseBoolean(propertiesSplit[2]);
                        var delay = Integer.parseInt(propertiesSplit[3]);
                        var breakTime = Integer.parseInt(propertiesSplit[4]);
                        var width = Integer.parseInt(propertiesSplit[5]);
                        var height = Integer.parseInt(propertiesSplit[6]);
                        var distance = Integer.parseInt(propertiesSplit[7]);
                        var result = MiscUtils.getBlockTypeFromString(propertiesSplit[8], "CUT_SANDSTONE");


                        var protectionWall = new ProtectionWallImpl(game, gPlayer, game.getPlayerTeam(gPlayer), stack);

                        if (!event.getPlayer().getEyeLocation().getBlock().getType().isAir()) {
                            MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_PROTECTION_WALL_NOT_USABLE_HERE));
                            return;
                        }

                        if (delay > 0) {
                            var delayFactory = new DelayFactoryImpl(delay, protectionWall, gPlayer, game);
                            game.registerDelay(delayFactory);
                        }

                        protectionWall.createWall(isBreakable, breakTime, width, height, distance, result);
                    } else {
                        event.setCancelled(true);

                        var delay = game.getActiveDelay(gPlayer, ProtectionWallImpl.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }
            }
        }
    }

    @OnEvent
    public void onBlockBreak(PlayerBreakBlockEventImpl event) {
        for (var checkedWall : event.getGame().getActiveSpecialItems(ProtectionWallImpl.class)) {
            if (checkedWall != null) {
                for (var wallBlock : checkedWall.getWallBlocks()) {
                    if (wallBlock.equals(event.getBlock()) && !checkedWall.isBreakable()) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return PROTECTION_WALL_PREFIX
                + MiscUtils.getBooleanFromProperty(
                "is-breakable", "specials.protection-wall.is-breakable", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.protection-wall.delay", event) + ":"
                + MiscUtils.getIntFromProperty(
                "break-time", "specials.protection-wall.break-time", event) + ":"
                + MiscUtils.getIntFromProperty(
                "width", "specials.protection-wall.width", event) + ":"
                + MiscUtils.getIntFromProperty(
                "height", "specials.protection-wall.height", event) + ":"
                + MiscUtils.getIntFromProperty(
                "distance", "specials.protection-wall.distance", event) + ":"
                + MiscUtils.getMaterialFromProperty(
                "material", "specials.protection-wall.material", event);
    }
}
