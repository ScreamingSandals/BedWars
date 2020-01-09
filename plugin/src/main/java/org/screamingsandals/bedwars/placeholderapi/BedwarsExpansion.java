package org.screamingsandals.bedwars.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;

public class BedwarsExpansion extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return String.join(", ", Main.getInstance().getDescription().getAuthors());
    }

    @Override
    public String getIdentifier() {
        return "bedwars";
    }

    @Override
    public String getVersion() {
        return Main.getVersion();
    }

    @Override
    public String getPlugin() {
        return Main.getInstance().getName();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        // Player
        if (player == null) {
            return "";
        }

        // current game
        switch (identifier.toLowerCase()) {
            case "current_game":
                return Main.isPlayerInGame(player) ? Main.getPlayerGameProfile(player).getGame().getName() : "none";
            case "current_game_players":
                if (Main.isPlayerInGame(player)) {
                    return Integer.toString(Main.getPlayerGameProfile(player).getGame().countConnectedPlayers());
                } else {
                    return "0";
                }
            case "current_game_maxplayers":
                if (Main.isPlayerInGame(player)) {
                    return Integer.toString(Main.getPlayerGameProfile(player).getGame().getMaxPlayers());
                } else {
                    return "0";
                }
            case "current_game_minplayers":
                if (Main.isPlayerInGame(player)) {
                    return Integer.toString(Main.getPlayerGameProfile(player).getGame().getMinPlayers());
                } else {
                    return "0";
                }
            case "current_game_world":
                if (Main.isPlayerInGame(player)) {
                    return Main.getPlayerGameProfile(player).getGame().getWorld().getName();
                } else {
                    return "none";
                }
            case "current_game_state":
                if (Main.isPlayerInGame(player)) {
                    return Main.getPlayerGameProfile(player).getGame().getStatus().name().toLowerCase();
                } else {
                    return "none";
                }
            case "current_available_teams":
                if (Main.isPlayerInGame(player)) {
                    return Integer.toString(Main.getPlayerGameProfile(player).getGame().countAvailableTeams());
                } else {
                    return "0";
                }
            case "current_connected_teams":
                if (Main.isPlayerInGame(player)) {
                    return Integer.toString(Main.getPlayerGameProfile(player).getGame().countRunningTeams());
                } else {
                    return "0";
                }
            case "current_teamchests":
                if (Main.isPlayerInGame(player)) {
                    return Integer.toString(Main.getPlayerGameProfile(player).getGame().countTeamChests());
                } else {
                    return "0";
                }
            case "current_team":
                if (Main.isPlayerInGame(player)) {
                    GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                    Game game = gPlayer.getGame();
                    if (gPlayer.isSpectator) {
                        return "spectator";
                    } else {
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return team.getName();
                        } else {
                            return "none";
                        }
                    }
                } else {
                    return "none";
                }
            case "current_team_colored":
                if (Main.isPlayerInGame(player)) {
                    GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                    Game game = gPlayer.getGame();
                    if (gPlayer.isSpectator) {
                        return ChatColor.GRAY + "spectator";
                    } else {
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return team.teamInfo.color.chatColor + team.getName();
                        } else {
                            return ChatColor.RED + "none";
                        }
                    }
                } else {
                    return ChatColor.RED + "none";
                }
            case "current_team_players":
                if (Main.isPlayerInGame(player)) {
                    GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                    Game game = gPlayer.getGame();
                    if (gPlayer.isSpectator) {
                        return "0";
                    } else {
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Integer.toString(team.countConnectedPlayers());
                        } else {
                            return "0";
                        }
                    }
                } else {
                    return "0";
                }
            case "current_team_maxplayers":
                if (Main.isPlayerInGame(player)) {
                    GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                    Game game = gPlayer.getGame();
                    if (gPlayer.isSpectator) {
                        return "0";
                    } else {
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Integer.toString(team.getMaxPlayers());
                        } else {
                            return "0";
                        }
                    }
                } else {
                    return "0";
                }
            case "current_team_bed":
                if (Main.isPlayerInGame(player)) {
                    GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                    Game game = gPlayer.getGame();
                    if (gPlayer.isSpectator) {
                        return "no";
                    } else {
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return team.isBed ? "yes" : "no";
                        } else {
                            return "no";
                        }
                    }
                } else {
                    return "no";
                }
            case "current_team_teamchests":
                if (Main.isPlayerInGame(player)) {
                    GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                    Game game = gPlayer.getGame();
                    if (gPlayer.isSpectator) {
                        return "0";
                    } else {
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Integer.toString(team.countTeamChests());
                        } else {
                            return "0";
                        }
                    }
                } else {
                    return "0";
                }
        }

        // Stats

        if (!Main.isPlayerStatisticsEnabled()) {
            return null;
        }

        PlayerStatistic stats = Main.getPlayerStatisticsManager().getStatistic(player);

        if (stats == null) {
            return null;
        }

        switch (identifier.toLowerCase()) {
            case "stats_deaths":
                return Integer.toString(stats.getCurrentDeaths() + stats.getDeaths());
            case "stats_destroyed_beds":
                return Integer.toString(stats.getCurrentDestroyedBeds() + stats.getDestroyedBeds());
            case "stats_kills":
                return Integer.toString(stats.getCurrentKills() + stats.getKills());
            case "stats_loses":
                return Integer.toString(stats.getCurrentLoses() + stats.getLoses());
            case "stats_score":
                return Integer.toString(stats.getCurrentScore() + stats.getScore());
            case "stats_wins":
                return Integer.toString(stats.getCurrentWins() + stats.getWins());
            case "stats_games":
                return Integer.toString(stats.getCurrentGames() + stats.getGames());
            case "stats_kd":
                return Double.toString(stats.getKD());
        }

        return null;
    }

}
