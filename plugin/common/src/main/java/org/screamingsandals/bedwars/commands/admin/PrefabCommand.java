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

package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class PrefabCommand extends BaseAdminSubCommand {
    public PrefabCommand() {
        super("prefab");
    }

    @Override
    public void construct(CommandManager<CommandSender> manager, Command.Builder<CommandSender> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument
                                .<CommandSender>newBuilder("prefab")
                                .withSuggestionsProvider(editModeSuggestion((commandContext, sender, game) -> game.getGameVariant().getPrefabNames()))
                        )
                        .handler(commandContext -> editMode(commandContext, (sender, game) -> {
                            String prefabName = commandContext.get("prefab");

                            var prefab = game.getGameVariant().getPrefab(prefabName);

                            if (prefab == null) {
                                sender.sendMessage(
                                        Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_PREFAB)
                                                .defaultPrefix()
                                                .placeholder("name", prefabName)
                                                .placeholder("variant", game.getGameVariant().getName())
                                );
                                return;
                            }

                            prefab.place(game, sender.as(Player.class));
                        }))
        );
    }
}
