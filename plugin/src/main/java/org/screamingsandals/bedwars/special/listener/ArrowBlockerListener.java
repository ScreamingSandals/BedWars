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
import org.screamingsandals.bedwars.special.ArrowBlocker;
import org.screamingsandals.bedwars.utils.DelayFactory;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static org.screamingsandals.bedwars.lib.lang.I.i18nc;
import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;
import static org.screamingsandals.bedwars.lib.lang.I18n.i18nonly;

public class ArrowBlockerListener implements Listener {
    private static final String ARROW_BLOCKER_PREFIX = "Module:ArrowBlocker:";

    @EventHandler
    public void onArrowBlockerRegistered(BedwarsApplyPropertyToBoughtItem event) {
        if (event.getPropertyName().equalsIgnoreCase("arrowblocker")) {
            ItemStack stack = event.getStack();
            APIUtils.hashIntoInvisibleString(stack, applyProperty(event));
        }
    }

    @EventHandler
    public void onPlayerUseItem(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!Main.isPlayerInGame(player)) {
            return;
        }

        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
        Game game = gPlayer.getGame();

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator && event.getItem() != null) {
                ItemStack stack = event.getItem();
                String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(stack, ARROW_BLOCKER_PREFIX);

                if (unhidden != null) {
                    if (!game.isDelayActive(player, ArrowBlocker.class)) {
                        event.setCancelled(true);

                        int protectionTime = Integer.parseInt(unhidden.split(":")[2]);
                        int delay = Integer.parseInt(unhidden.split(":")[3]);
                        ArrowBlocker arrowBlocker = new ArrowBlocker(game, event.getPlayer(),
                                game.getTeamOfPlayer(event.getPlayer()), stack, protectionTime);

                        if (arrowBlocker.isActivated()) {
                            event.getPlayer().sendMessage(i18nc("specials_arrow_blocker_already_activated", game.getCustomPrefix()));
                            return;
                        }

                        if (delay > 0) {
                            DelayFactory delayFactory = new DelayFactory(delay, arrowBlocker, player, game);
                            game.registerDelay(delayFactory);
                        }

                        arrowBlocker.activate();
                    } else {
                        event.setCancelled(true);

                        int delay = game.getActiveDelay(player, ArrowBlocker.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(player, i18nonly("special_item_delay").replace("%time%", String.valueOf(delay)));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = ((Player) entity).getPlayer();

        if (!Main.isPlayerInGame(player)) {
            return;
        }

        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
        Game game = gPlayer.getGame();

        if (gPlayer.isSpectator) {
            return;
        }

        ArrowBlocker arrowBlocker = (ArrowBlocker) game.getFirstActivedSpecialItemOfPlayer(player, ArrowBlocker.class);
        if (arrowBlocker != null && event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
            event.setCancelled(true);
        }
    }

    private String applyProperty(BedwarsApplyPropertyToBoughtItem event) {
        return ARROW_BLOCKER_PREFIX
                + MiscUtils.getIntFromProperty(
                "protection-time", "specials.arrow-blocker.protection-time", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.arrow-blocker.delay", event);
    }
}
