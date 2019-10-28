package org.screamingsandals.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;

import java.util.List;

import static misat11.lib.lang.I.m;

public class ReloadCommand extends BaseCommand {

    public ReloadCommand() {
        super("reload", ADMIN_PERMISSION, true);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        m("commands.reload.safe").send(sender);

        for (String game : Main.getGameNames()) {
            Main.getGame(game).stop();
        }

        new BukkitRunnable() {
            int timer = 60;

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
                    m("commands.reload.failed").send(sender);
                }

                if (!gameRuns || timer == 0) {
                    this.cancel();
                    Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
                    Bukkit.getServer().getPluginManager().enablePlugin(Main.getInstance());
                    sender.sendMessage("Plugin was reloaded! YAAY.");
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
