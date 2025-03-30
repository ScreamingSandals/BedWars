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

package org.screamingsandals.bedwars.statistics;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.statistics.PlayerStatisticsManager;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.database.DatabaseManager;
import org.screamingsandals.bedwars.events.SavePlayerStatisticEventImpl;
import org.screamingsandals.bedwars.holograms.LeaderboardHolograms;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.player.OfflinePlayer;
import org.screamingsandals.lib.player.Players;
import org.screamingsandals.lib.event.player.PlayerLeaveEvent;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;
import org.screamingsandals.lib.utils.annotations.parameters.ConfigFile;
import org.screamingsandals.lib.utils.logger.Logger;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.sql.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlayerStatisticManager implements PlayerStatisticsManager {
    @ConfigFile("database/bw_stats_players.yml")
    private final YamlConfigurationLoader loader;
    private final MainConfig mainConfig;
    private final DatabaseManager databaseManager;
    private final Logger logger;

    private ConfigurationNode fileDatabase;
    private StatisticType statisticType;
    private final Map<UUID, PlayerStatisticImpl> playerStatistic = new HashMap<>();
    private final Map<UUID, Map.Entry<@Nullable String, Integer>> allScores = new HashMap<>();

    // level system
    @Getter
    private int neededXpToNextLevel = -1;
    @Getter
    private final Map<Integer, Integer> neededXpToSpecificLevels = new HashMap<>();
    @Getter
    private final Map<Predicate<Integer>, Integer> neededXpToLevelsBasedOnFormula = new HashMap<>();

    @ShouldRunControllable
    public static boolean isEnabled() {
        return MainConfig.getInstance().node("statistics", "enabled").getBoolean();
    }

    public static PlayerStatisticManager getInstance() {
        if (!isEnabled()) {
            throw new UnsupportedOperationException("PlayerStatisticManager is not enabled!");
        }
        return ServiceManager.get(PlayerStatisticManager.class);
    }

    public PlayerStatisticImpl getStatistic(OfflinePlayer player) {
        if (player == null) {
            return null;
        }

        return getStatistic(player.getUuid());
    }


    @Override
    public PlayerStatisticImpl getStatistic(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        if (!this.playerStatistic.containsKey(uuid)) {
            return this.loadStatistic(uuid);
        }

        return this.playerStatistic.get(uuid);
    }

    @OnEnable
    public void initialize() {
        if (!mainConfig.node("statistics", "enabled").getBoolean()) {
            return;
        }

        statisticType = 
                mainConfig.node("statistics", "type").getString("").equalsIgnoreCase("database") ? 
                        StatisticType.DATABASE : 
                        StatisticType.YAML;
        
        if (statisticType == StatisticType.DATABASE) {
            this.initializeDatabase();
        } else {
            this.loadYml();
        }

        this.initializeLeaderboard();

        neededXpToNextLevel = -1;
        neededXpToLevelsBasedOnFormula.clear();
        neededXpToSpecificLevels.clear();

        var xpToLevel = MainConfig.getInstance().node("statistics", "xp-to-level").childrenMap();
        for (var xpToL : xpToLevel.entrySet()) {
            var key = xpToL.getKey().toString().toLowerCase(Locale.ROOT).replace(" ", "");
            var value = xpToL.getValue().getInt();
            if (value <= 0) {
                continue; // invalid
            }
            try {
                if ("any".equalsIgnoreCase(key)) {
                    neededXpToNextLevel = value;
                } else if (key.contains("n")) { // formula
                    var split = key.split("n");
                    var multiplier = Integer.parseInt(split[0]);
                    var addition = split.length > 1 ? Integer.parseInt(split[1].replace("+", "")) : 0;
                    neededXpToLevelsBasedOnFormula.put(integer -> ((integer - addition) % multiplier) == 0, value);
                } else {
                    neededXpToSpecificLevels.put(Integer.parseInt(key), value);
                }
            } catch (Throwable ignored) {
            }
        }
    }

    @OnEvent
    public void onLeave(PlayerLeaveEvent event) {
        unloadStatistic(event.player());
    }

    public void initializeDatabase() {
        logger.info("Loading statistics from database...");

        try {
            databaseManager.initialize();

            try (var connection = databaseManager.getConnection()) {
                connection.setAutoCommit(false);
                final var preparedStatement = connection.prepareStatement(databaseManager.getCreateTableSql());
                preparedStatement.executeUpdate();
                connection.commit();
                preparedStatement.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initializeLeaderboard() {
        allScores.clear();

        if (statisticType == StatisticType.DATABASE) {
            try (var connection = databaseManager.getConnection()) {
                connection.setAutoCommit(false);
                final var preparedStatement = connection
                        .prepareStatement(databaseManager.getScoresSql(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.first()) {
                    do {
                        allScores.put(UUID.fromString(resultSet.getString("uuid")), new AbstractMap.SimpleEntry<>(resultSet.getString("name"), resultSet.getInt("score")));
                    } while (resultSet.next());
                }
                connection.commit();
                preparedStatement.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            fileDatabase.node("data").childrenMap().forEach((key, node) -> allScores.put(UUID.fromString(key.toString()), new AbstractMap.SimpleEntry<>(node.node("name").getString(), node.node("score").getInt())));
        }
    }

    public List<LeaderboardEntryImpl> getLeaderboard(int count) {
        return allScores.entrySet()
                .stream()
                .sorted((c1, c2) -> Comparator.<Integer>reverseOrder().compare(c1.getValue().getValue(), c2.getValue().getValue()))
                .limit(count)
                .map(entry -> new LeaderboardEntryImpl(Players.getOfflinePlayer(entry.getKey()), entry.getValue().getValue(), entry.getValue().getKey()))
                .collect(Collectors.toList());
    }

    private PlayerStatisticImpl loadDatabaseStatistic(UUID uuid) {
        if (this.playerStatistic.containsKey(uuid)) {
            return this.playerStatistic.get(uuid);
        }
        var deserialize = new HashMap<String, Object>();

        try (var connection = databaseManager.getConnection()) {
            var preparedStatement = connection
                    .prepareStatement(databaseManager.getReadObjectSql());
            preparedStatement.setString(1, uuid.toString());
            var resultSet = preparedStatement.executeQuery();

            var meta = resultSet.getMetaData();
            while (resultSet.next()) {
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    var key = meta.getColumnName(i);
                    var value = resultSet.getObject(key);
                    deserialize.put(key, value);
                }
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PlayerStatisticImpl playerStatistic;

        if (deserialize.isEmpty()) {
            playerStatistic = new PlayerStatisticImpl(uuid);
        } else {
            playerStatistic = new PlayerStatisticImpl(deserialize);
        }

        var player = Players.getPlayer(uuid);
        if (player != null) {
            playerStatistic.setName(player.getName());
        }

        this.playerStatistic.put(playerStatistic.getUuid(), playerStatistic);
        updateScore(playerStatistic);
        return playerStatistic;
    }

    public PlayerStatisticImpl loadStatistic(UUID uuid) {
        return (statisticType == StatisticType.DATABASE) ? loadDatabaseStatistic(uuid) : loadYamlStatistic(uuid);
    }

    private PlayerStatisticImpl loadYamlStatistic(UUID uuid) {
        if (this.fileDatabase == null || this.fileDatabase.node("data", uuid.toString()).empty()) {
            var playerStatistic = new PlayerStatisticImpl(uuid);
            this.playerStatistic.put(uuid, playerStatistic);
            return playerStatistic;
        }

        var node = this.fileDatabase.node("data", uuid.toString());
        var playerStatistic = new PlayerStatisticImpl(node);
        playerStatistic.setUuid(uuid);

        var player = Players.getPlayer(uuid);
        if (player != null) {
            playerStatistic.setName(player.getName());
        }

        this.playerStatistic.put(uuid, playerStatistic);
        updateScore(playerStatistic);
        return playerStatistic;
    }

    private void loadYml() {
        try {
            logger.info("Loading statistics from YAML-File ...");

            this.fileDatabase = loader.load();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void storeDatabaseStatistic(PlayerStatisticImpl playerStatistic) {
        try (var connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);

            var preparedStatement = connection
                    .prepareStatement(databaseManager.getWriteObjectSql());

            preparedStatement.setString(1, playerStatistic.getUuid().toString());
            preparedStatement.setString(2, playerStatistic.getName());
            preparedStatement.setInt(3, playerStatistic.getDeaths());
            preparedStatement.setInt(4, playerStatistic.getDestroyedBeds());
            preparedStatement.setInt(5, playerStatistic.getKills());
            preparedStatement.setInt(6, playerStatistic.getLoses());
            preparedStatement.setInt(7, playerStatistic.getScore());
            preparedStatement.setInt(8, playerStatistic.getWins());
            preparedStatement.executeUpdate();
            connection.commit();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void storeStatistic(PlayerStatisticImpl statistic) {
        var savePlayerStatisticEvent = new SavePlayerStatisticEventImpl(statistic);
        EventManager.fire(savePlayerStatisticEvent);

        if (savePlayerStatisticEvent.isCancelled()) {
            return;
        }

        if (statisticType == StatisticType.DATABASE) {
            this.storeDatabaseStatistic(statistic);
        } else {
            this.storeYamlStatistic(statistic);
        }
    }

    private synchronized void storeYamlStatistic(PlayerStatisticImpl statistic) {
        var node = this.fileDatabase.node("data", statistic.getUuid().toString());
        statistic.serializeTo(node);
        try {
            this.loader.save(this.fileDatabase);
        } catch (Exception ex) {
            logger.warn("Couldn't store statistic data for player with uuid: {}", statistic.getUuid().toString(), ex);
        }
    }

    public void unloadStatistic(OfflinePlayer player) {
        if (statisticType == StatisticType.DATABASE) {
            this.playerStatistic.remove(player.getUuid());
        }
    }

    public void updateScore(PlayerStatisticImpl playerStatistic) {
        allScores.put(playerStatistic.getUuid(), new AbstractMap.SimpleEntry<>(playerStatistic.getName(), playerStatistic.getScore()));
        if (LeaderboardHolograms.isEnabled()) {
            LeaderboardHolograms.getInstance().updateEntries();
        }
    }
    
    public enum StatisticType {
        DATABASE,
        YAML
    }
}
