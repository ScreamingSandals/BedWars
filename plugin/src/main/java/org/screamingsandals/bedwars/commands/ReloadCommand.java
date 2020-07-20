package org.screamingsandals.bedwars.commands;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

import static misat11.lib.lang.I18n.i18n;

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
                    Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
                    Bukkit.getServer().getPluginManager().enablePlugin(Main.getInstance());
                    sender.sendMessage("Plugin reloaded!");
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
