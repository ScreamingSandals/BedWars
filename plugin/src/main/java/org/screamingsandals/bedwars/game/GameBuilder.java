package org.screamingsandals.bedwars.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.team.GameTeam;
import org.screamingsandals.lib.gamecore.team.TeamColor;

import static org.screamingsandals.lib.lang.I.mpr;

public class GameBuilder extends org.screamingsandals.lib.gamecore.core.GameBuilder<Game> {

    @Override
    public boolean create(String arenaName, Player player) {
        if (super.create(arenaName, player)) {
            setGameFrame(new Game(arenaName));
            return true;
        }
        return false;
    }

    @Override
    public void save(Player player) {
        if (getGameFrame().checkIntegrity()) {
            Main.getGameManager().registerGame(getGameFrame().getGameName(), getGameFrame());
        } else {
            checkWhatsWrong(player);
        }
    }

    public void addTeam(String teamName, TeamColor teamColor, int players) {
        super.addTeam(new GameTeam(teamName, teamColor, players));
    }

    private void checkWhatsWrong(CommandSender sender) {
        final var game = getGameFrame();
        final var gameWorld = game.getGameWorld();
        final var lobbyWorld = game.getLobbyWorld();
        final var teamsSize = game.getTeams().size();
        final var gameStoresSize = game.getStores().size();

        if (gameWorld == null) {
            mpr("game-builder.check-integrity.errors.game-world-does-not-exists").send(sender);
            return;
        }

        if (gameWorld.getPosition1() == null) {
            mpr("game-builder.check-integrity.errors.game-world-position-not-set")
                    .replace("%whatPosition%", "1").send(sender);
            return;
        }

        if (gameWorld.getPosition2() == null) {
            mpr("game-builder.check-integrity.errors.game-world-position-not-set")
                    .replace("%whatPosition%", "2").send(sender);
            return;
        }

        if (gameWorld.getSpectatorSpawn() == null) {
            mpr("game-builder.check-integrity.errors.game-world-spectator-not-set").send(sender);
            return;
        }

        if (lobbyWorld == null) {
            mpr("game-builder.check-integrity.errors.lobby-world-not-set").send(sender);
            return;
        }

        if (lobbyWorld.getSpawn() == null) {
            mpr("game-builder.check-integrity.errors.lobby-world-not-set").send(sender);
            return;
        }


        if (teamsSize < 2) {
            mpr("game-builder.check-integrity.errors.not-enough-teams").send(sender);
            mpr("game-builder.check-integrity.errors.not-enough-teams-line2")
                    .replace("%count%", teamsSize).send(sender);
            return;
        }

        if (gameStoresSize < 1) {
            mpr("game-builder.check-integrity.errors.not-enough-stores").send(sender);
            return;
        }
    }
}
