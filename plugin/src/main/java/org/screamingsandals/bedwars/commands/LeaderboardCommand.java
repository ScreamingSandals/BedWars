package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

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
                    if (!PlayerStatisticManager.isEnabled()) {
                        mpr("statistics_is_disabled").send(sender);
                    } else {
                        int max = MainConfig.getInstance().node("holograms", "leaderboard", "size").getInt();
                        mpr("leaderboard_header").replace("number", max).send(sender);

                        var statistics = PlayerStatisticManager.getInstance().getLeaderboard(max);
                        if (statistics.isEmpty()) {
                            m("leaderboard_no_scores").send(sender);
                        } else {
                            var l = new AtomicInteger(1);
                            statistics.forEach(leaderboardEntry ->
                                m("leaderboard_line")
                                        .replace("order", l.getAndIncrement())
                                        .replace("player", leaderboardEntry
                                                .getPlayer()
                                                .getLastName()
                                                .orElse(leaderboardEntry
                                                        .getPlayer()
                                                        .getUuid()
                                                        .toString()
                                                )
                                        )
                                        .replace("score", leaderboardEntry.getTotalScore())
                                        .send(sender)
                            );
                        }
                    }
                })
        );
    }
}
