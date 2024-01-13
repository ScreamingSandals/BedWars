/*
 * Copyright (C) 2024 ScreamingSandals
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
import cloud.commandframework.context.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.holograms.LeaderboardHolograms;
import org.screamingsandals.bedwars.holograms.StatisticsHolograms;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class AddholoCommand extends BaseCommand {
    public AddholoCommand() {
        super("addholo", BedWarsPermission.ADMIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
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
                            var eyeLocation = sender.as(Player.class).getEyeLocation();
                            if (!LeaderboardHolograms.isEnabled()) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_HOLO_NOT_ENABLED).defaultPrefix());
                            } else {
                                LeaderboardHolograms.getInstance().addHologramLocation(eyeLocation);
                                sender.sendMessage(Message.of(LangKeys.LEADERBOARD_HOLO_ADDED).defaultPrefix());
                            }
                        })
        );
    }

    private void executeStatsHologram(@NotNull CommandContext<CommandSender> commandContext) {
        var sender = commandContext.getSender();
        var eyeLocation = sender.as(Player.class).getEyeLocation();
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
