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

import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.PopUpTowerImpl;
import org.screamingsandals.bedwars.utils.DelayFactoryImpl;
import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.PlayerInteractEvent;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.utils.BlockFace;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class PopUpTowerListener {
    private static final String POPUP_TOWER_PREFIX = "Module:PopupTower:";

    @OnEvent
    public void onPopUpTowerRegistration(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("popuptower")) {
            event.setStack(ItemUtils.saveData(event.getStack(), this.applyProperty(event)));
        }
    }

    @OnEvent
    public void onPopUpTowerUse(PlayerInteractEvent event) {
        final var player = event.player();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gamePlayer = player.as(BedWarsPlayer.class);
        final var game = gamePlayer.getGame();
        final var action = event.action();
        if (action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {

            if (game != null && game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator() && event.item() != null) {
                var stack = event.item();
                String unhidden = ItemUtils.getIfStartsWith(stack, POPUP_TOWER_PREFIX);
                if (unhidden != null) {
                    if (!game.isDelayActive(gamePlayer, PopUpTowerImpl.class)) {
                        event.cancelled(true);

                        var propertiesSplit = unhidden.split(":");
                        var material = MiscUtils.getBlockTypeFromString(propertiesSplit[2], "WOOL");
                        var delay = Integer.parseInt(propertiesSplit[3]);

                        var playerFace = MiscUtils.yawToFace(player.getLocation().getYaw(), false);

                        var popupTower = new PopUpTowerImpl(game, gamePlayer, game.getPlayerTeam(gamePlayer), material, player.getLocation().getBlock().location().add(playerFace).add(BlockFace.DOWN), playerFace);

                        if (delay > 0) {
                            var delayFactory = new DelayFactoryImpl(delay, popupTower, gamePlayer, game);
                            game.registerDelay(delayFactory);
                        }

                        popupTower.runTask();

                        var stack2 = stack.withAmount(1); // we are removing exactly one popup tower
                        try {
                            if (player.getPlayerInventory().getItemInOffHand().equals(stack2)) {
                                player.getPlayerInventory().setItemInOffHand(ItemStackFactory.getAir());
                            } else {
                                player.getPlayerInventory().removeItem(stack2);
                            }
                        } catch (Throwable e) {
                            player.getPlayerInventory().removeItem(stack2);
                        }
                        player.forceUpdateInventory();
                    } else {
                        event.cancelled(true);

                        var delay = game.getActiveDelay(gamePlayer, PopUpTowerImpl.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }
            }
        }
    }

    // TODO: make more things configurable
    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return POPUP_TOWER_PREFIX
                + MiscUtils.getMaterialFromProperty(
                "material", "specials.popup-tower.material", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.popup-tower.delay", event);
    }
}
