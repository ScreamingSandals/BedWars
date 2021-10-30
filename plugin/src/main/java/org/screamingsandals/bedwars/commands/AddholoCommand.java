package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.context.CommandContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.screamingsandals.bedwars.holograms.LeaderboardHolograms;
import org.screamingsandals.bedwars.holograms.StatisticsHolograms;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class AddholoCommand extends BaseCommand {
    public AddholoCommand() {
        super("addholo", BedWarsPermission.ADMIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(this::executeStatsHologram)
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("stats")
                        .handler(this::executeStatsHologram)
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("leaderboard")
                        .handler(commandContext -> {
                            var sender = commandContext.getSender();
                            var eyeLocation = sender.as(PlayerWrapper.class).getEyeLocation();
                            if (!LeaderboardHolograms.isEnabled()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_HOLO_NOT_ENABLED).defaultPrefix());
                            } else {
                                LeaderboardHolograms.getInstance().addHologramLocation(eyeLocation);
                                sender.sendMessage(Message.of(LangKeys.LEADERBOARD_HOLO_ADDED).defaultPrefix());
                            }
                        })
        );
    }

    private void executeStatsHologram(@NonNull CommandContext<CommandSenderWrapper> commandContext) {
        var sender = commandContext.getSender();
        var eyeLocation = sender.as(PlayerWrapper.class).getEyeLocation();
        if (!StatisticsHolograms.isEnabled()) {
            sender.sendMessage(Message.of(LangKeys.ADMIN_HOLO_NOT_ENABLED).defaultPrefix());
        } else {
            var statisticHolograms = StatisticsHolograms.getInstance();
            statisticHolograms.addHologramLocation(eyeLocation);
            statisticHolograms.updateHolograms();
            sender.sendMessage(Message.of(LangKeys.ADMIN_HOLO_ADDED).defaultPrefix());
        }
    }
}
