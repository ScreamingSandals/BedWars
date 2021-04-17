package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LeaderboardCommand extends BaseCommand {
    public LeaderboardCommand() {
        super("leaderboard", BedWarsPermission.LEADERBOARD_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                .handler(commandContext -> {
                    var sender = commandContext.getSender();
                    if (!PlayerStatisticManager.isEnabled()) {
                        Message.of(LangKeys.STATISTICS_DISABLED).defaultPrefix().send(sender);
                    } else {
                        int max = MainConfig.getInstance().node("holograms", "leaderboard", "size").getInt();
                        Message
                                .of(LangKeys.LEADERBOARD_HEADER)
                                .defaultPrefix()
                                .placeholder("number", max)
                                .send(sender);

                        var statistics = PlayerStatisticManager.getInstance().getLeaderboard(max);
                        if (statistics.isEmpty()) {
                            Message.of(LangKeys.LEADERBOARD_NO_SCORES).defaultPrefix().send(sender);
                        } else {
                            var l = new AtomicInteger(1);
                            statistics.forEach(leaderboardEntry ->
                                Message.of(LangKeys.LEADERBOARD_LINE)
                                        .placeholder("order", l.getAndIncrement())
                                        .placeholder("player", leaderboardEntry
                                                .getPlayer()
                                                .getLastName()
                                                .orElse(leaderboardEntry
                                                        .getPlayer()
                                                        .getUuid()
                                                        .toString()
                                                )
                                        )
                                        .placeholder("score", leaderboardEntry.getTotalScore())
                                        .send(sender)
                            );
                        }
                    }
                })
        );
    }
}
