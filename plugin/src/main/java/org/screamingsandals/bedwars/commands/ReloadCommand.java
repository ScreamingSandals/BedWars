package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class ReloadCommand extends BaseCommand {
    public ReloadCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "reload", BedWarsPermission.ADMIN_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                    .handler(commandContext -> {
                        var sender = commandContext.getSender();

                        sender.sendMessage(i18n("safe_reload"));

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
                                    sender.sendMessage(i18n("safe_reload_failed_to_stop_game"));
                                }

                                if (!gameRuns || timer == 0) {
                                    this.cancel();
                                    plugin.getPluginLoader().disablePlugin(plugin);
                                    plugin.getPluginLoader().enablePlugin(plugin);
                                    sender.sendMessage("Plugin reloaded!");
                                    return;
                                }
                                timer--;
                            }

                        }.runTaskTimer(plugin, 0L, 20L);
                    })
        );
    }
}
