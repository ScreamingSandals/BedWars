package org.screamingsandals.bedwars.visuals;

import lombok.Data;
import org.bukkit.scoreboard.DisplaySlot;
import org.screamingsandals.bedwars.config.VisualsConfig;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.lib.gamecore.core.GameState;
import org.screamingsandals.lib.gamecore.player.GamePlayer;
import org.screamingsandals.lib.gamecore.visuals.ScoreboardManager;
import org.screamingsandals.lib.scoreboards.scoreboard.Scoreboard;
import org.screamingsandals.lib.scoreboards.scoreboard.ScoreboardCreator;

import java.util.List;
import static org.screamingsandals.lib.lang.I.m;

public class Scoreboards {
    public static void createForPlayer(GamePlayer gamePlayer) {
        //TODO - redo this
        final ScoreboardManager scoreboardManager = gamePlayer.getActiveGame().getScoreboardManager();
        final String scoreboardDisplayedName = m(VisualsConfig.PATH_SCOREBOARDS_NAME).get();
        Scoreboard lobbyScoreboard = ScoreboardCreator.get(GameState.WAITING.getName())
                .create(scoreboardDisplayedName, DisplaySlot.SIDEBAR, Scoreboard.sortLines(m(VisualsConfig.PATH_SCOREBOARDS_CONTENT_LOBBY).getList()));

        Scoreboard gameScoreboard = ScoreboardCreator.get(GameState.IN_GAME.getName())
                .create(scoreboardDisplayedName, DisplaySlot.SIDEBAR, Scoreboard.sortLines(m(VisualsConfig.PATH_SCOREBOARDS_CONTENT_GAME).getList()));

        Scoreboard deathmatchScoreboard = ScoreboardCreator.get(GameState.DEATHMATCH.getName())
                .create(scoreboardDisplayedName, DisplaySlot.SIDEBAR, Scoreboard.sortLines(m(VisualsConfig.PATH_SCOREBOARDS_CONTENT_DEATHMATCH).getList()));

        Scoreboard endGameScoreboard = ScoreboardCreator.get(GameState.AFTER_GAME_COUNTDOWN.getName())
                .create(scoreboardDisplayedName, DisplaySlot.SIDEBAR, Scoreboard.sortLines(m(VisualsConfig.PATH_SCOREBOARDS_CONTENT_END_GAME).getList()));

        scoreboardManager.saveScoreboard(gamePlayer, lobbyScoreboard);
        scoreboardManager.saveScoreboard(gamePlayer, gameScoreboard);
        scoreboardManager.saveScoreboard(gamePlayer, deathmatchScoreboard);
        scoreboardManager.saveScoreboard(gamePlayer, endGameScoreboard);
    }

    @Data
    public static class ContentBuilder {
        private final Scoreboard scoreboard;
        private final List<String> content;
        private final Game game;
    }
}
