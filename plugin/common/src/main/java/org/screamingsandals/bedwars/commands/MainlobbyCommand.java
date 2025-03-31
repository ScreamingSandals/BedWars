/*
 * Copyright (C) 2025 ScreamingSandals
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
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;

@Service
public class MainlobbyCommand extends BaseCommand {
    public MainlobbyCommand() {
        super("mainlobby", BedWarsPermission.ADMIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(manager
                                .argumentBuilder(String.class, "action")
                                .withSuggestionsProvider((c, s) -> List.of("enable", "set"))
                        )
                .handler(commandContext -> {
                    String action = commandContext.get("action");
                    var sender = commandContext.getSender();

                    if (action.contains("enable")) {
                        try {
                            MainConfig.getInstance().node("mainlobby", "enabled").set(true);
                            MainConfig.getInstance().saveConfig();

                            Message
                                    .of(LangKeys.SUCCESS)
                                    .join(LangKeys.ADMIN_MAINLOBBY_INFO)
                                    .defaultPrefix()
                                    .send(sender);
                        } catch (SerializationException e) {
                            e.printStackTrace();
                        }
                    } else if (action.contains("set")) {
                        var location = sender.as(Player.class).getLocation();

                        try {
                            MainConfig.getInstance().node("mainlobby", "location").set(MiscUtils.writeLocationToString(location));
                            MainConfig.getInstance().node("mainlobby", "world").set(location.getWorld().getName());
                            MainConfig.getInstance().saveConfig();

                            sender.sendMessage(Message.of(LangKeys.SUCCESS).defaultPrefix());
                        } catch (SerializationException e) {
                            e.printStackTrace();
                        }
                    }
                })
        );
    }
}
