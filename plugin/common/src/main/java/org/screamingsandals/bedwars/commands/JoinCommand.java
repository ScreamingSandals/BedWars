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
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Optional;

@Service
public class JoinCommand extends BaseCommand {

    public JoinCommand() {
        super("join", BedWarsPermission.JOIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSender> commandSenderWrapperBuilder, CommandManager<CommandSender> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(manager
                                .argumentBuilder(String.class, "game")
                                .withSuggestionsProvider((c, s) -> GameManagerImpl.getInstance().getGameNames())
                                .asOptional()
                        )
                        .handler(commandContext -> {
                            Optional<String> game = commandContext.getOptional("game");

                            var sender = commandContext.getSender().as(Player.class);
                            var player = sender.as(Player.class);
                            if (PlayerManagerImpl.getInstance().isPlayerInGame(sender)) {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_ALREADY_IN_GAME).defaultPrefix());
                                return;
                            }

                            if (game.isPresent()) {
                                var arenaN = game.get();
                                GameManagerImpl.getInstance().getGame(arenaN).ifPresentOrElse(
                                        game1 -> game1.joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player)),
                                        () -> sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix())
                                );
                            } else {
                                GameManagerImpl.getInstance().getGameWithHighestPlayers(false).ifPresentOrElse(
                                        game1 -> game1.joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player)),
                                        () -> sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix())
                                );
                            }
                        })
        );
    }
}
