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

import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.TNTSheepImpl;
import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.entity.LivingEntity;
import org.screamingsandals.lib.event.EventExecutionOrder;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.EntityDamageByEntityEvent;
import org.screamingsandals.lib.event.player.PlayerInteractEntityEvent;
import org.screamingsandals.lib.event.player.PlayerInteractEvent;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Objects;

@Service
public class TNTSheepListener {
    private static final String TNT_SHEEP_PREFIX = "Module:TNTSheep:";

    @OnEvent
    public void onTNTSheepRegistered(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("tntsheep")) {
            event.setStack(ItemUtils.saveData(event.getStack(), applyProperty(event)));
        }

    }

    @OnEvent
    public void onTNTSheepUsed(PlayerInteractEvent event) {
        var player = event.player();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gamePlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gamePlayer.getGame();

        if (event.action() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game != null && game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator() && event.item() != null) {
                var stack = event.item();
                String unhidden = ItemUtils.getIfStartsWith(stack, TNT_SHEEP_PREFIX);

                if (unhidden != null) {
                    event.cancelled(true);

                    var propertiesSplit = unhidden.split(":");
                    var speed = Double.parseDouble(propertiesSplit[2]);
                    var follow = Double.parseDouble(propertiesSplit[3]);
                    var maxTargetDistance = Double.parseDouble(propertiesSplit[4]);
                    var explosionTime = Integer.parseInt(propertiesSplit[5]);

                    final var clickedBlock = event.clickedBlock();
                    var startLocation = (clickedBlock == null)
                            ? player.getLocation()
                            : clickedBlock.location().add(event.blockFace().getDirection());

                    var sheep = new TNTSheepImpl(game, gamePlayer, game.getPlayerTeam(gamePlayer),
                            startLocation, stack, speed, follow, maxTargetDistance, explosionTime);

                    sheep.spawn();
                }
            }
        }
    }

    @OnEvent(order = EventExecutionOrder.LAST)
    public void onTNTSheepDamage(EntityDamageByEntityEvent event) {
        if (event.cancelled() || event.damageCause().is("CUSTOM", "VOID", "FALL")) {
            return;
        }

        if (event.entity() instanceof Player) {
            var player = (Player) event.entity();
            if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                var gamePlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
                var game = gamePlayer.getGame();
                if (game != null && event.damager().getEntityType().is("minecraft:tnt") && !game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.FRIENDLYFIRE, false)) {
                    var tnt = event.damager();
                    for (var sheep : game.getActiveSpecialItems(TNTSheepImpl.class)) {
                        if (tnt.equals(sheep.getTnt())) {
                            if (sheep.getTeam() == game.getPlayerTeam(gamePlayer)) {
                                event.cancelled(true);
                            }
                            return;
                        }
                    }
                }
            }
        } else if (event.entity() instanceof LivingEntity) {
            var mob = event.entity();
            for (var game : GameManagerImpl.getInstance().getGames()) {
                if (game.getStatus() == GameStatus.RUNNING && mob.getLocation().getWorld().equals(game.getGameWorld())) {
                    var sheeps = game.getActiveSpecialItems(TNTSheepImpl.class);
                    for (var sheep : sheeps) {
                        if (mob.equals(sheep.getEntity())) {
                            event.damage(0.0);
                            return;
                        }
                    }
                }
            }
        }

    }

    @OnEvent
    public void onTNTSheepInteractOtherUser(PlayerInteractEntityEvent event) {
        var player = event.player();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gamePlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
            var game = Objects.requireNonNull(gamePlayer.getGame());

            var rightClicked = event.clickedEntity();
            var vehicle = rightClicked.isInsideVehicle() ? rightClicked.getVehicle() : null;
            for (var sheep : game.getActiveSpecialItems(TNTSheepImpl.class)) {
                if (sheep.getEntity().equals(rightClicked) || sheep.getEntity().equals(vehicle)) {
                    event.cancelled(true);
                    return;
                }
            }
        }
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return TNT_SHEEP_PREFIX
                + MiscUtils.getDoubleFromProperty(
                "speed", "specials.tnt-sheep.speed", event) + ":"
                + MiscUtils.getDoubleFromProperty(
                "follow-range", "specials.tnt-sheep.follow-range", event) + ":"
                + MiscUtils.getDoubleFromProperty(
                "max-target-distance", "specials.tnt-sheep.max-target-distance", event) + ":"
                + MiscUtils.getIntFromProperty(
                "explosion-time", "specials.tnt-sheep.explosion-time", event);
    }
}
