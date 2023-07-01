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

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LeaderboardCommand extends BaseCommand {
    public LeaderboardCommand() {
        super("leaderboard", BedWarsPermission.LEADERBOARD_PERMISSION, true);
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
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
                                        .placeholder("player", Objects.requireNonNullElse(
                                                leaderboardEntry.getPlayer().getLastName(),
                                                leaderboardEntry.getLastKnownName() != null ? leaderboardEntry.getLastKnownName() : leaderboardEntry
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
