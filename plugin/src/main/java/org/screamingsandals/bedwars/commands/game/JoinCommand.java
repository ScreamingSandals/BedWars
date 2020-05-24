package org.screamingsandals.bedwars.commands.game;

import org.screamingsandals.bedwars.api.Permissions;
import org.screamingsandals.bedwars.commands.BedWarsCommand;
import org.screamingsandals.lib.commands.common.RegisterCommand;
import org.screamingsandals.lib.commands.common.SubCommandBuilder;
import org.screamingsandals.lib.commands.common.interfaces.ScreamingCommand;
import org.screamingsandals.lib.gamecore.GameCore;

import java.util.Collections;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

@RegisterCommand
public class JoinCommand implements ScreamingCommand {

    @Override
    public void register() {
        SubCommandBuilder.bukkitSubCommand()
                .createSubCommand(BedWarsCommand.COMMAND_NAME, "join", Permissions.JOIN_COMMAND, Collections.emptyList())
                .handleSubPlayerCommand((player, args) -> {
                    final var gamePlayer = GameCore.getPlayerManager().getRegisteredPlayer(player);
                    if (gamePlayer.isInGame()) {
                        mpr("commands.join.failed")
                                .game(gamePlayer.getActiveGame())
                                .send(player);
                        return;
                    }
                    
                    if (args.size() == 2) {
                        final var gameName = args.get(1);
                        final var game = GameCore.getGameManager().getRegisteredGame(gameName);
                        if (game.isEmpty()) {
                            mpr("general.errors.invalid-game")
                                    .replace("%gameName%", gameName)
                                    .send(player);
                            return;
                        }

                        game.get().join(gamePlayer);
                        return;
                    }

                    final var game = GameCore.getGameManager().getFirstAvailableGame();
                    if (game == null) {
                        mpr("general.errors.no-game-found")
                                .send(player);
                        return;
                    }
                    game.join(gamePlayer);
                });
    }
}
