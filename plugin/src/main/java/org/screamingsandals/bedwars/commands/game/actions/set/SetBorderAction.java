package org.screamingsandals.bedwars.commands.game.actions.set;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.commands.game.actions.Action;
import org.screamingsandals.bedwars.game.GameBuilder;
import org.screamingsandals.lib.debug.Debug;
import org.screamingsandals.lib.gamecore.utils.GameUtils;

import java.util.LinkedList;
import java.util.List;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

public class SetBorderAction implements Action {

    @Override
    public void handleCommand(GameBuilder gameBuilder, Player player, List<String> args) {
        final var currentGame = gameBuilder.getGameFrame();
        final var argsSize = args.size();

        if (argsSize < 2) {
            mpr("commands.admin.actions.set.border.invalid-entry")
                    .game(currentGame)
                    .sendList(player);
            return;
        }

        final var type = args.get(0);
        final var numberFromArgs = args.get(1);

        if (!GameUtils.canCastToInt(numberFromArgs, player)) {
            Debug.warn("nope"); //TODO
            return;
        }

        final var borderPointNumber = GameUtils.castToInt(numberFromArgs);
        final var location = player.getLocation();

        switch (type) {
            case "game": {
                switch (borderPointNumber) {
                    case 1:
                    case 2:
                        gameBuilder.setGameWorldBorder(location, borderPointNumber);
                        mpr("commands.admin.actions.set.border.done")
                                .replace("%number%", borderPointNumber)
                                .replace("%type%", type)
                                .send(player);
                        break;
                    default: {
                        mpr("commands.admin.actions.set.border.invalid-entry")
                                .game(currentGame)
                                .sendList(player);
                    }
                }
                break;
            }

            case "lobby": {
                switch (borderPointNumber) {
                    case 1:
                    case 2:
                        gameBuilder.setLobbyWorldBorder(location, borderPointNumber);
                        mpr("commands.admin.actions.set.border.done")
                                .replace("%number%", borderPointNumber)
                                .replace("%type%", type)
                                .send(player);
                        break;
                    default: {
                        mpr("commands.admin.actions.set.border.invalid-entry")
                                .game(currentGame)
                                .sendList(player);
                    }
                }
                break;
            }
        }
    }

    @Override
    public List<String> handleTab(GameBuilder gameBuilder, Player player, List<String> args) {
        final var argsSize = args.size();
        final List<String> toReturn = new LinkedList<>();
        if (argsSize == 0) {
            return toReturn;
        }

        final var typedType = args.get(0);
        if (argsSize == 1) {
            for (var found : List.of("game", "lobby")) {
                if (found.startsWith(typedType)) {
                    toReturn.add(found);
                }
            }

            return toReturn;
        }

        final var typedNumber = args.get(1);
        if (argsSize == 2) {
            for (var found : List.of("1", "2")) {
                if (found.startsWith(typedNumber)) {
                    toReturn.add(found);
                }
            }

            return toReturn;
        }

        return toReturn;
    }
}
