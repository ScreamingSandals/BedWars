package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.reflect.Reflect;

import java.util.logging.Level;

@Service
public class ReloadCommand extends BaseCommand {
    public ReloadCommand() {
        super("reload", BedWarsPermission.ADMIN_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                    .handler(commandContext -> {
                        var sender = commandContext.getSender();

                        sender.sendMessage(Message.of(LangKeys.SAFE_RELOAD).defaultPrefix());

                        GameManager.getInstance().getGames().forEach(Game::stop);
                        var plugin = Main.getInstance().getPluginDescription().as(JavaPlugin.class);

                        new BukkitRunnable() {
                            public int timer = 60;

                            @Override
                            public void run() {
                                boolean gameRuns = false;
                                for (var game : GameManager.getInstance().getGames()) {
                                    if (game.getStatus() != GameStatus.DISABLED) {
                                        gameRuns = true;
                                        break;
                                    }
                                }

                                if (gameRuns && timer == 0) {
                                    sender.sendMessage(Message.of(LangKeys.SAFE_RELOAD_FAILED_TO_STOP_GAME).defaultPrefix());
                                }

                                if (!gameRuns || timer == 0) {
                                    this.cancel();
                                    try {
                                        String message = String.format("Disabling %s", plugin.getDescription().getFullName());
                                        plugin.getLogger().info(message);
                                        Bukkit.getPluginManager().callEvent(new PluginDisableEvent(plugin));
                                        Reflect.getMethod(plugin, "setEnabled", boolean.class).invoke(false);
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
                                        for (var world : Bukkit.getWorlds()) {
                                            world.removePluginChunkTickets(plugin);
                                        }
                                    } catch (Throwable ex) {
                                        Bukkit.getLogger().log(Level.SEVERE, "Error occurred (in the plugin loader) while removing chunk tickets for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                                    }
                                    Bukkit.getServer().getPluginManager().enablePlugin(plugin);
                                    sender.sendMessage("Plugin reloaded! Keep in mind that restarting the server is safer!");
                                    return;
                                }
                                timer--;
                            }

                        }.runTaskTimer(plugin, 0L, 20L);
                    })
        );
    }
}
