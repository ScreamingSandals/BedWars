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

package org.screamingsandals.bedwars.utils;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.plugin.PluginManager;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;

@Service
public class PerWorldInventoryCompatibilityFix {
    @OnPostEnable
    public void applyFix(Plugin plugin) {
        try {
            var key = PluginManager.createKey("PerWorldInventory").orElseThrow();
            if (PluginManager.isEnabled(key)) {
                final var pwi = PluginManager.getPlatformClass(key).orElseThrow();
                if (pwi.getClass().getName().equals("me.ebonjaeger.perworldinventory.PerWorldInventory")) {
                    // Kotlin version
                    plugin.getServer().getPluginManager().registerEvents(new Listener() {
                        @EventHandler
                        public void onInventoryChange(me.ebonjaeger.perworldinventory.event.InventoryLoadEvent event) {
                            var player = event.getPlayer();
                            if (PlayerManagerImpl.getInstance().isPlayerInGame(player.getUniqueId())) {
                                BedWarsPlayer gPlayer = PlayerManagerImpl.getInstance().getPlayer(player.getUniqueId()).orElseThrow();
                                if (gPlayer.getGame() != null || gPlayer.isTeleportingFromGame_justForInventoryPlugins) {
                                    gPlayer.isTeleportingFromGame_justForInventoryPlugins = false;
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }, plugin);
                } else {
                    // Legacy version
                    plugin.getServer().getPluginManager().registerEvents(new Listener() {
                        @EventHandler
                        public void onInventoryChange(me.gnat008.perworldinventory.events.InventoryLoadEvent event) {
                            var player = event.getPlayer();
                            if (PlayerManagerImpl.getInstance().isPlayerInGame(player.getUniqueId())) {
                                BedWarsPlayer gPlayer = PlayerManagerImpl.getInstance().getPlayer(player.getUniqueId()).orElseThrow();
                                if (gPlayer.getGame() != null || gPlayer.isTeleportingFromGame_justForInventoryPlugins) {
                                    gPlayer.isTeleportingFromGame_justForInventoryPlugins = false;
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }, plugin);
                }
            }

        } catch (Throwable ignored) {
            // maybe something here can cause exception
        }
    }
}
