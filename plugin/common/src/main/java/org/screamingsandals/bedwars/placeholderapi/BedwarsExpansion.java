/*
 * Copyright (C) 2024 ScreamingSandals
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.lib.placeholders.PlaceholderExpansion;
import org.screamingsandals.lib.player.Players;
import org.screamingsandals.lib.sender.MultiPlatformOfflinePlayer;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Locale;

@Service
public class BedwarsExpansion extends PlaceholderExpansion {

    public BedwarsExpansion() {
        super("bedwars");
    }

    @Override
    @Nullable
    public Component onRequest(@Nullable MultiPlatformOfflinePlayer player, @NotNull String identifier) {
        // any game
        if (identifier.startsWith("game_")) {
            var gameName = identifier.substring(5);
            var index = gameName.lastIndexOf("_");
            var operation = gameName.substring(index + 1).toLowerCase();
            if (operation.equals("teams")) {
                index = gameName.lastIndexOf("_", index - 1);
                operation = gameName.substring(index + 1).toLowerCase();
            }
            gameName = gameName.substring(0, index);
            var gameOpt = GameManagerImpl.getInstance().getGame(gameName);
            if (gameOpt.isPresent()) {
                var game = gameOpt.get();
                // TODO: make all placeholders work for remote games
                if (game instanceof GameImpl) {
                    switch (operation) {
                        case "name":
                            return Component.text(game.getName());
                        case "displayName":
                            return ((GameImpl) game).getDisplayNameComponent().asComponent();
                        case "players":
                            return Component.text(((GameImpl) game).countConnectedPlayers());
                        case "maxplayers":
                            return Component.text(((GameImpl) game).getMaxPlayers());
                        case "minplayers":
                            return Component.text(((GameImpl) game).getMinPlayers());
                        case "world":
                            return Component.text(((GameImpl) game).getWorld().getName());
                        case "state":
                            return Component.text(game.getStatus().name().toLowerCase());
                        case "available_teams":
                            return Component.text(((GameImpl) game).countAvailableTeams());
                        case "connected_teams":
                            return Component.text(((GameImpl) game).countActiveTeams());
                        case "teamchests":
                            return Component.text(((GameImpl) game).countTeamChests());
                    }
                } else {
                    switch (operation) {
                        case "name":
                            return Component.text(game.getName());
                        case "state":
                            return Component.text(game.getStatus().name().toLowerCase());
                    }
                }
            }
        }

        if (identifier.startsWith("all_games_")) {
            var operation = identifier.substring(10).toLowerCase(Locale.ROOT);
            // TODO: count remote games
            switch (operation) {
                case "players":
                    return Component.text(GameManagerImpl.getInstance().getLocalGames().stream().mapToInt(GameImpl::countConnectedPlayers).sum());
                case "maxplayers":
                    return Component.text(GameManagerImpl.getInstance().getLocalGames().stream().mapToInt(GameImpl::getMaxPlayers).sum());
            }
        }

        // other player stats

        if (identifier.startsWith("otherstats_")) {
            if (!PlayerStatisticManager.isEnabled()) {
                return null;
            }
            var playerName = identifier.substring(11);
            var index = playerName.lastIndexOf("_");
            var operation = playerName.substring(index + 1).toLowerCase();
            if (operation.equals("beds")) {
                index = playerName.lastIndexOf("_", index - 1);
                operation = playerName.substring(index + 1).toLowerCase();
            }
            playerName = playerName.substring(0, index);

            var stats = PlayerStatisticManager.getInstance().getStatistic(
                    Players.getOfflinePlayer(playerName)
            );

            if (stats == null) {
                return null;
            }

            switch (operation) {
                case "deaths":
                    return Component.text(stats.getDeaths());
                case "destroyed_beds":
                    return Component.text(stats.getDestroyedBeds());
                case "kills":
                    return Component.text(stats.getKills());
                case "loses":
                    return Component.text(stats.getLoses());
                case "score":
                    return Component.text(stats.getScore());
                case "wins":
                    return Component.text(stats.getWins());
                case "games":
                    return Component.text(stats.getGames());
                case "kd":
                    return Component.text(stats.getKD());
            }
        }

        // Player
        if (player == null) {
            return Component.empty();
        }

        if (identifier.startsWith("current_")) {
            // current game
            switch (identifier.toLowerCase().substring(8)) {
                case "game":
                    return Component.text(PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(GameImpl::getName).orElse("none"));
                case "game_players":
                    return PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.countConnectedPlayers())).orElseGet(() -> Component.text("0"));
                case "game_time":
                    var m_Game = PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid());
                    if (m_Game.isEmpty() || m_Game.get().getStatus() != GameStatus.RUNNING)
                        return Component.text("0");
                    return Component.text(m_Game.get().getFormattedTimeLeft());
                case "game_maxplayers":
                    return PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.getMaxPlayers())).orElseGet(() -> Component.text("0"));
                case "game_minplayers":
                    return PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.getMinPlayers())).orElseGet(() -> Component.text("0"));
                case "game_world":
                    return Component.text(PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(g -> g.getWorld().getName()).orElse("none"));
                case "game_state":
                    return Component.text(PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(g -> g.getStatus().name().toLowerCase()).orElse("none"));
                case "available_teams":
                    return PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.countAvailableTeams())).orElseGet(() -> Component.text("0"));
                case "connected_teams":
                    return PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.countActiveTeams())).orElseGet(() -> Component.text("0"));
                case "teamchests":
                    return PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.countTeamChests())).orElseGet(() -> Component.text("0"));
                case "team":
                    if (PlayerManagerImpl.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player.getUuid()).orElseThrow();
                        var game = gPlayer.getGame();
                        var team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Component.text(team.getName());
                        } else if (gPlayer.isSpectator()) {
                            return Component.text("spectator");
                        }
                    }
                    return Component.text("none");
                case "team_colored":
                    if (PlayerManagerImpl.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player.getUuid()).orElseThrow();
                        var game = gPlayer.getGame();
                        var team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Component.text(team.getName(), team.getColor().getTextColor());
                        } else if (gPlayer.isSpectator()) {
                            return Component.text("spectator", Color.GRAY);
                        }
                    }
                    return Component.text("none", Color.RED);
                case "team_color":
                    if (PlayerManagerImpl.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player.getUuid()).orElseThrow();
                        var game = gPlayer.getGame();
                        var team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Component.text("", team.getColor().getTextColor());
                        } else {
                            return Component.text("", Color.GRAY);
                        }
                    } else {
                        return Component.empty();
                    }
                case "team_players":
                    if (PlayerManagerImpl.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player.getUuid()).orElseThrow();
                        var game = gPlayer.getGame();
                        var team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Component.text(team.countConnectedPlayers());
                        }
                    }
                    return Component.text("0");
                case "team_maxplayers":
                    if (PlayerManagerImpl.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player.getUuid()).orElseThrow();
                        var game = gPlayer.getGame();
                        var team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Component.text(team.getMaxPlayers());
                        }
                    }
                    return Component.text("0");
                case "team_bed":
                    if (PlayerManagerImpl.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player.getUuid()).orElseThrow();
                        var game = gPlayer.getGame();
                        var team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Component.text(team.getTarget().isValid() ? "yes" : "no");
                        }
                    }
                    return Component.text("no");
                case "team_teamchests":
                    if (PlayerManagerImpl.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player.getUuid()).orElseThrow();
                        var game = gPlayer.getGame();
                        var team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Component.text(team.countTeamChests());
                        }
                    }
                    return Component.text("0");
            }
        }

        // Stats

        if (identifier.startsWith("stats_")) {
            if (!PlayerStatisticManager.isEnabled()) {
                return null;
            }

            var stats = PlayerStatisticManager.getInstance().getStatistic(Players.wrapPlayer(player));

            if (stats == null) {
                return null;
            }

            switch (identifier.toLowerCase().substring(6)) {
                case "deaths":
                    return Component.text(stats.getDeaths());
                case "destroyed_beds":
                    return Component.text(stats.getDestroyedBeds());
                case "kills":
                    return Component.text(stats.getKills());
                case "loses":
                    return Component.text(stats.getLoses());
                case "score":
                    return Component.text(stats.getScore());
                case "wins":
                    return Component.text(stats.getWins());
                case "games":
                    return Component.text(stats.getGames());
                case "kd":
                    return Component.text(stats.getKD());
            }
        }

        return null;
    }

}
