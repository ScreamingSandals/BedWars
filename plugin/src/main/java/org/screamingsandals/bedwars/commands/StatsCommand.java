package org.screamingsandals.bedwars.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.bukkit.Bukkit;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.statistics.PlayerStatistic;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.OfflinePlayerWrapper;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatsCommand extends BaseCommand {
    public StatsCommand() {
        super("stats", BedWarsPermission.STATS_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(manager
                        .argumentBuilder(String.class, "player")
                                .withSuggestionsProvider((c, s) -> {
                                    if (PlayerStatisticManager.isEnabled()
                                            && (c.getSender().hasPermission(BedWarsPermission.OTHER_STATS_PERMISSION.asPermission()) && !c.getSender().hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission()))) {
                                        return PlayerMapper.getPlayers().stream().map(PlayerWrapper::getName).collect(Collectors.toList());
                                    }
                                    return List.of();
                                })
                                .asOptional()
                        )
                    .handler(commandContext -> {
                        var sender = commandContext.getSender();

                        if (!PlayerStatisticManager.isEnabled()) {
                            sender.sendMessage(Message.of(LangKeys.STATISTICS_DISABLED).defaultPrefix());
                        } else {
                            var playerName = commandContext.<String>getOptional("player");

                            if (playerName.isPresent()) {
                                if (!sender.hasPermission(BedWarsPermission.OTHER_STATS_PERMISSION.asPermission()) && !sender.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                                    sender.sendMessage(Message.of(LangKeys.NO_PERMISSIONS).defaultPrefix());
                                } else {
                                    var name = playerName.get();
                                    // TODO: add this feature to PlayerMapper
                                    var off = Bukkit.getServer().getPlayerExact(name);

                                    if (off == null) {
                                        sender.sendMessage(Message.of(LangKeys.STATISTICS_PLAYER_DOES_NOT_EXIST).defaultPrefix());
                                    } else {
                                        var statistic = PlayerStatisticManager.getInstance().getStatistic(PlayerMapper.wrapOfflinePlayer(off));
                                        if (statistic == null) {
                                            sender.sendMessage(Message.of(LangKeys.STATISTICS_NOT_FOUND).defaultPrefix());
                                        } else {
                                            sendStats(sender, statistic);
                                        }
                                    }
                                }
                            } else {
                                if (sender.getType() == CommandSenderWrapper.Type.PLAYER) {
                                    var statistic = PlayerStatisticManager.getInstance().getStatistic(sender.as(OfflinePlayerWrapper.class));
                                    if (statistic == null) {
                                        sender.sendMessage(Message.of(LangKeys.STATISTICS_NOT_FOUND).defaultPrefix());
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
        Message
                .of(LangKeys.STATISTICS_HEADER)
                .join(LangKeys.STATISTICS_KILLS)
                .join(LangKeys.STATISTICS_DEATHS)
                .join(LangKeys.STATISTICS_KD)
                .join(LangKeys.STATISTICS_WINS)
                .join(LangKeys.STATISTICS_LOSES)
                .join(LangKeys.STATISTICS_GAMES)
                .join(LangKeys.STATISTICS_BEDS)
                .join(LangKeys.STATISTICS_SCORE)
                .placeholder("player", statistic.getName())
                .placeholder("kills", statistic.getKills())
                .placeholder("deaths", statistic.getDeaths())
                .placeholder("kd", statistic.getKD())
                .placeholder("wins", statistic.getWins())
                .placeholder("loses", statistic.getLoses())
                .placeholder("games", statistic.getGames())
                .placeholder("beds", statistic.getDestroyedBeds())
                .placeholder("score", statistic.getScore())
                .defaultPrefix()
                .prefixPolicy(Message.PrefixPolicy.FIRST_MESSAGE)
                .send(sender);
    }
}
