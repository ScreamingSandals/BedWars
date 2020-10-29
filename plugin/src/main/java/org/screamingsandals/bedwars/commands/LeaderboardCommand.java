package org.screamingsandals.bedwars.commands;

import org.bukkit.command.CommandSender;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.statistics.LeaderboardEntry;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static misat11.lib.lang.I18n.*;

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
                    m("leaderboard_line").replace("order", l.getAndIncrement()).replace("player", leaderboardEntry.getPlayer().getName())
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
