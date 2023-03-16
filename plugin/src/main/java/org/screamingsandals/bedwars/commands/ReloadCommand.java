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

package org.screamingsandals.bedwars.commands;

import org.bukkit.World;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

import java.io.File;
import java.util.List;
import java.util.logging.Level;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class ReloadCommand extends BaseCommand {

    public ReloadCommand() {
        super("reload", ADMIN_PERMISSION, true, false);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        sender.sendMessage(i18n("safe_reload"));

        for (String game : Main.getGameNames()) {
            Main.getGame(game).stop();
        }

        new BukkitRunnable() {
            public int timer = 60;

            @Override
            public void run() {
                boolean gameRuns = false;
                for (String game : Main.getGameNames()) {
                    if (Main.getGame(game).getStatus() != GameStatus.DISABLED) {
                        gameRuns = true;
                        break;
                    }
                }

                if (gameRuns && timer == 0) {
                    sender.sendMessage(i18n("safe_reload_failed_to_stop_game"));
                }

                if (!gameRuns || timer == 0) {
                    this.cancel();
                    /*if (ClassStorage.IS_PAPER_SERVER) {
                        Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance(), false);
                        Bukkit.getServer().getPluginManager().enablePlugin(Main.getInstance());
                        sender.sendMessage("Plugin reloaded!");
                    } else {*/
                        Plugin plugin = Main.getInstance();
                        try {
                            String message = String.format("Disabling %s", plugin.getDescription().getFullName());
                            plugin.getLogger().info(message);
                            Bukkit.getPluginManager().callEvent(new PluginDisableEvent(plugin));
                            Main.getInstance().se(false);
                        } catch (Throwable ex) {
                            Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                        }

                        try {
                            Bukkit.getScheduler().cancelTasks(plugin);
                        } catch (Throwable ex) {
                            Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while cancelling tasks for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                        }

                        try {
                            Bukkit.getServicesManager().unregisterAll(plugin);
                        } catch (Throwable ex) {
                            Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering services for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                        }

                        try {
                            HandlerList.unregisterAll(plugin);
                        } catch (Throwable ex) {
                            Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering events for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                        }

                        try {
                            Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin);
                            Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin);
                        } catch (Throwable ex) {
                            Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while unregistering plugin channels for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                        }

                        try {
                            for (World world : Bukkit.getWorlds()) {
                                world.removePluginChunkTickets(plugin);
                            }
                        } catch (Throwable ex) {
                            // older versions don't even have chunk tickets
                            //Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while removing chunk tickets for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                        }
                        Bukkit.getServer().getPluginManager().enablePlugin(Main.getInstance());
                        sender.sendMessage("Plugin reloaded! Keep in mind that restarting the server is safer!");
                    //}
                    return;
                }
                timer--;
            }

        }.runTaskTimer(Main.getInstance(), 0L, 20L);
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        // Nothing to add.
    }

}
