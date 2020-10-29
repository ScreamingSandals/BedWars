package org.screamingsandals.bedwars.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
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
        // any game
        if (identifier.startsWith("game_")) {
            String gameName = identifier.substring(5);
            int index = gameName.lastIndexOf("_");
            String operation = gameName.substring(index).toLowerCase();
            gameName = gameName.substring(0, index);
            Game game = Main.getGame(gameName);
            if (game != null) {
                switch (operation) {
                    case "name":
                        return game.getName();
                    case "players":
                        return Integer.toString(game.countConnectedPlayers());
                    case "maxplayers":
                        return Integer.toString(game.getMaxPlayers());
                    case "minplayers":
                        return Integer.toString(game.getMinPlayers());
                    case "world":
                        return game.getWorld().getName();
                    case "state":
                        return game.getStatus().name().toLowerCase();
                    case "aviable_teams": // Wow, you found an easter egg
                    case "available_teams":
                        return Integer.toString(game.countAvailableTeams());
                    case "connected_teams":
                        return Integer.toString(game.countRunningTeams());
                    case "teamchests":
                        return Integer.toString(game.countTeamChests());
                }
            }
        }

        // other player stats

        if (identifier.startsWith("otherstats_")) {
            if (!Main.isPlayerStatisticsEnabled()) {
                return null;
            }
            String playerName = identifier.substring(5);
            int index = playerName.lastIndexOf("_");
            String operation = playerName.substring(index).toLowerCase();
            if (operation.equals("beds")) {
                index = playerName.lastIndexOf("_", index - 1);
                operation = playerName.substring(index).toLowerCase();
            }
            playerName = playerName.substring(0, index);

            PlayerStatistic stats = Main.getPlayerStatisticsManager().getStatistic(Bukkit.getOfflinePlayer(playerName));

            if (stats == null) {
                return null;
            }

            switch (identifier.toLowerCase().substring(11)) {
                case "deaths":
                    return Integer.toString(stats.getDeaths());
                case "destroyed_beds":
                    return Integer.toString(stats.getDestroyedBeds());
                case "kills":
                    return Integer.toString(stats.getKills());
                case "loses":
                    return Integer.toString(stats.getLoses());
                case "score":
                    return Integer.toString(stats.getScore());
                case "wins":
                    return Integer.toString(stats.getWins());
                case "games":
                    return Integer.toString(stats.getGames());
                case "kd":
                    return Double.toString(stats.getKD());
            }
        }

        // Player
        if (player == null) {
            return "";
        }

        if (identifier.startsWith("current_")) {
            // current game
            switch (identifier.toLowerCase().substring(8)) {
                case "game":
                    return Main.isPlayerInGame(player) ? Main.getPlayerGameProfile(player).getGame().getName() : "none";
                case "game_players":
                    if (Main.isPlayerInGame(player)) {
                        return Integer.toString(Main.getPlayerGameProfile(player).getGame().countConnectedPlayers());
                    } else {
                        return "0";
                    }
                case "game_maxplayers":
                    if (Main.isPlayerInGame(player)) {
                        return Integer.toString(Main.getPlayerGameProfile(player).getGame().getMaxPlayers());
                    } else {
                        return "0";
                    }
                case "game_minplayers":
                    if (Main.isPlayerInGame(player)) {
                        return Integer.toString(Main.getPlayerGameProfile(player).getGame().getMinPlayers());
                    } else {
                        return "0";
                    }
                case "game_world":
                    if (Main.isPlayerInGame(player)) {
                        return Main.getPlayerGameProfile(player).getGame().getWorld().getName();
                    } else {
                        return "none";
                    }
                case "game_state":
                    if (Main.isPlayerInGame(player)) {
                        return Main.getPlayerGameProfile(player).getGame().getStatus().name().toLowerCase();
                    } else {
                        return "none";
                    }
                case "aviable_teams": // Wow, you found an easter egg
                case "available_teams":
                    if (Main.isPlayerInGame(player)) {
                        return Integer.toString(Main.getPlayerGameProfile(player).getGame().countAvailableTeams());
                    } else {
                        return "0";
                    }
                case "connected_teams":
                    if (Main.isPlayerInGame(player)) {
                        return Integer.toString(Main.getPlayerGameProfile(player).getGame().countRunningTeams());
                    } else {
                        return "0";
                    }
                case "teamchests":
                    if (Main.isPlayerInGame(player)) {
                        return Integer.toString(Main.getPlayerGameProfile(player).getGame().countTeamChests());
                    } else {
                        return "0";
                    }
                case "team":
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
                case "team_colored":
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
                case "team_players":
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
                case "team_maxplayers":
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
                case "team_bed":
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
                case "team_teamchests":
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
        }

        // Stats

        if (identifier.startsWith("stats_")) {
            if (!Main.isPlayerStatisticsEnabled()) {
                return null;
            }

            PlayerStatistic stats = Main.getPlayerStatisticsManager().getStatistic(player);

            if (stats == null) {
                return null;
            }

            switch (identifier.toLowerCase().substring(6)) {
                case "deaths":
                    return Integer.toString(stats.getDeaths());
                case "destroyed_beds":
                    return Integer.toString(stats.getDestroyedBeds());
                case "kills":
                    return Integer.toString(stats.getKills());
                case "loses":
                    return Integer.toString(stats.getLoses());
                case "score":
                    return Integer.toString(stats.getScore());
                case "wins":
                    return Integer.toString(stats.getWins());
                case "games":
                    return Integer.toString(stats.getGames());
                case "kd":
                    return Double.toString(stats.getKD());
            }
        }

        return null;
    }

}
