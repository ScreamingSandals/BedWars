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

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.statistics.PlayerStatisticImpl;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.OfflinePlayer;
import org.screamingsandals.lib.player.Players;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatsCommand extends BaseCommand {
    public StatsCommand() {
        super("stats", BedWarsPermission.STATS_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(manager
                        .argumentBuilder(String.class, "player")
                                .withSuggestionsProvider((c, s) -> {
                                    if (PlayerStatisticManager.isEnabled()
                                            && (c.getSender().hasPermission(BedWarsPermission.OTHER_STATS_PERMISSION.asPermission()) && !c.getSender().hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission()))) {
                                        return Server.getConnectedPlayers().stream().map(Player::getName).collect(Collectors.toList());
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
                                    var off = Players.getPlayerExact(name);

                                    if (off == null) {
                                        sender.sendMessage(Message.of(LangKeys.STATISTICS_PLAYER_DOES_NOT_EXIST).defaultPrefix());
                                    } else {
                                        var statistic = PlayerStatisticManager.getInstance().getStatistic(off);
                                        if (statistic == null) {
                                            sender.sendMessage(Message.of(LangKeys.STATISTICS_NOT_FOUND).defaultPrefix());
                                        } else {
                                            sendStats(sender, statistic);
                                        }
                                    }
                                }
                            } else {
                                if (sender.getType() == CommandSender.Type.PLAYER) {
                                    var statistic = PlayerStatisticManager.getInstance().getStatistic(sender.as(OfflinePlayer.class));
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

    public static void sendStats(CommandSender sender, PlayerStatisticImpl statistic) {
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
