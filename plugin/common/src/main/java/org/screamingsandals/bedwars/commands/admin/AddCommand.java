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

package org.screamingsandals.bedwars.commands.admin;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.arguments.standard.StringArgument;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.variants.VariantImpl;
import org.screamingsandals.bedwars.variants.VariantManagerImpl;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class AddCommand extends BaseAdminSubCommand {
    public AddCommand() {
        super("add");
    }

    @Override
    public void construct(CommandManager<CommandSenderWrapper> manager, Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(StringArgument
                                .<CommandSenderWrapper>newBuilder("variant")
                                .withSuggestionsProvider((c, s) -> VariantManagerImpl.getInstance().getVariantNames())
                                .asOptional()
                        )
                        .handler(commandContext -> {
                            String gameName = commandContext.get("game");
                            var variant = commandContext.<String>getOptional("variant");
                            var sender = commandContext.getSender();

                            if (GameManagerImpl.getInstance().hasGame(gameName)) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_ERROR_ALREADY_EXISTS).defaultPrefix());
                            } else if (AdminCommand.gc.containsKey(gameName)) {
                                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_ERROR_ALREADY_WORKING_ON_IT).defaultPrefix());
                            } else {
                                VariantImpl variantObj = null;
                                if (variant.isPresent()) {
                                    var variantOpt = VariantManagerImpl.getInstance().getVariant(variant.get());
                                    if (variantOpt.isPresent()) {
                                        variantObj = variantOpt.get();
                                    } else {
                                        sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_INVALID_VARIANT)
                                                .placeholder("variant", variant.get()).defaultPrefix());
                                    }
                                }

                                var creator = GameImpl.createGame(gameName);
                                AdminCommand.gc.put(gameName, creator);

                                if (variantObj != null) {
                                    creator.setGameVariant(variantObj);
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_SUCCESS_ADDED_WITH_VARIANT)
                                            .placeholder("arena", gameName)
                                            .placeholder("variant", variantObj.getName())
                                            .defaultPrefix());
                                } else {
                                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_SUCCESS_ADDED)
                                            .placeholder("arena", gameName)
                                            .defaultPrefix());
                                }
                            }
                        })
        );
    }
}
