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
import org.screamingsandals.bedwars.special.ArrowBlockerImpl;
import org.screamingsandals.bedwars.utils.DelayFactoryImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.SEntityDamageEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class ArrowBlockerListener {
    private static final String ARROW_BLOCKER_PREFIX = "Module:ArrowBlocker:";

    @OnEvent
    public void onArrowBlockerRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("arrowblocker")) {
            event.setStack(ItemUtils.saveData(event.getStack(), applyProperty(event)));
        }
    }

    @OnEvent
    public void onPlayerUseItem(SPlayerInteractEvent event) {
        var player = event.player();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gPlayer.getGame();

        if (event.action() == SPlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game != null && game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator() && event.item() != null) {
                var stack = event.item();
                var unhidden = ItemUtils.getIfStartsWith(stack, ARROW_BLOCKER_PREFIX);

                if (unhidden != null) {
                    if (!game.isDelayActive(gPlayer, ArrowBlockerImpl.class)) {
                        event.cancelled(true);

                        final var propertiesSplit = unhidden.split(":");
                        int protectionTime = Integer.parseInt(propertiesSplit[2]);
                        int delay = Integer.parseInt(propertiesSplit[3]);
                        var arrowBlocker = new ArrowBlockerImpl(game, gPlayer, game.getPlayerTeam(gPlayer), stack, protectionTime);

                        if (arrowBlocker.isActivated()) {
                            player.sendMessage(Message.of(LangKeys.SPECIALS_ARROW_BLOCKER_ALREADY_ACTIVATED).prefixOrDefault(game.getCustomPrefixComponent()));
                            return;
                        }

                        if (delay > 0) {
                            var delayFactory = new DelayFactoryImpl(delay, arrowBlocker, gPlayer, game);
                            game.registerDelay(delayFactory);
                        }

                        arrowBlocker.activate();
                    } else {
                        event.cancelled(true);

                        int delay = game.getActiveDelay(gPlayer, ArrowBlockerImpl.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGH)
    public void onDamage(SEntityDamageEvent event) {
        var entity = event.entity();
        if (!(entity instanceof PlayerWrapper)) {
            return;
        }

        var player = (PlayerWrapper) event.entity();

        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gPlayer.getGame();

        if (gPlayer.isSpectator() || game == null) {
            return;
        }

        var arrowBlocker = game.getFirstActiveSpecialItemOfPlayer(gPlayer, ArrowBlockerImpl.class);
        if (arrowBlocker != null && event.damageCause().is("PROJECTILE")) {
            event.cancelled(true);
        }
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return ARROW_BLOCKER_PREFIX
                + MiscUtils.getIntFromProperty(
                "protection-time", "specials.arrow-blocker.protection-time", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.arrow-blocker.delay", event);
    }
}
