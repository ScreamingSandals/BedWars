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
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sender.CommandSender;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Optional;

@Service
public class AlljoinCommand extends BaseCommand {
    public AlljoinCommand() {
        super("alljoin", BedWarsPermission.ALL_JOIN_PERMISSION, true);
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
                            Optional<String> gameName = commandContext.getOptional("game");

                            var sender = commandContext.getSender();
                            var game = gameName
                                    .flatMap(GameManagerImpl.getInstance()::getGame)
                                    .or(GameManagerImpl.getInstance()::getGameWithHighestPlayers);

                            if (game.isEmpty()) {
                                sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                                return;
                            }

                            Server.getConnectedPlayers().forEach(player -> {
                                if (player.hasPermission(BedWarsPermission.DISABLE_ALL_JOIN_PERMISSION.asPermission())) {
                                    return;
                                }

                                if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                                    var p = player.as(BedWarsPlayer.class);
                                    p.getGame().leaveFromGame(p);
                                }
                                game.get().joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player));
                            });
                        })
        );
    }
}
