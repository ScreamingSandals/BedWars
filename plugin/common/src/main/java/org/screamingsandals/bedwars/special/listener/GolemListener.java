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
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.special.GolemImpl;
import org.screamingsandals.bedwars.utils.DelayFactoryImpl;
import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.entity.Mob;
import org.screamingsandals.lib.entity.projectile.ProjectileEntity;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.EntityDamageByEntityEvent;
import org.screamingsandals.lib.event.entity.EntityDeathEvent;
import org.screamingsandals.lib.event.entity.EntityTargetEvent;
import org.screamingsandals.lib.event.player.PlayerDeathEvent;
import org.screamingsandals.lib.event.player.PlayerInteractEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class GolemListener {
    private static final String GOLEM_PREFIX = "Module:Golem:";

    @OnEvent
    public void onGolemRegister(ApplyPropertyToBoughtItemEventImpl event) {
        if (event.getPropertyName().equalsIgnoreCase("golem")) {
            event.setStack(ItemUtils.saveData(event.getStack(), applyProperty(event)));
        }
    }

    @OnEvent
    public void onGolemUse(PlayerInteractEvent event) {
        var player = event.player();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        var gamePlayer = PlayerManagerImpl.getInstance().getPlayer(player).orElseThrow();
        var game = gamePlayer.getGame();

        if (event.action() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.action() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            if (game != null && game.getStatus() == GameStatus.RUNNING && !gamePlayer.isSpectator() && event.item() != null) {
                var stack = event.item();
                var unhidden = ItemUtils.getIfStartsWith(stack, GOLEM_PREFIX);

                if (unhidden != null) {
                    if (!game.isDelayActive(gamePlayer, GolemImpl.class)) {
                        event.cancelled(true);

                        final var propertiesSplit = unhidden.split(":");
                        var speed = Double.parseDouble(propertiesSplit[2]);
                        var follow = Double.parseDouble(propertiesSplit[3]);
                        var health = Double.parseDouble(propertiesSplit[4]);
                        var showName = Boolean.parseBoolean(propertiesSplit[5]);
                        var delay = Integer.parseInt(propertiesSplit[6]);
                        //boolean collidable = Boolean.parseBoolean(propertiesSplit[7]); //keeping this to keep configs compatible
                        var name = propertiesSplit[8];

                        final var clickedBlock = event.clickedBlock();
                        var location = (clickedBlock == null)
                                ? player.getLocation()
                                : clickedBlock.location().add(event.blockFace().getDirection()).add(0.5, 0.5, 0.5);

                        var golem = new GolemImpl(game, gamePlayer, game.getPlayerTeam(gamePlayer),
                                stack, location, speed, follow, health, name, showName);

                        if (delay > 0) {
                            var delayFactory = new DelayFactoryImpl(delay, golem, gamePlayer, game);
                            game.registerDelay(delayFactory);
                        }

                        golem.spawn();
                    } else {
                        event.cancelled(true);

                        var delay = game.getActiveDelay(gamePlayer, GolemImpl.class).getRemainDelay();
                        MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ITEM_DELAY).placeholder("time", delay));
                    }
                }
            }
        }
    }

    @OnEvent
    public void onGolemDamage(EntityDamageByEntityEvent event) {
        if (!event.entity().getEntityType().is("IRON_GOLEM")) {
            return;
        }

        var ironGolem = event.entity();
        for (var game : GameManagerImpl.getInstance().getLocalGames()) {
            if (game.getStatus() == GameStatus.RUNNING && ironGolem.getLocation().getWorld().equals(game.getGameWorld())) {
                for (var golem : game.getActiveSpecialItems(GolemImpl.class)) {
                    if (golem.getEntity().equals(ironGolem)) {
                        if (event.damager() instanceof Player) {
                            var player = (Player) event.damager();
                            if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                                if (golem.getTeam() != game.getPlayerTeam(player.as(BedWarsPlayer.class))) {
                                    return;
                                }
                            }
                        } else if (event.damager() instanceof ProjectileEntity) {
                            var shooter = event.damager().as(ProjectileEntity.class).getShooter();
                            if (shooter instanceof Player) {
                                var player = (Player) shooter;
                                if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                                    if (golem.getTeam() != game.getPlayerTeam(player.as(BedWarsPlayer.class))) {
                                        return;
                                    }
                                }
                            }
                        }

                        event.cancelled(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.FRIENDLYFIRE, false));
                        return;
                    }
                }
            }
        }
    }

    @OnEvent
    public void onGolemTarget(EntityTargetEvent event) {
        if (!event.entity().getEntityType().is("IRON_GOLEM")) {
            return;
        }

        var ironGolem = event.entity();
        for (var game : GameManagerImpl.getInstance().getLocalGames()) {
            if ((game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) && ironGolem.getLocation().getWorld().equals(game.getGameWorld())) {
                for (var item : game.getActiveSpecialItems(GolemImpl.class)) {
                    if (item.getEntity().equals(ironGolem)) {
                        if (event.target() instanceof Player) {
                            final var player = (Player) event.target();

                            if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                                var gPlayer = player.as(BedWarsPlayer.class);
                                if (game.isProtectionActive(gPlayer)) {
                                    event.cancelled(true);
                                    return;
                                }

                                if (item.getTeam() == game.getPlayerTeam(gPlayer)) {
                                    event.cancelled(true);
                                    // Try to find enemy
                                    var playerTarget = MiscUtils.findTarget(game, player, item.getFollowRange());
                                    if (playerTarget != null) {
                                        // Oh. We found enemy!
                                        ironGolem.as(Mob.class).setCurrentTarget(playerTarget);
                                        return;
                                    }
                                }
                            } else {
                                event.cancelled(true);
                            }
                        }
                    }
                }
            }
        }
    }

    @OnEvent
    public void onGolemTargetDie(PlayerDeathEvent event) {
        if (PlayerManagerImpl.getInstance().isPlayerInGame(event.player())) {
            var game = PlayerManagerImpl.getInstance().getGameOfPlayer(event.player()).orElseThrow();

            for (var item : game.getActiveSpecialItems(GolemImpl.class)) {
                var iron = item.getEntity().as(Mob.class);
                if (event.player().equals(iron.getCurrentTarget())) {
                    iron.setCurrentTarget(null);
                }
            }
        }
    }

    @OnEvent
    public void onGolemDeath(EntityDeathEvent event) {
        if (!event.entity().getEntityType().is("IRON_GOLEM")) {
            return;
        }

        var ironGolem = event.entity();
        for (var game : GameManagerImpl.getInstance().getLocalGames()) {
            if ((game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) && ironGolem.getLocation().getWorld().equals(game.getGameWorld())) {
                for (var item : game.getActiveSpecialItems(GolemImpl.class)) {
                    if (item.getEntity().equals(ironGolem)) {
                        event.drops().clear();
                    }
                }
            }
        }
    }

    private String applyProperty(ApplyPropertyToBoughtItemEventImpl event) {
        return GOLEM_PREFIX
                + MiscUtils.getDoubleFromProperty(
                "speed", "specials.golem.speed", event) + ":"
                + MiscUtils.getDoubleFromProperty(
                "follow-range", "specials.golem.follow-range", event) + ":"
                + MiscUtils.getDoubleFromProperty(
                "health", "specials.golem.health", event) + ":"
                + MiscUtils.getBooleanFromProperty(
                "show-name", "specials.golem.show-name", event) + ":"
                + MiscUtils.getIntFromProperty(
                "delay", "specials.golem.delay", event) + ":"
                + MiscUtils.getBooleanFromProperty("collidable",
                "specials.golem.collidable", event) + ":"
                + MiscUtils.getStringFromProperty(
                "name-format", "specials.golem.name-format", event);
    }
}
