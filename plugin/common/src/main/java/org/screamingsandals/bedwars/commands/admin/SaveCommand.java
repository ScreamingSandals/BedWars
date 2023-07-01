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
import cloud.commandframework.context.CommandContext;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.event.ClickEvent;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.ArrayList;

@Service
public class SaveCommand extends BaseAdminSubCommand {
    public SaveCommand() {
        super("save");
    }

    @Override
    public void construct(CommandManager<CommandSender> manager, Command.Builder<CommandSender> commandSenderWrapperBuilder) {
        manager.command(
                commandSenderWrapperBuilder
                        .handler(this::save)
        );

        manager.command(
                commandSenderWrapperBuilder
                        .literal("force")
                        .handler(this::save)
        );
    }

    private void save(@NotNull CommandContext<CommandSender> commandContext) {
        editMode(commandContext, (sender, game) -> {
            // SEVERE (currently)
            for (var team : game.getTeams()) {
                if (team.getTarget() == null) {
                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_TARGET_BLOCK_FOR_TEAM_BEFORE_SAVE).defaultPrefix().placeholder("team", team.getName()));
                    return;
                } else if (team.getTeamSpawns().isEmpty()) {
                    sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_SPAWN_FOR_TEAM_BEFORE_SAVE).defaultPrefix().placeholder("team", team.getName()));
                    return;
                }
            }
            if (game.getTeams().size() < 2) {
                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_NEED_2_TEAMS).defaultPrefix());
                return;
            } else if (game.getPos1() == null || game.getPos2() == null) {
                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_BOUNDS_BEFORE_SAVE).defaultPrefix());
                return;
            } else if (game.getLobbySpawn() == null) {
                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_LOBBY_BEFORE_SAVE).defaultPrefix());
                return;
            } else if (game.getSpecSpawn() == null) {
                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SET_SPEC_BEFORE_SAVE).defaultPrefix());
                return;
            }

            // WARNINGS
            var warnings = new ArrayList<Message>();
            if (!commandContext.getRawInputJoined().trim().endsWith("force")) { // is there a better way?
                if (game.getGameStoreList().isEmpty()) {
                    warnings.add(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MISSING_STORES).defaultPrefix());
                } else if ((game.getGameStoreList().size() % game.getTeams().size()) != 0) {
                    warnings.add(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_WEIRD_STORE_COUNT).placeholder("count", game.getGameStoreList().size()).defaultPrefix());
                }
                if (game.getSpawners().isEmpty()) {
                    warnings.add(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_MISSING_SPAWNERS).defaultPrefix());
                }
            }

            if (warnings.isEmpty()) {
                game.saveToConfig();
                GameManagerImpl.getInstance().addGame(game);
                game.start();
                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_SUCCESS_SAVED_AND_STARTED).placeholderRaw("game", game.getName()).defaultPrefix());
                AdminCommand.gc.remove(commandContext.<String>get("game"));
            } else {
                warnings.forEach(sender::sendMessage);
                sender.sendMessage(Message.of(LangKeys.ADMIN_ARENA_EDIT_ERRORS_SKIP_WARNINGS)
                        .placeholderRaw("game", game.getName())
                        .placeholder("command", Component.text()
                                .content("/" + commandContext.getRawInputJoined() + " force")
                                .hoverEvent(Message.of(LangKeys.ADMIN_INFO_SELECT_CLICK)
                                        .placeholderRaw("command", "/" + commandContext.getRawInputJoined() + " force")
                                        .asComponent(sender))
                                .clickEvent(ClickEvent.runCommand("/" + commandContext.getRawInputJoined() + " force")).build())
                        .defaultPrefix());
            }
        });
    }
}
