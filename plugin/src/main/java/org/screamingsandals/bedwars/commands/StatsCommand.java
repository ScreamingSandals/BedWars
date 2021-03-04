package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.statistics.PlayerStatistic;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.lib.player.OfflinePlayerWrapper;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

public class StatsCommand extends BaseCommand {
    public StatsCommand(CommandManager<CommandSenderWrapper> manager) {
        super(manager, "stats", BedWarsPermission.STATS_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(manager
                        .argumentBuilder(String.class, "player")
                                .withSuggestionsProvider((c, s) -> {
                                    if (PlayerStatisticManager.isEnabled()
                                            && (c.getSender().hasPermission(BedWarsPermission.OTHER_STATS_PERMISSION.asPermission()) && !c.getSender().hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission()))) {
                                        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
                                    }
                                    return List.of();
                                })
                                .asOptional()
                        )
                    .handler(commandContext -> {
                        var sender = commandContext.getSender();

                        if (!PlayerStatisticManager.isEnabled()) {
                            sender.sendMessage(i18n("statistics_is_disabled"));
                        } else {
                            var playerName = commandContext.<String>getOptional("player");

                            if (playerName.isPresent()) {
                                if (!sender.hasPermission(BedWarsPermission.OTHER_STATS_PERMISSION.asPermission()) && !sender.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                                    sender.sendMessage(i18n("no_permissions"));
                                } else {
                                    var name = playerName.get();
                                    // TODO: add this feature to PlayerMapper
                                    var off = Bukkit.getServer().getPlayerExact(name);

                                    if (off == null) {
                                        sender.sendMessage(i18n("statistics_player_is_not_exists"));
                                    } else {
                                        var statistic = PlayerStatisticManager.getInstance().getStatistic(PlayerMapper.wrapOfflinePlayer(off));
                                        if (statistic == null) {
                                            sender.sendMessage(i18n("statistics_not_found"));
                                        } else {
                                            sendStats(sender, statistic);
                                        }
                                    }
                                }
                            } else {
                                if (sender.getType() == CommandSenderWrapper.Type.PLAYER) {
                                    var statistic = PlayerStatisticManager.getInstance().getStatistic(sender.as(OfflinePlayerWrapper.class));
                                    if (statistic == null) {
                                        sender.sendMessage(i18n("statistics_not_found"));
                                    } else {
                                        sendStats(sender, statistic);
                                    }
                                }
                            }
                        }
                    })
        );
    }

    public static void sendStats(CommandSenderWrapper sender, PlayerStatistic statistic) {
        sender.sendMessage(i18n("statistics_header").replace("%player%", statistic.getName()));

        sender.sendMessage(i18n("statistics_kills", false).replace("%kills%",
                Integer.toString(statistic.getKills())));
        sender.sendMessage(i18n("statistics_deaths", false).replace("%deaths%",
                Integer.toString(statistic.getDeaths())));
        sender.sendMessage(i18n("statistics_kd", false).replace("%kd%",
                Double.toString(statistic.getKD())));
        sender.sendMessage(i18n("statistics_wins", false).replace("%wins%",
                Integer.toString(statistic.getWins())));
        sender.sendMessage(i18n("statistics_loses", false).replace("%loses%",
                Integer.toString(statistic.getLoses())));
        sender.sendMessage(i18n("statistics_games", false).replace("%games%",
                Integer.toString(statistic.getGames())));
        sender.sendMessage(i18n("statistics_beds", false).replace("%beds%",
                Integer.toString(statistic.getDestroyedBeds())));
        sender.sendMessage(i18n("statistics_score", false).replace("%score%",
                Integer.toString(statistic.getScore())));
    }
}
