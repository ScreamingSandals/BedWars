/*
 * Copyright (C) 2023 ScreamingSandals
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

package org.screamingsandals.bedwars.listener;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.event.player.PlayerMoveEvent;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GameCreator;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.Arrays;

public class Player19Listener implements Listener {
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            if (gPlayer.getGame().getStatus() == GameStatus.WAITING) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        // This is already in 1.8.8, but in older 1.8.x versions not (need to check 1.8.8/9 version)
        if (event.isCancelled()) {
            return;
        }

        for (String s : Main.getGameNames()) {
            Game game = Main.getGame(s);
            if (game.getStatus() == GameStatus.RUNNING && game.getOriginalOrInheritedSpawnerDisableMerge()) {
                if (GameCreator.isInArea(event.getEntity().getLocation(), game.getPos1(), game.getPos2()) || GameCreator.isInArea(event.getTarget().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            if (game.getOriginalOrInheritedDamageWhenPlayerIsNotInArena() && game.getStatus() == GameStatus.RUNNING
                    && !gPlayer.isSpectator) {
                if (!GameCreator.isInArea(event.getTo(), game.getPos1(), game.getPos2())) {
                    AttributeInstance armor = player.getAttribute(Attribute.GENERIC_ARMOR);
                    AttributeInstance armorToughness = null;
                    if (Arrays.stream(Attribute.values()).anyMatch(attribute -> "GENERIC_ARMOR_TOUGHNESS".equals(attribute.name()))) {
                        armorToughness = player.getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
                    }
                    if (armor == null) {
                        player.damage(5);
                    } else {
                        // this is not 100% accurate formula - armorToughness check contains weaponDamage which is hardcoded to 5 (4*5=20) but we don't know the weapon damage yet
                        double multiplier = (1.0 - Math.min(20.0, Math.max(armor.getValue() / 5.0, armor.getValue() - 20.0 / ((armorToughness != null ? armorToughness.getValue() : 0) + 8))) / 25.0);
                        if (multiplier < 1) {
                            multiplier = 2 - multiplier;
                        }
                        double weaponDamage = multiplier * 5.0;
                        player.damage(weaponDamage);
                    }
                }
            } else if (Main.getConfigurator().config.getBoolean("preventSpectatorFlyingAway", false) && gPlayer.isSpectator && (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING)) {
                if (!GameCreator.isInArea(event.getTo(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
