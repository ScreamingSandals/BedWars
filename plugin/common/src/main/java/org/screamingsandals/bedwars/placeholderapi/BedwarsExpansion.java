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
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.game.RemoteGame;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.game.target.AExpirableTarget;
import org.screamingsandals.bedwars.game.target.TargetBlockImpl;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.lib.lang.Message;
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
    public @Nullable Component onRequest(@Nullable MultiPlatformOfflinePlayer player, @NotNull String identifier) {
        // any game
        if (identifier.startsWith("game_")) {
            var gameName = identifier.substring(5);
            var index = gameName.lastIndexOf("_");
            var operation = gameName.substring(index + 1).toLowerCase(Locale.ROOT);
            if ("teams".equals(operation)) {
                index = gameName.lastIndexOf("_", index - 1);
                operation = gameName.substring(index + 1).toLowerCase(Locale.ROOT);
            } else if (gameName.contains("_team_")) {
                index = gameName.indexOf("_team_");
                operation = gameName.substring(index + 1).toLowerCase(Locale.ROOT);
            }
            gameName = gameName.substring(0, index);
            var gameOpt = GameManagerImpl.getInstance().getGame(gameName);
            if (gameOpt.isPresent()) {
                var g = gameOpt.get();
                if (g instanceof GameImpl) {
                    var game = (GameImpl) g;
                    if (operation.startsWith("team_")) {
                        index = operation.lastIndexOf("_");
                        if (index != -1) {
                            var teamName = operation.substring(5, index);
                            var teamOperation = operation.substring(index + 1).toLowerCase(Locale.ROOT);

                            TeamImpl team = game.getTeamFromName(teamName);

                            if (team != null) {
                                switch (teamOperation) {
                                    case "colored":
                                        return Component.text(team.getName(), team.getColor().getTextColor());
                                    case "color":
                                        return Component.text("", team.getColor().getTextColor());
                                    case "ingame":
                                        return Component.text(team.isStarted() ? "yes" : "no");
                                    case "players":
                                        return Component.text(team.countConnectedPlayers());
                                    case "maxplayers":
                                        return Component.text(team.getMaxPlayers());
                                    case "bed": // 0.2.x
                                    case "targetvalid":
                                        return Component.text(team.isStarted() && team.getTarget().isValid() ? "yes" : "no");
                                    case "bedsymbol": // 0.2.x
                                    case "targetvalidsymbol": {
                                        if (team.isStarted() && team.getTarget().isValid()) {
                                            if (team.getTarget() instanceof TargetBlockImpl && ((TargetBlockImpl) team.getTarget()).isEmpty()) {
                                                return Message.ofRichText(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_ANCHOR_EMPTY, "")).asComponent();
                                            } else if (team.getTarget() instanceof AExpirableTarget && ((AExpirableTarget) team.getTarget()).getRemainingTime() < 30) {
                                                return Message.ofRichText(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_TEAM_COUNT, ""))
                                                        .placeholder("count", Component.text(((AExpirableTarget) team.getTarget()).getRemainingTime() + " "))
                                                        .asComponent();
                                            } else {
                                                return Message.ofRichText(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_TARGET_BLOCK_EXISTS, "")).asComponent();
                                            }
                                        } else {
                                            return Message.ofRichText(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_TARGET_BLOCK_LOST, "")).asComponent();
                                        }
                                    }
                                    case "teamchests":
                                        return Component.text(team.countTeamChests());
                                }
                            }
                        }
                    }
                    switch (operation) {
                        case "name":
                            return Component.text(game.getName());
                        case "displayname":
                            return game.getDisplayNameComponent().asComponent();
                        case "players":
                            return Component.text(game.countConnectedPlayers());
                        case "maxplayers":
                            return Component.text(game.getMaxPlayers());
                        case "minplayers":
                            return Component.text(game.getMinPlayers());
                        case "time":
                            return Component.text(game.getTimeLeft());
                        case "timeformat":
                            return Component.text(game.getFormattedTimeLeft());
                        case "elapsedtime":
                            switch (game.getStatus()) {
                                case WAITING:
                                    return Component.text(game.getLobbyCountdown() - game.getTimeLeft());
                                case RUNNING:
                                    return Component.text(game.getGameTime() - game.getTimeLeft());
                                case GAME_END_CELEBRATING:
                                    return Component.text(game.getPostGameWaiting() - game.getTimeLeft());
                                case REBUILDING:
                                case DISABLED:
                                    return Component.text("0");
                            }
                        case "elapsedtimeformat":
                            switch (game.getStatus()) {
                                case WAITING:
                                    return Component.text(GameImpl.getFormattedTimeLeft(game.getLobbyCountdown() - game.getTimeLeft()));
                                case RUNNING:
                                    return Component.text(GameImpl.getFormattedTimeLeft(game.getGameTime() - game.getTimeLeft()));
                                case GAME_END_CELEBRATING:
                                    return Component.text(GameImpl.getFormattedTimeLeft(game.getPostGameWaiting() - game.getTimeLeft()));
                                case REBUILDING:
                                case DISABLED:
                                    return Component.text(GameImpl.getFormattedTimeLeft(0));
                            }
                        case "world":
                            return Component.text(game.getWorld().getName());
                        case "state":
                            return Component.text(game.getStatus().name().toLowerCase(Locale.ROOT));
                        case "running":
                            return Component.text(game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING ? "true" : "false");
                        case "waiting":
                            return Component.text(game.getStatus() == GameStatus.WAITING ? "true" : "false");
                        case "available_teams":
                            return Component.text(game.countAvailableTeams());
                        case "connected_teams":
                            return Component.text(game.countActiveTeams());
                        case "teamchests":
                            return Component.text(game.countTeamChests());
                    }
                } else {
                    var remoteGame = (RemoteGame) g;

                    switch (operation) {
                        case "name":
                            return Component.text(remoteGame.getName());
                        case "displayname":
                            return remoteGame.getDisplayNameComponent().as(Component.class);
                        case "players":
                            return Component.text(remoteGame.countConnectedPlayers());
                        case "maxplayers":
                            return Component.text(remoteGame.getMaxPlayers());
                        case "minplayers":
                            return Component.text(remoteGame.getMinPlayers());
                        case "time": {
                            var timeLeft = remoteGame.getTimeLeftInCurrentState();
                            return Component.text(timeLeft != null ? timeLeft : 0);
                        }
                        case "timeformat": {
                            var timeLeft = remoteGame.getTimeLeftInCurrentState();
                            return Component.text(GameImpl.getFormattedTimeLeft(timeLeft != null ? timeLeft : 0));
                        }
                        case "elapsedtime":
                            switch (remoteGame.getStatus()) {
                                case WAITING:
                                case RUNNING:
                                case GAME_END_CELEBRATING:
                                    var elapsed = remoteGame.getElapsedTimeInCurrentState();
                                    return Component.text(elapsed != null ? elapsed : 0);
                                case REBUILDING:
                                case DISABLED:
                                    return Component.text("0");
                            }
                        case "elapsedtimeformat":
                            switch (remoteGame.getStatus()) {
                                case WAITING:
                                case RUNNING:
                                case GAME_END_CELEBRATING:
                                    var elapsed = remoteGame.getElapsedTimeInCurrentState();
                                    return Component.text(GameImpl.getFormattedTimeLeft(elapsed != null ? elapsed : 0));
                                case REBUILDING:
                                case DISABLED:
                                    return Component.text(GameImpl.getFormattedTimeLeft(0));
                            }
                        case "state":
                            return Component.text(remoteGame.getStatus().name().toLowerCase(Locale.ROOT));
                        case "running":
                            return Component.text(remoteGame.getStatus() == GameStatus.RUNNING || remoteGame.getStatus() == GameStatus.GAME_END_CELEBRATING ? "true" : "false");
                        case "waiting":
                            return Component.text(remoteGame.getStatus() == GameStatus.WAITING ? "true" : "false");
                        case "available_teams":
                            return Component.text(remoteGame.countAvailableTeams());
                        case "connected_teams":
                            return Component.text(remoteGame.countActiveTeams());
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
                case "anyrunning":
                    return Component.text(GameManagerImpl.getInstance().getLocalGames().stream().anyMatch(game -> game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) ? "true" : "false");
                case "anywaiting":
                    return Component.text(GameManagerImpl.getInstance().getLocalGames().stream().anyMatch(game -> game.getStatus() == GameStatus.WAITING) ? "true" : "false");
            }
        }

        // other player stats

        if (identifier.startsWith("otherstats_")) {
            if (!PlayerStatisticManager.isEnabled()) {
                return null;
            }
            var playerName = identifier.substring(11);
            var index = playerName.lastIndexOf("_");
            var operation = playerName.substring(index + 1).toLowerCase(Locale.ROOT);
            if (operation.equals("beds")) {
                index = playerName.lastIndexOf("_", index - 1);
                operation = playerName.substring(index + 1).toLowerCase(Locale.ROOT);
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
            if (identifier.toLowerCase(Locale.ROOT).startsWith("current_game_team_")) {
                String operation = identifier.substring(18);
                int index = operation.lastIndexOf("_");
                if (index != -1) {
                    String teamName = operation.substring(0, index);
                    String teamOperation = operation.substring(index + 1).toLowerCase(Locale.ROOT);

                    var game = PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).orElse(null);
                    if (game != null) {
                        TeamImpl team = game.getTeamFromName(teamName);

                        if (team != null) {
                            switch (teamOperation) {
                                case "colored":
                                    return Component.text(team.getName(), team.getColor().getTextColor());
                                case "color":
                                    return Component.text("", team.getColor().getTextColor());
                                case "ingame":
                                    return Component.text(team.isStarted() ? "yes" : "no");
                                case "players":
                                    return Component.text(team.countConnectedPlayers());
                                case "maxplayers":
                                    return Component.text(team.getMaxPlayers());
                                case "bed": // 0.2.x
                                case "targetvalid":
                                    return Component.text(team.isStarted() && team.getTarget().isValid() ? "yes" : "no");
                                case "bedsymbol": // 0.2.x
                                case "targetvalidsymbol": {
                                    if (team.isStarted() && team.getTarget().isValid()) {
                                        if (team.getTarget() instanceof TargetBlockImpl && ((TargetBlockImpl) team.getTarget()).isEmpty()) {
                                            return Message.ofRichText(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_ANCHOR_EMPTY, "")).asComponent();
                                        } else if (team.getTarget() instanceof AExpirableTarget && ((AExpirableTarget) team.getTarget()).getRemainingTime() < 30) {
                                            return Message.ofRichText(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_TEAM_COUNT, ""))
                                                    .placeholder("count", Component.text(((AExpirableTarget) team.getTarget()).getRemainingTime() + " "))
                                                    .asComponent();
                                        } else {
                                            return Message.ofRichText(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_TARGET_BLOCK_EXISTS, "")).asComponent();
                                        }
                                    } else {
                                        return Message.ofRichText(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_TARGET_BLOCK_LOST, "")).asComponent();
                                    }
                                }
                                case "teamchests":
                                    return Component.text(team.countTeamChests());
                            }
                        }
                    }
                }
            }
            // current game
            switch (identifier.toLowerCase(Locale.ROOT).substring(8)) {
                case "game":
                    return Component.text(PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(GameImpl::getName).orElse("none"));
                case "game_displayname":
                    return PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(GameImpl::getDisplayNameComponent).orElseGet(() -> Component.text("none"));
                case "game_players":
                    return PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.countConnectedPlayers())).orElseGet(() -> Component.text("0"));
                case "game_time": {
                    var m_Game = PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid());
                    if (m_Game.isEmpty()) {
                        return Component.text("0");
                    }
                    return Component.text(m_Game.get().getTimeLeft());
                }
                case "game_timeformat": {
                    var m_Game = PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid());
                    if (m_Game.isEmpty()) {
                        return Component.text("0");
                    }
                    return Component.text(m_Game.get().getFormattedTimeLeft());
                }
                case "game_elapsedtime": {
                    var m_Game = PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid());
                    if (m_Game.isEmpty()) {
                        return Component.text("0");
                    }
                    switch (m_Game.get().getStatus()) {
                        case WAITING:
                            return Component.text(m_Game.get().getLobbyCountdown() - m_Game.get().getTimeLeft());
                        case RUNNING:
                            return Component.text(m_Game.get().getGameTime() - m_Game.get().getTimeLeft());
                        case GAME_END_CELEBRATING:
                            return Component.text(m_Game.get().getPostGameWaiting() - m_Game.get().getTimeLeft());
                        case REBUILDING:
                        case DISABLED:
                            return Component.text("0");
                    }
                }
                case "game_elapsedtimeformat": {
                    var m_Game = PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid());
                    if (m_Game.isEmpty()) {
                        return Component.text("0");
                    }
                    switch (m_Game.get().getStatus()) {
                        case WAITING:
                            return Component.text(GameImpl.getFormattedTimeLeft(m_Game.get().getLobbyCountdown() - m_Game.get().getTimeLeft()));
                        case RUNNING:
                            return Component.text(GameImpl.getFormattedTimeLeft(m_Game.get().getGameTime() - m_Game.get().getTimeLeft()));
                        case GAME_END_CELEBRATING:
                            return Component.text(GameImpl.getFormattedTimeLeft(m_Game.get().getPostGameWaiting() - m_Game.get().getTimeLeft()));
                        case REBUILDING:
                        case DISABLED:
                            return Component.text("0");
                    }
                }
                case "game_maxplayers":
                    return PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.getMaxPlayers())).orElseGet(() -> Component.text("0"));
                case "game_minplayers":
                    return PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.getMinPlayers())).orElseGet(() -> Component.text("0"));
                case "game_world":
                    return Component.text(PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(g -> g.getWorld().getName()).orElse("none"));
                case "game_state":
                    return Component.text(PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid()).map(g -> g.getStatus().name().toLowerCase(Locale.ROOT)).orElse("none"));
                case "game_running":
                    return Component.text(PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid())
                            .map(game -> game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING ? "true" : "false")
                            .orElse("false"));
                case "game_waiting":
                    return Component.text(PlayerManagerImpl.getInstance().getGameOfPlayer(player.getUuid())
                            .map(game -> game.getStatus() == GameStatus.WAITING ? "true" : "false")
                            .orElse("false"));
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
                case "team_bed": // 0.2.x
                case "team_targetvalid":
                    if (PlayerManagerImpl.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player.getUuid()).orElseThrow();
                        var game = gPlayer.getGame();
                        var team = game.getPlayerTeam(gPlayer);
                        if (team != null) {
                            return Component.text(team.getTarget().isValid() ? "yes" : "no");
                        }
                    }
                    return Component.text("no");
                case "team_bedsymbol": // 0.2.x
                case "team_targetvalidsymbol": {
                    if (PlayerManagerImpl.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManagerImpl.getInstance().getPlayer(player.getUuid()).orElseThrow();
                        var game = gPlayer.getGame();
                        var team = game.getPlayerTeam(gPlayer);
                        if (team != null && team.getTarget().isValid()) {
                            if (team.getTarget() instanceof TargetBlockImpl && ((TargetBlockImpl) team.getTarget()).isEmpty()) {
                                return Message.ofRichText(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_ANCHOR_EMPTY, "")).asComponent();
                            } else if (team.getTarget() instanceof AExpirableTarget && ((AExpirableTarget) team.getTarget()).getRemainingTime() < 30) {
                                return Message.ofRichText(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_TEAM_COUNT, ""))
                                        .placeholder("count", Component.text(((AExpirableTarget) team.getTarget()).getRemainingTime() + " "))
                                        .asComponent();
                            } else {
                                return Message.ofRichText(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_TARGET_BLOCK_EXISTS, "")).asComponent();
                            }
                        } else {
                            return Message.ofRichText(game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_TARGET_BLOCK_LOST, "")).asComponent();
                        }
                    }
                    return Message.ofRichText(MainConfig.getInstance().node("sidebar", "game", "team-prefixes", "target-block-lost").getString("no")).asComponent();
                }
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

            switch (identifier.toLowerCase(Locale.ROOT).substring(6)) {
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
