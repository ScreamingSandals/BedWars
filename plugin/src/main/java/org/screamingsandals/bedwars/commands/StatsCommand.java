package org.screamingsandals.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.statistics.PlayerStatistic;
import org.screamingsandals.bedwars.utils.Permissions;
import org.screamingsandals.lib.screamingcommands.base.annotations.RegisterCommand;
import org.screamingsandals.lib.screamingcommands.base.interfaces.IBasicCommand;

import java.util.List;

import static misat11.lib.lang.I.m;
import static misat11.lib.lang.I.mpr;

@RegisterCommand(commandName = "bw", subCommandName = "stats")
public class StatsCommand implements IBasicCommand {

    public StatsCommand(Main main) {
    }

    @Override
    public String getPermission() {
        return Permissions.BASE.permission;
    }

    @Override
    public String getDescription() {
        return "BedWars Statistics command";
    }

    @Override
    public String getUsage() {
        return "/bw stats <player name>";
    }

    @Override
    public String getInvalidUsageMessage() {
        return mpr("commands.errors.unknown_usage").get();
    }

    @Override
    public boolean onPlayerCommand(Player player, List<String> args) {
        if (!Main.isPlayerStatisticsEnabled()) {
            mpr("commands.statistics.disabled").send(player);
        } else {
            if (args.size() >= 1) {
                if (!player.hasPermission(Permissions.SEE_OTHER_STATS.permission)
                        && !player.hasPermission(Permissions.ADMIN.permission)) {
                    mpr("commands.errors.no_permissions").send(player);
                } else {
                    String name = args.get(0);
                    OfflinePlayer offlinePlayer = Main.getInstance().getServer().getPlayerExact(name);

                    if (offlinePlayer == null) {
                        mpr("commands.statistics.not_found").send(player);
                    } else {
                        PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(offlinePlayer);
                        if (statistic == null) {
                            mpr("commands.statistics.not_found").send(player);
                        } else {
                            sendStats(player, statistic);
                        }
                    }
                }
            } else {
                PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(player);
                if (statistic == null) {
                    mpr("commands.statistics.not_found").send(player);
                } else {
                    sendStats(player, statistic);
                }
            }
        }
        return true;
    }

    @Override
    public boolean onConsoleCommand(ConsoleCommandSender consoleCommandSender, List<String> args) {
        if (!Main.isPlayerStatisticsEnabled()) {
            mpr("commands.statistics.disabled").send(consoleCommandSender);
        } else {
            if (args.size() >= 1) {
                String name = args.get(0);
                OfflinePlayer offlinePlayer = Main.getInstance().getServer().getPlayerExact(name);

                if (offlinePlayer == null) {
                    mpr("commands.statistics.not_found").send(consoleCommandSender);
                } else {
                    PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(offlinePlayer);
                    if (statistic == null) {
                        mpr("commands.statistics.not_found").send(consoleCommandSender);
                    } else {
                        sendStats(consoleCommandSender, statistic);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onPlayerTabComplete(Player player, Command command, List<String> completion, List<String> args) {
        if (args.size() == 1 && Main.isPlayerStatisticsEnabled()
                && (player.hasPermission(Permissions.SEE_OTHER_STATS.permission)
                || player.hasPermission(Permissions.ADMIN.permission))) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completion.add(p.getName());
            }
        }
    }

    @Override
    public void onConsoleTabComplete(ConsoleCommandSender consoleCommandSender, Command command, List<String> completion, List<String> args) {
        if (args.size() == 1 && Main.isPlayerStatisticsEnabled()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completion.add(p.getName());
            }
        }
    }

    private void sendStats(CommandSender player, PlayerStatistic statistic) {
        m("commands.statistics.header").replace("%player%", statistic.getName()).send(player);
        m("commands.statistics.kills")
                .replace("%kills%", Integer.toString(statistic.getKills() + statistic.getCurrentKills()))
                .send(player);
        m("commands.statistics.deaths")
                .replace("%deaths%", Integer.toString(statistic.getDeaths() + statistic.getCurrentDeaths()))
                .send(player);
        m("commands.statistics.kd")
                .replace("%kd%", Double.toString(statistic.getCurrentKD()))
                .send(player);
        m("commands.statistics.wins")
                .replace("%wins%", Integer.toString(statistic.getWins() + statistic.getCurrentWins()))
                .send(player);
        m("commands.statistics.loses")
                .replace("%loses%", Integer.toString(statistic.getLoses() + statistic.getCurrentLoses()))
                .send(player);
        m("commands.statistics.games")
                .replace("%games%", Integer.toString(statistic.getGames() + statistic.getCurrentGames()))
                .send(player);
        m("commands.statistics.beds")
                .replace("%beds%", Integer.toString(statistic.getDestroyedBeds() + statistic.getCurrentDestroyedBeds()))
                .send(player);
        m("commands.statistics.score")
                .replace("%score%", Integer.toString(statistic.getScore() + statistic.getCurrentScore()))
                .send(player);
    }
}
