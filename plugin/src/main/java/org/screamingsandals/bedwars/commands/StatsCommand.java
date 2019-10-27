package org.screamingsandals.bedwars.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.statistics.PlayerStatistic;

import java.util.List;

import static misat11.lib.lang.I.m;
import static misat11.lib.lang.I18n.i18n;

public class StatsCommand extends BaseCommand {

    public StatsCommand() {
        super("stats", null, true);
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!Main.isPlayerStatisticsEnabled()) {
            m("commands.statistics.disabled").send(sender);
        } else {
            if (args.size() >= 1) {
                if (!sender.hasPermission(OTHER_STATS_PERMISSION) && !sender.hasPermission(ADMIN_PERMISSION)) {
                    m("commands.errors.no_permissions").send(sender);
                } else {
                    String name = args.get(0);
                    OfflinePlayer offlinePlayer = Main.getInstance().getServer().getPlayerExact(name);

                    if (offlinePlayer == null) {
                        m("commands.statistics.not_found").send(sender);
                    } else {
                        PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(offlinePlayer);
                        if (statistic == null) {
                            m("commands.statistics.not_found").send(sender);
                        } else {
                            this.sendStats(sender, statistic);
                        }
                    }
                }
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(player);
                    if (statistic == null) {
                        m("commands.statistics.not_found").send(sender);
                    } else {
                        this.sendStats(player, statistic);
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {
        if (args.size() == 1 && Main.isPlayerStatisticsEnabled()
                && (sender.hasPermission(OTHER_STATS_PERMISSION) || sender.hasPermission(ADMIN_PERMISSION))) {
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
                .replace("%kd%", Double.toString(statistic.getKD() + statistic.getCurrentKD()))
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
