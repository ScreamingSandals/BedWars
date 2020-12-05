package org.screamingsandals.bedwars.scoreboard;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.bedwars.listener.Player116ListenerUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ScreamingBoard {

    private final Game game;

    public static final String GAME_OBJECTIVE = "bedwars_game";
    public static final String LOBBY_OBJECTIVE = "bedwars_lobby";

    public ScreamingBoard(Game game) {
            this.game = game;

            new BukkitRunnable(){
                @Override
                public void run() {
                    switch (game.getStatus()) {
                        case WAITING:
                            updateLobbyBoard();
                            break;
                        case RUNNING:
                            updateGameBoard();
                            break;
                        default:
                            destroy();
                            break;

                    }
                }
            }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    private void updateLobbyBoard() {
        if (!game.getOriginalOrInheritedLobbyScoreaboard()) {
            return;
        }

        for (Player player : game.getConnectedPlayers()) {
            registerBoard(LOBBY_OBJECTIVE, player);
            Scoreboard board = player.getScoreboard();
            Objective obj = board.getObjective(LOBBY_OBJECTIVE);
            if (obj != null) {
                List<String> rows = Main.getConfigurator().config.getStringList("lobby-scoreboard.content");
                if (rows.isEmpty()) {
                    return;
                }

                rows = resizeAndMakeUnique(rows, player);

                int i = 15;
                for (String row : rows) {
                    try {
                        final String element = game.formatLobbyScoreboardString(row);
                        final Score score = obj.getScore(element);

                        if (score.getScore() != i) {
                            score.setScore(i);
                            for (String entry : board.getEntries()) {
                                if (obj.getScore(entry).getScore() == i && !entry.equalsIgnoreCase(element)) {
                                    board.resetScores(entry);
                                }
                            }
                        }
                    } catch (IllegalArgumentException | IllegalStateException e){
                        e.printStackTrace();
                    }
                    i--;
                }
            }

            player.setScoreboard(board);
        }
        unregisterUnusedTeams();

    }

    public void updateGameBoard() {
        if (!game.getOriginalOrInheritedScoreaboard()) {
            return;
        }

        final List<String> teamStatus = new ArrayList<>();

        //lets process team status first
        for (RunningTeam team : game.getRunningTeams()) {
            String formattedScore = formatScoreboardTeam(team, !team.isTargetBlockExists(), team.isTargetBlockExists() && "RESPAWN_ANCHOR".equals(team.getTargetBlock().getBlock().getType().name()) && Player116ListenerUtils.isAnchorEmpty(team.getTargetBlock().getBlock()));
            teamStatus.add(formattedScore);
        }


        for (Player player : game.getConnectedPlayers()) {
            registerBoard(GAME_OBJECTIVE, player);
            org.bukkit.scoreboard.Scoreboard board = player.getScoreboard();
            final Objective obj = board.getObjective(GAME_OBJECTIVE);
            obj.setDisplayName(game.formatScoreboardTitle());
            game.getRunningTeams().forEach(team->registerTeam(team, GAME_OBJECTIVE));

            List<String> content = Main.getConfigurator().config.getStringList("experimental.new-scoreboard-system.content");

            if (content.isEmpty()) {
                return;
            }

            content = resizeAndMakeUnique(content, player);

            final List<String> finalContent = new ArrayList<>();

            content.forEach(line->{
                if (line.contains("%team_status%")) {
                    finalContent.addAll(teamStatus);
                    return;
                }

                //Process more placeholders here if required
                finalContent.add(line);
            });


            int i = 15;
            for (String element : finalContent) {
                try {
                    final Score score = obj.getScore(element);

                    if (score.getScore() != i) {
                        score.setScore(i);
                        for (String entry : board.getEntries()) {
                            if (obj.getScore(entry).getScore() == i && !entry.equalsIgnoreCase(element)) {
                                board.resetScores(entry);
                            }
                        }
                    }
                } catch (IllegalArgumentException | IllegalStateException e){
                    e.printStackTrace();
                }
                i--;
            }

            player.setScoreboard(board);


        }
    }

    private String formatScoreboardTeam(RunningTeam team, boolean destroy, boolean empty) {
        if (team == null) {
            return "";
        }

        return Main.getConfigurator().config.getString("scoreboard.teamTitle")
                .replace("%color%", TeamColor.fromApiColor(team.getColor())
                        .chatColor.toString()).replace("%team%", team.getName())
                .replace("%bed%", destroy ? game.bedLostString() : (empty ? game.anchorEmptyString() : game.bedExistString()));
    }

    public void registerTeam(RunningTeam team, String obj_name) {
        for (Player player : game.getConnectedPlayers()) {
            registerBoard(obj_name, player);
            Scoreboard board = player.getScoreboard();
            Objective obj = board.getObjective(obj_name);
            if (obj != null) {
                Team scoreboardTeam = board.getTeam(team.getName());

                //register team
                if (scoreboardTeam == null) {
                    scoreboardTeam = board.registerNewTeam(team.getName());
                    scoreboardTeam.setAllowFriendlyFire(false);
                    scoreboardTeam.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
                    if (!Main.isLegacy()) {
                        scoreboardTeam.setColor(TeamColor.fromApiColor(team.getColor()).chatColor);
                    } else {
                        scoreboardTeam.setPrefix(TeamColor.fromApiColor(team.getColor()).chatColor.toString());
                    }
                }

                //Check if there are players that need to be removed from entry
                for (String scoreboardEntry : new HashSet<>(scoreboardTeam.getEntries())) {
                    final Player scoreboardPlayer = Bukkit.getPlayerExact(scoreboardEntry);
                    if (scoreboardPlayer == null || !team.getConnectedPlayers().contains(scoreboardPlayer)) {
                        scoreboardTeam.removeEntry(scoreboardEntry);
                    }
                }


                //add entries
                for (Player teamPlayer : team.getConnectedPlayers()) {
                    if (!scoreboardTeam.hasEntry(teamPlayer.getName())) {
                        scoreboardTeam.addEntry(teamPlayer.getName());
                    }
                }

                player.setScoreboard(board);
            }
        }
    }

    public void registerBoard(String obj_name, Player player) {
        Scoreboard board = player.getScoreboard();
        if (board == null || board == Bukkit.getScoreboardManager().getMainScoreboard() ||
           board.getObjective(obj_name) == null) {

            board = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective obj = board.getObjective(obj_name);
            if (obj == null) {
                obj = board.registerNewObjective(obj_name, "dummy");
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                obj.setDisplayName(obj_name.equals(LOBBY_OBJECTIVE) ?
                        game.formatLobbyScoreboardString(
                        Main.getConfigurator().config.getString("lobby-scoreboard.title", "§eBEDWARS")) :
                        game.formatScoreboardTitle());
            }

            player.setScoreboard(board);
        }
    }

    public void unregisterUnusedTeams() {
        //Dont unregister teams while game is running lol
        if (game.getStatus() != GameStatus.WAITING) {
            return;
        }

        List<String> teamNames = new ArrayList<>();
        game.getRunningTeams().forEach(team->teamNames.add(team.getName()));

        for (Player player : game.getConnectedPlayers()) {
            Scoreboard board = player.getScoreboard();
            if (board != null && board.getObjective(LOBBY_OBJECTIVE) != null) {
                for (Team sboardTeam : new HashSet<>(board.getTeams())) {
                    if (!teamNames.contains(sboardTeam.getName())) {
                        try {
                            board.getTeam(sboardTeam.getName()).unregister();
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
    }

    public void unregisterTeam(Team team, String OBJECTIVE) {
        for (Player player : game.getConnectedPlayers()) {
            registerBoard(OBJECTIVE, player);
            Scoreboard board = player.getScoreboard();
            Objective obj = board.getObjective(OBJECTIVE);

            if (obj != null) {
                Team scoreboardTeam = board.getTeam(team.getName());
                if (scoreboardTeam != null) {
                    try {
                        scoreboardTeam.unregister();
                    } catch (Exception ignored) {}
                }
            }

            player.setScoreboard(board);
        }
    }

    public void destroy() {
        for (Player player : game.getConnectedPlayers()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }


    public static List<String> resizeAndMakeUnique(List<String> lines,
                                                   Player player) {
        final List<String> content = new ArrayList<>();

        lines.forEach(line -> {
            String copy = line;
            if (copy == null) {
                copy = " ";
            }


            copy = Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI") ?
                    PlaceholderAPI.setPlaceholders(player, copy) : copy;

            //avoid exceptions returned by getScore()
            if (copy.length() > 40) {
                copy = copy.substring(40);
            }



            final StringBuilder builder = new StringBuilder(copy);
            while (content.contains(builder.toString())) {
                builder.append(" ");
            }
            content.add(builder.toString());
        });

        if(content.size() > 15) {
            return content.subList(0, 15);
        }
        return content;
    }




}
