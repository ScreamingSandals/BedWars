package org.screamingsandals.bedwars.scoreboard;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.TeamColor;
import org.screamingsandals.bedwars.lib.scoreboard.scoreboard.ScreamingScoreboard;
import org.screamingsandals.bedwars.listener.Player116ListenerUtils;

import java.util.*;

public class ScreamingBoard {

    private final Game game;
    private BukkitTask bukkitTask;

    public static final String GAME_OBJECTIVE = "bedwars_game";
    public static final String LOBBY_OBJECTIVE = "bedwars_lobby";

    private final Map<Player, ScreamingScoreboard> playerBoards = new HashMap<>();

    public ScreamingBoard(Game game) {
        this.game = game;

        game.getConnectedPlayers().forEach(player-> { playerBoards.put(player,
                new ScreamingScoreboard(player, LOBBY_OBJECTIVE,
                        Main.getConfigurator().config.getString("lobby-scoreboard.title", "§eBEDWARS"))); });

            bukkitTask = new BukkitRunnable(){
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
                            this.cancel();
                            break;

                    }
                }
            }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }

    public void forceStop () {
        try {
            if (bukkitTask != null && !bukkitTask.isCancelled()) {
                bukkitTask.cancel();
            }
        } catch (Throwable t){
            t.printStackTrace();
        }
    }


    private void updateLobbyBoard() {
        if (!game.getConfigurationContainer().getOrDefault(ConfigurationContainer.LOBBY_SCOREBOARD, Boolean.class, false)) {
            return;
        }

        playerBoards.forEach((player, sboard) -> {
            List<String> rows = Main.getConfigurator().config.getStringList("lobby-scoreboard.content");
            if (rows.isEmpty()) {
                return;
            }

            rows = resizeAndMakeUnique(rows, player);
            int i = 15;
            sboard.getBukkitScoreboard().getEntries().forEach(entry-> sboard.getBukkitScoreboard().resetScores(entry));
            for (String row : rows) {
                sboard.getLinePainter().paintLine(i, game.formatLobbyScoreboardString(row));
                i--;
            }

            sboard.addTeams(game.getRunningTeams());
            player.setScoreboard(sboard.getBukkitScoreboard());
        });
    }


    public void updateGameBoard() {
        if (!game.getConfigurationContainer().getOrDefault(ConfigurationContainer.GAME_SCOREBOARD, Boolean.class, false)) {
            return;
        }

        final List<String> teamStatus = new ArrayList<>();
        //lets process team status first
        for (RunningTeam team : game.getRunningTeams()) {
            String formattedScore = formatScoreboardTeam(team, !team.isTargetBlockExists(), team.isTargetBlockExists() && "RESPAWN_ANCHOR".equals(team.getTargetBlock().getBlock().getType().name()) && Player116ListenerUtils.isAnchorEmpty(team.getTargetBlock().getBlock()));
            teamStatus.add(formattedScore);
        }


        playerBoards.forEach((player, board) -> {
            board.registerNewObjective(GAME_OBJECTIVE, "BEDWARS");
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

            board.getBukkitScoreboard().getEntries().forEach(entry-> board.getBukkitScoreboard().resetScores(entry));

            int i = 15;
            for (String element : finalContent) {
                board.getLinePainter().paintLine(i, element);
                i--;
            }

            board.getLinePainter().setDisplayName(game.formatScoreboardTitle());
            player.setScoreboard(board.getBukkitScoreboard());

        });

    }

    private String formatScoreboardTeam(RunningTeam team, boolean destroy, boolean empty) {
        if (team == null) {
            return "";
        }

        return Main.getConfigurator().config.getString("experimental.new-scoreboard-system.teamTitle")
                .replace("%team_size%", String.valueOf(
                        team.getConnectedPlayers().size()))
                .replace("%color%", TeamColor.fromApiColor(team.getColor())
                        .chatColor.toString()).replace("%team%", team.getName())
                .replace("%bed%", destroy ? game.bedLostString() : (empty ? game.anchorEmptyString() : game.bedExistString()));
    }

    public void registerTeam(org.screamingsandals.bedwars.api.Team team) {
        playerBoards.forEach((player, board) ->{
            board.addTeam(team.getName(), TeamColor.fromApiColor(team.getColor()).chatColor);
            player.setScoreboard(board.getBukkitScoreboard());
        });
    }

    public void unregisterTeam(org.screamingsandals.bedwars.game.Team team) {
        playerBoards.forEach((player, board) -> {
            board.removeTeam(team.name);
            player.setScoreboard(board.getBukkitScoreboard());
        });
    }

    public void handlePlayerJoin(Player player) {
        playerBoards.put(player, new ScreamingScoreboard(player, LOBBY_OBJECTIVE,
                Main.getConfigurator().config.getString("lobby-scoreboard.title", "§eBEDWARS")));
    }

    public void handleTeamLeave(Player left) {
        playerBoards.forEach((player, board) -> {
            board.removePlayer(left);
            player.setScoreboard(board.getBukkitScoreboard());
        });
    }



    public void destroy() {
        game.getConnectedPlayers().forEach(player -> player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard()));
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


    public void registerPlayerInTeam(Player toJoin, org.screamingsandals.bedwars.game.Team teamForJoin) {
        playerBoards.forEach((player, board) -> {
            board.addPlayerToTeam(toJoin, teamForJoin.getName());
            player.setScoreboard(board.getBukkitScoreboard());
        });
    }
}
