package org.screamingsandals.bedwars.commands.game.admin.actions.set;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.commands.game.admin.actions.Action;
import org.screamingsandals.bedwars.game.GameBuilder;
import org.screamingsandals.lib.gamecore.adapter.LocationAdapter;

import java.util.LinkedList;
import java.util.List;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

public class SetSpawnAction implements Action {

    @Override
    public void handleCommand(GameBuilder gameBuilder, Player player, List<String> args) {
        final var currentGame = gameBuilder.getGameFrame();
        final var argsSize = args.size();

        if (argsSize < 1) {
            mpr("commands.admin.actions.set.spawn.invalid-entry")
                    .game(currentGame)
                    .sendList(player);
            return;
        }

        final var type = args.get(0);

        switch (type) {
            case "lobby": {
                if (gameBuilder.setLobbySpawn(player)) {
                    mpr("commands.admin.actions.set.spawn.done")
                            .replace("%type%", type)
                            .send(player);
                    return;
                }
                break;
            }
            case "spectators": {
                if (gameBuilder.setSpectatorsSpawn(player)) {
                    mpr("commands.admin.actions.set.spawn.done")
                            .replace("%type%", type)
                            .send(player);
                    return;
                }
                break;
            }
        }

        if (argsSize == 2 && type.equalsIgnoreCase("team")) {
            final var location = player.getLocation();

            if (!gameBuilder.isLocationInsideGame(location)) {
                mpr("general.errors.outside-of-the-border").send(player);
                return;
            }

            final var teamName = args.get(1);
            final var team = currentGame.getRegisteredTeam(teamName);

            if (team.isEmpty()) {
                mpr("general.errors.invalid-team")
                        .replace("%team%", team)
                        .send(player);
                return;
            }

            team.get().setSpawnLocation(new LocationAdapter(location));
            mpr("commands.admin.actions.set.spawn.done")
                    .replace("%type%", type)
                    .send(player);
        }
    }

    @Override
    public List<String> handleTab(GameBuilder gameBuilder, Player player, List<String> args) {
        final var toReturn = new LinkedList<String>();
        final var argsSize = args.size();

        if (argsSize == 0) {
            return toReturn;
        }

        if (argsSize == 1) {
            final var typed = args.get(0);
            final List<String> available = List.of("lobby", "spectators", "team");

            for (var found : available) {
                if (found.startsWith(typed)) {
                    toReturn.add(found);
                }
                return toReturn;
            }
        }

        if (argsSize == 2) {
            final var typed = args.get(1);

            for (var found : gameBuilder.getGameFrame().getAvailableTeams()) {
                if (found.startsWith(typed)) {
                    toReturn.add(found);
                }
                return toReturn;
            }
        }
        return toReturn;
    }
}
