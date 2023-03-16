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

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.statistics.PlayerStatistic;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class StatsCommand extends BaseCommand {

    public StatsCommand() {
        super("stats", STATS_PERMISSION, true, Main.getConfigurator().config.getBoolean("default-permissions.stats"));
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!Main.isPlayerStatisticsEnabled()) {
            sender.sendMessage(i18n("statistics_is_disabled"));
        } else {
            if (args.size() >= 1) {
                if (!hasPermission(sender, OTHER_STATS_PERMISSION, false) && !hasPermission(sender, ADMIN_PERMISSION, false)) {
                    sender.sendMessage(i18n("no_permissions"));
                } else {
                    String name = args.get(0);
                    OfflinePlayer off = Main.getInstance().getServer().getPlayerExact(name);

                    if (off == null) {
                        sender.sendMessage(i18n("statistics_player_is_not_exists"));
                    } else {
                        PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(off);
                        if (statistic == null) {
                            sender.sendMessage(i18n("statistics_not_found"));
                        } else {
                            sendStats(sender, statistic);
                        }
                    }
                }
            } else {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(player);
                    if (statistic == null) {
                        player.sendMessage(i18n("statistics_not_found"));
                    } else {
                        sendStats(player, statistic);
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
                && (hasPermission(sender, OTHER_STATS_PERMISSION, false) && hasPermission(sender, ADMIN_PERMISSION, false))) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completion.add(p.getName());
            }
        }
    }

    public static void sendStats(CommandSender player, PlayerStatistic statistic) {
        player.sendMessage(i18n("statistics_header").replace("%player%", statistic.getName()));

        player.sendMessage(i18n("statistics_kills", false).replace("%kills%",
                Integer.toString(statistic.getKills())));
        player.sendMessage(i18n("statistics_deaths", false).replace("%deaths%",
                Integer.toString(statistic.getDeaths())));
        player.sendMessage(i18n("statistics_kd", false).replace("%kd%",
                Double.toString(statistic.getKD())));
        player.sendMessage(i18n("statistics_wins", false).replace("%wins%",
                Integer.toString(statistic.getWins())));
        player.sendMessage(i18n("statistics_loses", false).replace("%loses%",
                Integer.toString(statistic.getLoses())));
        player.sendMessage(i18n("statistics_games", false).replace("%games%",
                Integer.toString(statistic.getGames())));
        player.sendMessage(i18n("statistics_beds", false).replace("%beds%",
                Integer.toString(statistic.getDestroyedBeds())));
        player.sendMessage(i18n("statistics_score", false).replace("%score%",
                Integer.toString(statistic.getScore())));
    }

}
