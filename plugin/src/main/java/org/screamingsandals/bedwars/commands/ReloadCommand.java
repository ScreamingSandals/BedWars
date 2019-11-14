package org.screamingsandals.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.utils.Permissions;
import org.screamingsandals.lib.screamingcommands.base.annotations.RegisterCommand;
import org.screamingsandals.lib.screamingcommands.base.interfaces.IBasicCommand;

import java.util.List;

import static misat11.lib.lang.I.m;
import static misat11.lib.lang.I.mpr;

@RegisterCommand(commandName = "bw", subCommandName = "reload")
public class ReloadCommand implements IBasicCommand {

    public ReloadCommand(Main main) {
    }

    @Override
    public String getPermission() {
        return Permissions.ADMIN.permission;
    }

    @Override
    public String getDescription() {
        return "BedWars Reload command";
    }

    @Override
    public String getUsage() {
        return "/bw reload";
    }

    @Override
    public String getInvalidUsageMessage() {
        return ""; //how
    }

    @Override
    public boolean onPlayerCommand(Player player, List<String> list) {
        doReload(player);
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender consoleCommandSender, List<String> list) {
        doReload(consoleCommandSender);
        return true;
    }

    @Override
    public void onPlayerTabComplete(Player player, Command command, List<String> list, List<String> list1) {

    }

    @Override
    public void onConsoleTabComplete(ConsoleCommandSender consoleCommandSender, Command command, List<String> list, List<String> list1) {

    }

    private void doReload(Object object) {
        mpr("commands.reload.safe").send(object);

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
                    m("commands.reload.failed").send(object);
                }

                if (!gameRuns || timer == 0) {
                    this.cancel();
                    Bukkit.getServer().getPluginManager().disablePlugin(Main.getInstance());
                    Bukkit.getServer().getPluginManager().enablePlugin(Main.getInstance());
                    return;
                }
                timer--;
            }

        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }
}
