/*
 * Copyright (C) 2022 ScreamingSandals
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

import org.bukkit.command.CommandSender;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.statistics.LeaderboardEntry;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.screamingsandals.bedwars.lib.lang.I18n.*;

public class LeaderboardCommand extends BaseCommand {

    public LeaderboardCommand() {
        super("leaderboard", LEADERBOARD_PERMISSION, true, Main.getConfigurator().config.getBoolean("default-permissions.leaderboard"));
    }

    @Override
    public boolean execute(CommandSender sender, List<String> args) {
        if (!Main.isPlayerStatisticsEnabled()) {
            mpr("statistics_is_disabled").send(sender);
        } else {
            int max = Main.getConfigurator().config.getInt("holograms.leaderboard.size");
            mpr("leaderboard_header").replace("number", max).send(sender);

            List<LeaderboardEntry> statistics = Main.getPlayerStatisticsManager().getLeaderboard(max);
            if (statistics.isEmpty()) {
                m("leaderboard_no_scores").send(sender);
            } else {
                AtomicInteger l = new AtomicInteger(1);
                statistics.forEach(leaderboardEntry -> {
                    m("leaderboard_line").replace("order", l.getAndIncrement())
                            .replace("player", leaderboardEntry.getPlayer().getName() != null ? leaderboardEntry.getPlayer().getName() : (leaderboardEntry.getLatestKnownName() != null ? leaderboardEntry.getLatestKnownName() : leaderboardEntry.getPlayer().getUniqueId().toString()))
                            .replace("score", leaderboardEntry.getTotalScore()).send(sender);
                });
            }
        }
        return true;
    }

    @Override
    public void completeTab(List<String> completion, CommandSender sender, List<String> args) {

    }

}
