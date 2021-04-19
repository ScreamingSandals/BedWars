package org.screamingsandals.bedwars.placeholderapi;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GameManager;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.lib.placeholders.PlaceholderExpansion;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.sender.MultiPlatformOfflinePlayer;
import org.screamingsandals.lib.utils.annotations.Service;

@Service(dependsOn = {
        PlayerMapper.class
})
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
            var gameOpt = GameManager.getInstance().getGame(gameName);
            if (gameOpt.isPresent()) {
                var game = gameOpt.get();
                switch (operation) {
                    case "name":
                        return Component.text(game.getName());
                    case "players":
                        return Component.text(game.countConnectedPlayers());
                    case "maxplayers":
                        return Component.text(game.getMaxPlayers());
                    case "minplayers":
                        return Component.text(game.getMinPlayers());
                    case "world":
                        return Component.text(game.getWorld().getName());
                    case "state":
                        return Component.text(game.getStatus().name().toLowerCase());
                    case "available_teams":
                        return Component.text(game.countAvailableTeams());
                    case "connected_teams":
                        return Component.text(game.countRunningTeams());
                    case "teamchests":
                        return Component.text(game.countTeamChests());
                }
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
                    PlayerMapper.wrapOfflinePlayer(Bukkit.getOfflinePlayer(playerName))
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
                    return Component.text(PlayerManager.getInstance().getGameOfPlayer(player.getUuid()).map(Game::getName).orElse("none"));
                case "game_players":
                    return PlayerManager.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.countConnectedPlayers())).orElseGet(() -> Component.text("0"));
                case "game_time":
                    var m_Game = PlayerManager.getInstance().getGameOfPlayer(player.getUuid());
                    if (m_Game.isEmpty() || m_Game.get().getStatus() != GameStatus.RUNNING)
                        return Component.text("0");
                    return Component.text(m_Game.get().getFormattedTimeLeft());
                case "game_maxplayers":
                    return PlayerManager.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.getMaxPlayers())).orElseGet(() -> Component.text("0"));
                case "game_minplayers":
                    return PlayerManager.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.getMinPlayers())).orElseGet(() -> Component.text("0"));
                case "game_world":
                    return Component.text(PlayerManager.getInstance().getGameOfPlayer(player.getUuid()).map(g -> g.getWorld().getName()).orElse("none"));
                case "game_state":
                    return Component.text(PlayerManager.getInstance().getGameOfPlayer(player.getUuid()).map(g -> g.getStatus().name().toLowerCase()).orElse("none"));
                case "available_teams":
                    return PlayerManager.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.countAvailableTeams())).orElseGet(() -> Component.text("0"));
                case "connected_teams":
                    return PlayerManager.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.countRunningTeams())).orElseGet(() -> Component.text("0"));
                case "teamchests":
                    return PlayerManager.getInstance().getGameOfPlayer(player.getUuid()).map(g -> Component.text(g.countTeamChests())).orElseGet(() -> Component.text("0"));
                case "team":
                    if (PlayerManager.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManager.getInstance().getPlayer(player.getUuid()).get();
                        var game = gPlayer.getGame();
                        if (gPlayer.isSpectator) {
                            return Component.text("spectator");
                        } else {
                            var team = game.getPlayerTeam(gPlayer);
                            if (team != null) {
                                return Component.text(team.getName());
                            } else {
                                return Component.text("none");
                            }
                        }
                    } else {
                        return Component.text("none");
                    }
                case "team_colored":
                    if (PlayerManager.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManager.getInstance().getPlayer(player.getUuid()).get();
                        var game = gPlayer.getGame();
                        if (gPlayer.isSpectator) {
                            return Component.text("spectator", NamedTextColor.GRAY);
                        } else {
                            var team = game.getPlayerTeam(gPlayer);
                            if (team != null) {
                                return Component.text(team.getName(), NamedTextColor.NAMES.value(team.teamInfo.color.chatColor.name()));
                            } else {
                                return Component.text("none", NamedTextColor.RED);
                            }
                        }
                    } else {
                        return Component.text("none", NamedTextColor.RED);
                    }
                case "team_color":
                    if (PlayerManager.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManager.getInstance().getPlayer(player.getUuid()).get();
                        var game = gPlayer.getGame();
                        if (gPlayer.isSpectator) {
                            return Component.text("", NamedTextColor.GRAY);
                        } else {
                            var team = game.getPlayerTeam(gPlayer);
                            if (team != null) {
                                return Component.text("", NamedTextColor.NAMES.value(team.teamInfo.color.chatColor.name()));
                            } else {
                                return Component.text("", NamedTextColor.GRAY);
                            }
                        }
                    } else {
                        return Component.empty();
                    }
                case "team_players":
                    if (PlayerManager.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManager.getInstance().getPlayer(player.getUuid()).get();
                        var game = gPlayer.getGame();
                        if (gPlayer.isSpectator) {
                            return Component.text("0");
                        } else {
                            var team = game.getPlayerTeam(gPlayer);
                            if (team != null) {
                                return Component.text(team.countConnectedPlayers());
                            } else {
                                return Component.text("0");
                            }
                        }
                    } else {
                        return Component.text("0");
                    }
                case "team_maxplayers":
                    if (PlayerManager.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManager.getInstance().getPlayer(player.getUuid()).get();
                        var game = gPlayer.getGame();
                        if (gPlayer.isSpectator) {
                            return Component.text("0");
                        } else {
                            var team = game.getPlayerTeam(gPlayer);
                            if (team != null) {
                                return Component.text(team.getMaxPlayers());
                            } else {
                                return Component.text("0");
                            }
                        }
                    } else {
                        return Component.text("0");
                    }
                case "team_bed":
                    if (PlayerManager.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManager.getInstance().getPlayer(player.getUuid()).get();
                        var game = gPlayer.getGame();
                        if (gPlayer.isSpectator) {
                            return Component.text("no");
                        } else {
                            var team = game.getPlayerTeam(gPlayer);
                            if (team != null) {
                                return Component.text(team.isBed ? "yes" : "no");
                            } else {
                                return Component.text("no");
                            }
                        }
                    } else {
                        return Component.text("no");
                    }
                case "team_teamchests":
                    if (PlayerManager.getInstance().isPlayerInGame(player.getUuid())) {
                        var gPlayer = PlayerManager.getInstance().getPlayer(player.getUuid()).get();
                        var game = gPlayer.getGame();
                        if (gPlayer.isSpectator) {
                            return Component.text("0");
                        } else {
                            var team = game.getPlayerTeam(gPlayer);
                            if (team != null) {
                                return Component.text(team.countTeamChests());
                            } else {
                                return Component.text("0");
                            }
                        }
                    } else {
                        return Component.text("0");
                    }
            }
        }

        // Stats

        if (identifier.startsWith("stats_")) {
            if (!PlayerStatisticManager.isEnabled()) {
                return null;
            }

            var stats = PlayerStatisticManager.getInstance().getStatistic(PlayerMapper.wrapPlayer(player));

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
