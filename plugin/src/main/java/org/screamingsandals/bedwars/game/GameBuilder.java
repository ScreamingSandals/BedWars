package org.screamingsandals.bedwars.game;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.team.GameTeam;
import org.screamingsandals.lib.gamecore.GameCore;

import static org.screamingsandals.lib.gamecore.language.GameLanguage.mpr;

public class GameBuilder extends org.screamingsandals.lib.gamecore.core.GameBuilder<Game> {

    @Override
    public boolean create(String arenaName, Player player) {
        if (super.create(arenaName, player)) {
            gameFrame = new Game(arenaName);
            mpr("game-builder.created")
                    .replace("%game%", arenaName)
                    .send(player);

            storeListener = new StoreListener(gameFrame);
            GameCore.registerListener(storeListener);

            return true;
        }
        return false;
    }

    @Override
    public void save(Player player) {
        super.save(player);

        if (gameFrame.checkIntegrity(true)) {
            final var gameManager = Main.getGameManager();
            gameManager.saveGame(gameFrame);
            gameManager.registerGame(gameFrame.getUuid(), gameFrame);

            final var uuid = gameFrame.getUuid();
            GameCore.getEntityManager().unregisterAll(uuid);
            GameCore.getHologramManager().destroyAll(uuid);

            GameCore.unregisterListener(storeListener);
            storeListener = null;

            player.sendMessage("Saved");
        } else {
            checkWhatsWrong(player);
        }
    }

    public void addTeam(GameTeam gameTeam) {
        gameFrame.getTeams().add(gameTeam);
    }

    private void checkWhatsWrong(CommandSender sender) {
        final var gameWorld = gameFrame.getGameWorld();
        final var lobbyWorld = gameFrame.getLobbyWorld();
        final var teamsSize = gameFrame.getTeams().size();
        final var gameStoresSize = gameFrame.getStores().size();

        if (gameWorld == null) {
            mpr("game-builder.check-integrity.errors.game-world-does-not-exists").send(sender);
            return;
        }

        if (gameWorld.getBorder1() == null) {
            mpr("game-builder.check-integrity.errors.game-world-position-not-set")
                    .replace("%whatPosition%", "1").send(sender);
            return;
        }

        if (gameWorld.getBorder2() == null) {
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
