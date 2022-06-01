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
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.econ.EconomyProvider;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sender.CommandSenderWrapper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service(dependsOn = {
        EconomyProvider.class
})
public class JoinCommand extends BaseCommand {

    public JoinCommand() {
        super("join", BedWarsPermission.JOIN_PERMISSION, false);
    }

    @Override
    protected void construct(Command.Builder<CommandSenderWrapper> commandSenderWrapperBuilder, CommandManager<CommandSenderWrapper> manager) {
        manager.command(
                commandSenderWrapperBuilder
                        .argument(manager
                                .argumentBuilder(String.class, "game")
                                .withSuggestionsProvider((c, s) -> GameManagerImpl.getInstance().getGameNames())
                                .asOptional()
                        )
                        .handler(commandContext -> {
                            Optional<String> game = commandContext.getOptional("game");

                            var sender = commandContext.getSender().as(PlayerWrapper.class);
                            var player = sender.as(PlayerWrapper.class);
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
                                // Go through all games and find the ones with the most players (Every game in list will have same player count)
                                List<GameImpl> highestCountGames = new ArrayList<>();
                                for (GameImpl waitingGame : GameManagerImpl.getInstance().getGames()) {
                                    if (waitingGame.getStatus() != GameStatus.WAITING) { continue; }
                                    if (highestCountGames.isEmpty()) { highestCountGames.add(waitingGame); }

                                    int playerCount = waitingGame.countPlayers();
                                    int highestCount = highestCountGames.get(0).countPlayers();

                                    if (highestCount == playerCount) { highestCountGames.add(waitingGame); }
                                    if (playerCount > highestCount) {
                                        highestCountGames.clear();
                                        highestCountGames.add(waitingGame);
                                    }
                                }
                                // If there are no games, send error message
                                if (highestCountGames.isEmpty()) {
                                    sender.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_GAME_NOT_FOUND).defaultPrefix());
                                    return;
                                }
                                // If there is only one game, join it
                                if (highestCountGames.size() == 1) {
                                    highestCountGames.get(0).joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player));
                                    return;
                                }
                                // If there are multiple games, send them to a random one
                                int randomIndex = MiscUtils.randInt(0, highestCountGames.size()-1);
                                highestCountGames.get(randomIndex).joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player));
                            }
                        })
        );
    }
}
