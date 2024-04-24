/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.placeholderapi;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.game.Team;
import org.screamingsandals.bedwars.listener.Player116ListenerUtils;

import java.util.Locale;

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
            String operation = gameName.substring(index + 1).toLowerCase(Locale.ROOT);
            if (operation.equals("teams")) {
                index = gameName.lastIndexOf("_", index - 1);
                operation = gameName.substring(index + 1).toLowerCase(Locale.ROOT);
            } else if (gameName.contains("_team_")) {
                index = gameName.indexOf("_team_");
                operation = gameName.substring(index + 1).toLowerCase(Locale.ROOT);
            }
            gameName = gameName.substring(0, index);
            Game game = Main.getGame(gameName);
            if (game != null) {
                if (operation.startsWith("team_")) {
                    index = operation.lastIndexOf("_");
                    if (index != -1) {
                        String teamName = operation.substring(5, index);
                        String teamOperation = operation.substring(index + 1).toLowerCase(Locale.ROOT);

                        Team team = (Team) game.getTeamFromName(teamName);

                        if (team != null) {
                            switch (teamOperation) {
                                case "colored":
                                    return team.color.chatColor + team.getName();
                                case "color":
                                    return team.color.chatColor.toString();
                                case "ingame":
                                    return game.getCurrentTeamByTeam(team) != null ? "yes" : "no";
                                case "players": {
                                    CurrentTeam ct = game.getCurrentTeamByTeam(team);
                                    if (ct != null) {
                                        return Integer.toString(ct.countConnectedPlayers());
                                    } else {
                                        return "0";
                                    }
                                }
                                case "maxplayers":
                                    return Integer.toString(team.getMaxPlayers());
                                case "bed": {
                                    CurrentTeam ct = game.getCurrentTeamByTeam(team);
                                    if (ct != null) {
                                        return ct.isBed ? "yes" : "no";
                                    } else {
                                        return "no";
                                    }
                                }
                                case "bedsymbol": {
                                    CurrentTeam ct = game.getCurrentTeamByTeam(team);
                                    if (ct != null) {
                                        boolean empty = ct.isBed && "RESPAWN_ANCHOR".equals(team.bed.getBlock().getType().name()) && Player116ListenerUtils.isAnchorEmpty(team.bed.getBlock());
                                        return !ct.isBed ? Game.bedLostString() : (empty ? Game.anchorEmptyString() : Game.bedExistString());
                                    } else {
                                        return Game.bedLostString();
                                    }
                                }
                                case "teamchests": {
                                    CurrentTeam ct = game.getCurrentTeamByTeam(team);
                                    if (ct != null) {
                                        return Integer.toString(ct.countTeamChests());
                                    } else {
                                        return "0";
                                    }
                                }
                            }
                        }
                    }
                }
                switch (operation) {
                    case "name":
                        return game.getName();
                    case "players":
                        return Integer.toString(game.countConnectedPlayers());
                    case "maxplayers":
                        return Integer.toString(game.getMaxPlayers());
                    case "minplayers":
                        return Integer.toString(game.getMinPlayers());
                    case "time":
                        return Integer.toString(game.getCountdown());
                    case "timeformat":
                        return Game.getFormattedTimeLeftS(game.getCountdown());
                    case "elapsedtime":
                        switch (game.getStatus()) {
                            case WAITING:
                                return Integer.toString(game.getLobbyCountdown() - game.getCountdown());
                            case RUNNING:
                                return Integer.toString(game.getGameTime() - game.getCountdown());
                            case GAME_END_CELEBRATING:
                                return Integer.toString(game.getPostGameWaiting() - game.getCountdown());
                            case REBUILDING:
                            case DISABLED:
                                return "0";
                        }
                    case "elapsedtimeformat":
                        switch (game.getStatus()) {
                            case WAITING:
                                return Game.getFormattedTimeLeftS(game.getLobbyCountdown() - game.getCountdown());
                            case RUNNING:
                                return Game.getFormattedTimeLeftS(game.getGameTime() - game.getCountdown());
                            case GAME_END_CELEBRATING:
                                return Game.getFormattedTimeLeftS(game.getPostGameWaiting() - game.getCountdown());
                            case REBUILDING:
                            case DISABLED:
                                return Game.getFormattedTimeLeftS(0);
                        }
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

        if (identifier.startsWith("all_games_")) {
            String operation = identifier.substring(10).toLowerCase(Locale.ROOT);
            switch (operation) {
                case "players":
                    return Integer.toString(Main.getGameNames().stream().map(Main::getGame).mapToInt(Game::countConnectedPlayers).sum());
                case "maxplayers":
                    return Integer.toString(Main.getGameNames().stream().map(Main::getGame).mapToInt(Game::getMaxPlayers).sum());
            }
        }

        // other player stats

        if (identifier.startsWith("otherstats_")) {
            if (!Main.isPlayerStatisticsEnabled()) {
                return null;
            }
            String playerName = identifier.substring(11);
            int index = playerName.lastIndexOf("_");
            String operation = playerName.substring(index + 1).toLowerCase(Locale.ROOT);
            if (operation.equals("beds")) {
                index = playerName.lastIndexOf("_", index - 1);
                operation = playerName.substring(index + 1).toLowerCase(Locale.ROOT);
            }
            playerName = playerName.substring(0, index);

            PlayerStatistic stats = Main.getPlayerStatisticsManager().getStatistic(Bukkit.getOfflinePlayer(playerName));

            if (stats == null) {
                return null;
            }

            switch (operation) {
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
            if (identifier.toLowerCase(Locale.ROOT).startsWith("current_game_team_")) {
                String operation = identifier.substring(18);
                int index = operation.lastIndexOf("_");
                if (index != -1) {
                    String teamName = operation.substring(0, index);
                    String teamOperation = operation.substring(index + 1).toLowerCase(Locale.ROOT);

                    Game game = Main.isPlayerInGame(player) ? Main.getPlayerGameProfile(player).getGame() : null;
                    if (game != null) {
                        Team team = (Team) game.getTeamFromName(teamName);

                        if (team != null) {
                            switch (teamOperation) {
                                case "colored":
                                    return team.color.chatColor + team.getName();
                                case "color":
                                    return team.color.chatColor.toString();
                                case "ingame":
                                    return game.getCurrentTeamByTeam(team) != null ? "yes" : "no";
                                case "players": {
                                    CurrentTeam ct = game.getCurrentTeamByTeam(team);
                                    if (ct != null) {
                                        return Integer.toString(ct.countConnectedPlayers());
                                    } else {
                                        return "0";
                                    }
                                }
                                case "maxplayers":
                                    return Integer.toString(team.getMaxPlayers());
                                case "bed": {
                                    CurrentTeam ct = game.getCurrentTeamByTeam(team);
                                    if (ct != null) {
                                        return ct.isBed ? "yes" : "no";
                                    } else {
                                        return "no";
                                    }
                                }
                                case "bedsymbol": {
                                    CurrentTeam ct = game.getCurrentTeamByTeam(team);
                                    if (ct != null) {
                                        boolean empty = ct.isBed && "RESPAWN_ANCHOR".equals(team.bed.getBlock().getType().name()) && Player116ListenerUtils.isAnchorEmpty(team.bed.getBlock());
                                        return !ct.isBed ? Game.bedLostString() : (empty ? Game.anchorEmptyString() : Game.bedExistString());
                                    } else {
                                        return Game.bedLostString();
                                    }
                                }
                                case "teamchests": {
                                    CurrentTeam ct = game.getCurrentTeamByTeam(team);
                                    if (ct != null) {
                                        return Integer.toString(ct.countTeamChests());
                                    } else {
                                        return "0";
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // current game
            switch (identifier.toLowerCase(Locale.ROOT).substring(8)) {
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
                case "game_time":
                    if (Main.isPlayerInGame(player)) {
                        return Integer.toString(Main.getPlayerGameProfile(player).getGame().getCountdown());
                    } else {
                        return "0";
                    }
                case "game_timeformat":
                    if (Main.isPlayerInGame(player)) {
                        return Game.getFormattedTimeLeftS(Main.getPlayerGameProfile(player).getGame().getCountdown());
                    } else {
                        return "0";
                    }
                case "game_elapsedtime":
                    if (Main.isPlayerInGame(player)) {
                        Game game = Main.getPlayerGameProfile(player).getGame();
                        switch (game.getStatus()) {
                            case WAITING:
                                return Integer.toString(game.getLobbyCountdown() - game.getCountdown());
                            case RUNNING:
                                return Integer.toString(game.getGameTime() - game.getCountdown());
                            case GAME_END_CELEBRATING:
                                return Integer.toString(game.getPostGameWaiting() - game.getCountdown());
                            case REBUILDING:
                            case DISABLED:
                                return "0";
                        }
                    } else {
                        return "0";
                    }
                case "game_elapsedtimeformat":
                    if (Main.isPlayerInGame(player)) {
                        Game game = Main.getPlayerGameProfile(player).getGame();
                        switch (game.getStatus()) {
                            case WAITING:
                                return Game.getFormattedTimeLeftS(game.getLobbyCountdown() - game.getCountdown());
                            case RUNNING:
                                return Game.getFormattedTimeLeftS(game.getGameTime() - game.getCountdown());
                            case GAME_END_CELEBRATING:
                                return Game.getFormattedTimeLeftS(game.getPostGameWaiting() - game.getCountdown());
                            case REBUILDING:
                            case DISABLED:
                                return Game.getFormattedTimeLeftS(0);
                        }
                    } else {
                        return Game.getFormattedTimeLeftS(0);
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
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return team.getName();
                        } else if (gPlayer.isSpectator) {
                            return "spectator";
                        } else {
                            return "none";
                        }
                    } else {
                        return "none";
                    }
                case "team_colored":
                    if (Main.isPlayerInGame(player)) {
                        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                        Game game = gPlayer.getGame();
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return team.teamInfo.color.chatColor + team.getName();
                        } else if (gPlayer.isSpectator) {
                            return ChatColor.GRAY + "spectator";
                        } else {
                            return ChatColor.RED + "none";
                        }
                    } else {
                        return ChatColor.RED + "none";
                    }
                case "team_color":
                    if (Main.isPlayerInGame(player)) {
                        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                        Game game = gPlayer.getGame();
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return team.teamInfo.color.chatColor.toString();
                        } else {
                            return ChatColor.GRAY.toString();
                        }
                    } else {
                        return "";
                    }
                case "team_players":
                    if (Main.isPlayerInGame(player)) {
                        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                        Game game = gPlayer.getGame();
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Integer.toString(team.countConnectedPlayers());
                        } else {
                            return "0";
                        }
                    } else {
                        return "0";
                    }
                case "team_maxplayers":
                    if (Main.isPlayerInGame(player)) {
                        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                        Game game = gPlayer.getGame();
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Integer.toString(team.getMaxPlayers());
                        } else {
                            return "0";
                        }
                    } else {
                        return "0";
                    }
                case "team_bed":
                    if (Main.isPlayerInGame(player)) {
                        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                        Game game = gPlayer.getGame();
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return team.isBed ? "yes" : "no";
                        } else {
                            return "no";
                        }
                    } else {
                        return "no";
                    }
                case "team_bedsymbol":
                    if (Main.isPlayerInGame(player)) {
                        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                        Game game = gPlayer.getGame();
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            boolean empty = team.isBed && "RESPAWN_ANCHOR".equals(team.teamInfo.bed.getBlock().getType().name()) && Player116ListenerUtils.isAnchorEmpty(team.teamInfo.bed.getBlock());
                            return !team.isBed ? Game.bedLostString() : (empty ? Game.anchorEmptyString() : Game.bedExistString());
                        } else {
                            return Game.bedLostString();
                        }
                    } else {
                        return Game.bedLostString();
                    }
                case "team_teamchests":
                    if (Main.isPlayerInGame(player)) {
                        GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                        Game game = gPlayer.getGame();
                        CurrentTeam team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Integer.toString(team.countTeamChests());
                        } else {
                            return "0";
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

            switch (identifier.toLowerCase(Locale.ROOT).substring(6)) {
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

    @Override
    public boolean persist() {
        return true;
    }
}
