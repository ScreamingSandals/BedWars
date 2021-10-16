package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.reflect.Reflect;

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

                        reload(sender);
                    })
        );
    }

    public static void reload(CommandSenderWrapper sender) {
        sender.sendMessage(Message.of(LangKeys.SAFE_RELOAD).defaultPrefix());

        for (var game : GameManagerImpl.getInstance().getGameNames()) {
            GameManagerImpl.getInstance().getGame(game).ifPresent(GameImpl::stop);
        }

        var logger = BedWarsPlugin.getInstance().getLogger();
        var plugin = BedWarsPlugin.getInstance().getPluginDescription().as(JavaPlugin.class);

        Tasker.build(taskBase -> new Runnable() {
            public int timer = 60;

            @Override
            public void run() {
                boolean gameRuns = false;
                for (var game : GameManagerImpl.getInstance().getGames()) {
                    if (game.getStatus() != GameStatus.DISABLED) {
                        gameRuns = true;
                        break;
                    }
                }

                if (gameRuns && timer == 0) {
                    sender.sendMessage(Message.of(LangKeys.SAFE_RELOAD_FAILED_TO_STOP_GAME).defaultPrefix());
                }

                if (!gameRuns || timer == 0) {
                    taskBase.cancel();
                    try {
                        logger.info(String.format("Disabling %s", plugin.getDescription().getFullName()));
                        Bukkit.getPluginManager().callEvent(new PluginDisableEvent(plugin));
                        Reflect.getMethod(plugin, "setEnabled", boolean.class).invoke(false);
                    } catch (Throwable ex) {
                        logger.trace("Error occurred (in the plugin loader) while disabling " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                    }

                    try {
                        Tasker.cancelAll();
                    } catch (Throwable ex) {
                        logger.trace("Error occurred (in the plugin loader) while cancelling tasks for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                    }

                    try {
                        Bukkit.getServicesManager().unregisterAll(plugin);
                    } catch (Throwable ex) {
                        logger.trace("Error occurred (in the plugin loader) while unregistering services for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                    }

                    try {
                        HandlerList.unregisterAll(plugin);
                    } catch (Throwable ex) {
                        logger.trace("Error occurred (in the plugin loader) while unregistering events for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                    }

                    try {
                        Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin);
                        Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin);
                    } catch (Throwable ex) {
                        logger.trace("Error occurred (in the plugin loader) while unregistering plugin channels for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                    }

                    try {
                        for (var world : Bukkit.getWorlds()) {
                            world.removePluginChunkTickets(plugin);
                        }
                    } catch (Throwable ex) {
                        logger.trace("Error occurred (in the plugin loader) while removing chunk tickets for " + plugin.getDescription().getFullName() + " (Is it up to date?)", ex);
                    }
                    Bukkit.getServer().getPluginManager().enablePlugin(plugin);
                    sender.sendMessage("Plugin reloaded! Keep in mind that restarting the server is safer!");
                    return;
                }
                timer--;
            }
        }).repeat(20, TaskerTime.TICKS).start();
    }
}
