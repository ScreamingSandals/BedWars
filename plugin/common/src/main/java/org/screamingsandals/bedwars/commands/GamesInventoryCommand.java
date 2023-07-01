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
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.inventories.GamesInventory;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class GamesInventoryCommand extends BaseCommand {
    private final GamesInventory gamesInventory;

    public GamesInventoryCommand(GamesInventory gamesInventory) {
        super("gamesinv", BedWarsPermission.GAMES_INVENTORY_PERMISSION, false);
        this.gamesInventory = gamesInventory;
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument
                                .<CommandSender>newBuilder("inventory")
                                .withSuggestionsProvider((objectCommandContext, s) -> gamesInventory.getInventoriesNames())
                        )
                        .handler(commandContext -> {
                            var player = commandContext.getSender().as(Player.class);
                            String inventory = commandContext.get("inventory");

                            if (!gamesInventory.openForPlayer(player, inventory)) {
                                player.sendMessage(Message.of(LangKeys.GAMES_INVENTORY_UNKNOWN_INVENTORY).defaultPrefix().placeholder("type", inventory));
                            }
                        })
        );
    }
}
