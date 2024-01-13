/*
 * Copyright (C) 2024 ScreamingSandals
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
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.BridgeEggImpl;
import org.screamingsandals.bedwars.utils.DelayFactoryImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.entity.Entities;
import org.screamingsandals.lib.entity.Entity;
import org.screamingsandals.lib.entity.projectile.ProjectileEntity;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.ProjectileHitEvent;
import org.screamingsandals.lib.event.player.PlayerInteractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class BridgeEggListener {
    private static final String BRIDGE_EGG_PREFIX = "Module:BridgeEgg:";
    private final Map<Entity, BridgeEggImpl> bridges = new HashMap<>();

    @OnEvent
    public void onEggRegistration(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("bridgeegg")) {
            event.setStack(ItemUtils.saveData(event.getStack(), this.applyProperty(event)));
        }
    }

    @OnEvent
    public void onEggUse(PlayerInteractEvent event) {
        final var player = event.player();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        BedWarsPlayer gamePlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        final var game = gamePlayer.getGame();
        if (event.action() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            var stack = event.item();
            if (game != null && game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator() && stack != null) {
                String unhidden = ItemUtils.getIfStartsWith(stack, BRIDGE_EGG_PREFIX);
                if (unhidden != null) {
                    if (!game.isDelayActive(gamePlayer, BridgeEggImpl.class)) {
                        event.cancelled(true);

                        final var propertiesSplit = unhidden.split(":");
                        var distance = Double.parseDouble(propertiesSplit[2]);
                        var material = MiscUtils.getBlockTypeFromString(propertiesSplit[3], "GLASS");
                        var delay = Integer.parseInt(propertiesSplit[4]);

                        var egg = Objects.requireNonNull(Entities.spawn("egg", player.getLocation().add(0, 1, 0), projectile -> {
                            projectile.setVelocity(player.getLocation().getFacingDirection().multiply(2));
                            ((ProjectileEntity) projectile).setShooter(player);
                        }));

                        var bridgeEgg = new BridgeEggImpl(game, gamePlayer, game.getPlayerTeam(gamePlayer), egg, material, distance);

                        if (delay > 0) {
                            var delayFactory = new DelayFactoryImpl(delay, bridgeEgg, gamePlayer, game);
                            game.registerDelay(delayFactory);
                        }

                        this.bridges.put(egg, bridgeEgg);
                        bridgeEgg.runTask();

                        var stack2 = stack.withAmount(1); // we are removing exactly one egg
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

                        var delay = game.getActiveDelay(gamePlayer, BridgeEggImpl.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }
            }
        }
    }

    @OnEvent
    public void onProjectileHit(ProjectileHitEvent event) {
        final var egg = event.entity();

        if (this.bridges.containsKey(egg)) {
            egg.remove();
            this.bridges.get(egg).getTask().cancel();
            this.bridges.remove(egg);
        }
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return BRIDGE_EGG_PREFIX
                + MiscUtils.getDoubleFromProperty(
                "distance", "specials.bridge-egg.distance", event) + ":"
                + MiscUtils.getMaterialFromProperty(
                "material", "specials.bridge-egg.material", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.bridge-egg.delay", event);
    }
}
