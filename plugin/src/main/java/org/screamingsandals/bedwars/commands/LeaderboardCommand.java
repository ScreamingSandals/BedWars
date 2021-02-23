package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.statistics.LeaderboardEntry;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.screamingsandals.bedwars.lib.lang.I.m;
import static org.screamingsandals.bedwars.lib.lang.I.mpr;

public class LeaderboardCommand extends BaseCommand {
    public LeaderboardCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "leaderboard", BedWarsPermission.LEADERBOARD_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                .handler(commandContext -> {
                    var sender = commandContext.getSender();
                    if (!Main.isPlayerStatisticsEnabled()) {
                        mpr("statistics_is_disabled").send(sender);
                    } else {
                        int max = MainConfig.getInstance().node("holograms", "leaderboard", "size").getInt();
                        mpr("leaderboard_header").replace("number", max).send(sender);

                        List<LeaderboardEntry> statistics = Main.getPlayerStatisticsManager().getLeaderboard(max);
                        if (statistics.isEmpty()) {
                            m("leaderboard_no_scores").send(sender);
                        } else {
                            var l = new AtomicInteger(1);
                            statistics.forEach(leaderboardEntry -> {
                                m("leaderboard_line").replace("order", l.getAndIncrement()).replace("player", leaderboardEntry.getPlayer().getName())
                                        .replace("score", leaderboardEntry.getTotalScore()).send(sender);
                            });
                        }
                    }
                })
        );
    }
}
