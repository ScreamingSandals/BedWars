package org.screamingsandals.bedwars.commands.game.actions.add;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.commands.game.actions.Action;
import org.screamingsandals.bedwars.game.GameBuilder;
import org.screamingsandals.bedwars.game.team.GameTeam;
import org.screamingsandals.lib.gamecore.team.TeamColor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

public class AddTeamAction implements Action {

    @Override
    public void handleCommand(GameBuilder gameBuilder, Player player, List<String> args) {
        final var currentGame = gameBuilder.getGameFrame();
        final var argsSize = args.size();
        if (argsSize < 3) {
            mpr("commands.admin.actions.add.team.invalid-entry")
                    .game(currentGame)
                    .sendList(player);
            return;
        }

        final var teamName = args.get(0);
        final var teamColor = TeamColor.get(args.get(1));
        int maxPlayer;

        if (currentGame.isTeamRegistered(teamName)) {
            mpr("commands.admin.actions.add.team.already-registered")
                    .game(currentGame)
                    .replace("%name%", teamName)
                    .send(player);
            return;
        }

        if (teamColor.isEmpty()) {
            mpr("commands.admin.actions.add.team.invalid-color")
                    .game(currentGame)
                    .replace("%color%", args.get(1))
                    .sendList(player);
            return;
        }

        try {
            maxPlayer = Integer.parseInt(args.get(2));
        } catch (NumberFormatException ignored) {
            mpr("general.errors.invalid-number")
                    .game(currentGame)
                    .send(player);
            return;
        }

        final var color = teamColor.get();
        gameBuilder.addTeam(new GameTeam(teamName, color, maxPlayer));
        mpr("commands.admin.actions.add.team.created")
                .game(currentGame)
                .replace("%color%", color.chatColor)
                .replace("%name%", teamName)
                .send(player);

    }

    @Override
    public List<String> handleTab(GameBuilder gameBuilder, Player player, List<String> args) {
        final var argsSize = args.size();
        final List<String> toReturn = new LinkedList<>();

        if (argsSize == 1) {
            return Collections.singletonList("WRITE YOUR TEAM NAME"); //todo LANGUAGE
        }

        if (argsSize == 2) {
            final var typed = args.get(1);

            for (TeamColor found : TeamColor.values()) {
                final var name = found.colorName;

                if (name.startsWith(typed)) {
                    toReturn.add(name);
                }
            }
        }

        if (argsSize == 3) {
            toReturn.addAll(List.of("1", "2", "3", "4", "5", "6"));
        }

        return toReturn;
    }
}
