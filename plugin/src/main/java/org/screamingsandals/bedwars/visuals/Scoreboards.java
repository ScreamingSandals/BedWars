package org.screamingsandals.bedwars.visuals;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bukkit.scoreboard.DisplaySlot;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.config.VisualsConfig;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.team.GameTeam;
import org.screamingsandals.lib.gamecore.core.GameState;
import org.screamingsandals.lib.scoreboards.scoreboard.Scoreboard;
import org.screamingsandals.lib.scoreboards.scoreboard.ScoreboardCreator;

import java.util.List;
import java.util.UUID;

import static org.screamingsandals.lib.lang.I.m;

@Data
public class Scoreboards {
    private final Game game;

    @EqualsAndHashCode(callSuper = false)
    @Data
    private static class GameScoreboard extends org.screamingsandals.lib.scoreboards.scoreboard.Scoreboard {
        private Game game = null;
        private GameScoreboard gameScoreboard;

        public static GameScoreboard of(Scoreboard scoreboard) {
            final var toReturn = new GameScoreboard();
            toReturn.scoreboardHolder = scoreboard.getScoreboardHolder();
            toReturn.gameScoreboard = toReturn;

            return toReturn;
        }

        public void addTeams(List<GameTeam> gameTeams) {
            gameTeams.forEach(gameTeam -> gameScoreboard.addTeam(gameTeam.getTeamName(), gameTeam.getTeamColor().chatColor));
        }

        public void parsePlaceholders() {
            if (game == null) {
                return;
            }

            final var lines = scoreboardHolder.getLines();
            final var parser = game.getPlaceholderParser();

            for (var entry : lines.entrySet()) {
                lines.replace(entry.getKey(), entry.getValue(), parser.replace(entry.getValue()));
            }
        }
    }

    @Data
    public static class ContentBuilder {
        private final UUID uuid;
        private final GameState gameState;

        public static GameScoreboard get(UUID uuid, GameState gameState) {
            final var toReturn = new ContentBuilder(uuid, gameState);
            final var state = gameState.getName();
            final var scoreboardName = uuid.toString() + gameState.name();
            final String displayName;
            final List<String> lines;

            if (toReturn.isCustomContentEnabled()) {
                displayName = Main.getVisualsConfig().getString(VisualsConfig.PATH_SCOREBOARDS_NAME);
                lines = Main.getVisualsConfig().getStringList(VisualsConfig.PATH_SCOREBOARDS_CONTENT + state);
            } else {
                displayName = m("scoreboards.name").get();
                lines = m("scoreboards.content." + state).getList();
            }

            return GameScoreboard
                    .of(ScoreboardCreator
                            .create(scoreboardName, displayName, DisplaySlot.SIDEBAR, ScoreboardCreator.sortLines(lines)).get());
        }

        public boolean isCustomContentEnabled() {
            return Main.getVisualsConfig().getBoolean(VisualsConfig.PATH_SCOREBOARDS_CUSTOM_ENABLED);
        }
    }
}
