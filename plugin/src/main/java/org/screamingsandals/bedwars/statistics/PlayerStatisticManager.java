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

package org.screamingsandals.bedwars.statistics;

import org.bukkit.Bukkit;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.events.BedwarsSavePlayerStatisticEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.api.statistics.LeaderboardEntry;
import org.screamingsandals.bedwars.api.statistics.PlayerStatisticsManager;

import java.io.File;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerStatisticManager implements PlayerStatisticsManager {
    private File databaseFile = null;
    private FileConfiguration fileDatabase;
    private Map<UUID, PlayerStatistic> playerStatistic;
    private Map<UUID, Map.Entry<String, Integer>> allScores = new HashMap<>();

    public PlayerStatisticManager() {
        this.playerStatistic = new HashMap<>();
        this.fileDatabase = null;
    }

    public PlayerStatistic getStatistic(OfflinePlayer player) {
        if (player == null) {
            return null;
        }

        return getStatistic(player.getUniqueId());
    }

    public PlayerStatistic getStatistic(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        if (!this.playerStatistic.containsKey(uuid)) {
            return this.loadStatistic(uuid);
        }

        return this.playerStatistic.get(uuid);
    }

    public void initialize() {
        if (!Main.getConfigurator().config.getBoolean("statistics.enabled", false)) {
            return;
        }

        if (Main.getConfigurator().config.getString("statistics.type").equalsIgnoreCase("database")) {
            this.initializeDatabase();
        } else {
            File file = new File(Main.getInstance().getDataFolder() + "/database/bw_stats_players.yml");
            this.loadYml(file);
        }

        this.initializeLeaderboard();
    }

    public void initializeDatabase() {
        Main.getInstance().getLogger().info("Loading statistics from database ...");

        try {
            Main.getDatabaseManager().initialize();

            try (Connection connection = Main.getDatabaseManager().getConnection()) {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection
                        .prepareStatement(Main.getDatabaseManager().getCreateTableSql());
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

        if (Main.getConfigurator().config.getString("statistics.type").equalsIgnoreCase("database")) {
            try (Connection connection = Main.getDatabaseManager().getConnection()) {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection
                        .prepareStatement(Main.getDatabaseManager().getScoresSql(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
            for (String key : fileDatabase.getConfigurationSection("data").getKeys(false)) {
                allScores.put(UUID.fromString(key), new AbstractMap.SimpleEntry<>(fileDatabase.getString("data." + key + ".name"), fileDatabase.getInt("data." + key + ".score")));
            }
        }
    }

    public List<LeaderboardEntry> getLeaderboard(int count) {
        List<LeaderboardEntry> entries = new ArrayList<>();

        allScores.entrySet().stream()
                .sorted((c1, c2) -> Comparator.<Integer>reverseOrder().compare(c1.getValue().getValue(), c2.getValue().getValue()))
                .limit(count)
                .forEach(entry -> entries.add(new org.screamingsandals.bedwars.statistics.LeaderboardEntry(Bukkit.getOfflinePlayer(entry.getKey()), entry.getValue().getValue(), entry.getValue().getKey())));

        return entries;
    }

    private PlayerStatistic loadDatabaseStatistic(UUID uuid) {
        if (this.playerStatistic.containsKey(uuid)) {
            return this.playerStatistic.get(uuid);
        }
        HashMap<String, Object> deserialize = new HashMap<>();

        try (Connection connection = Main.getDatabaseManager().getConnection()) {
            PreparedStatement preparedStatement = connection
                    .prepareStatement(Main.getDatabaseManager().getReadObjectSql());
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData meta = resultSet.getMetaData();
            while (resultSet.next()) {
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String key = meta.getColumnName(i);
                    Object value = resultSet.getObject(key);
                    deserialize.put(key, value);
                }
            }

            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PlayerStatistic playerStatistic;

        if (deserialize.isEmpty()) {
            playerStatistic = new PlayerStatistic(uuid);
        } else {
            playerStatistic = new PlayerStatistic(deserialize);
        }
        Player player = Main.getInstance().getServer().getPlayer(uuid);
        if (player != null && !playerStatistic.getName().equals(player.getName())) {
            playerStatistic.setName(player.getName());
        }
        allScores.put(uuid, new AbstractMap.SimpleEntry<>(playerStatistic.getName(), playerStatistic.getScore()));

        this.playerStatistic.put(playerStatistic.getId(), playerStatistic);
        return playerStatistic;
    }

    public PlayerStatistic loadStatistic(UUID uuid) {
        if (Main.getConfigurator().config.getString("statistics.type").equalsIgnoreCase("database")) {
            return this.loadDatabaseStatistic(uuid);
        } else {
            return this.loadYamlStatistic(uuid);
        }
    }

    private PlayerStatistic loadYamlStatistic(UUID uuid) {

        if (this.fileDatabase == null || !this.fileDatabase.contains("data." + uuid.toString())) {
            PlayerStatistic playerStatistic = new PlayerStatistic(uuid);
            this.playerStatistic.put(uuid, playerStatistic);
            return playerStatistic;
        }

        if (!this.fileDatabase.isConfigurationSection("data." + uuid.toString())) {
            Main.getInstance().getLogger().warning("Statistics of player with UUID " + uuid + " are not properly saved and the plugin cannot load them!");
            PlayerStatistic playerStatistic = new PlayerStatistic(uuid);
            this.playerStatistic.put(uuid, playerStatistic);
            return playerStatistic;
        }

        HashMap<String, Object> deserialize = new HashMap<>();
        deserialize.putAll(this.fileDatabase.getConfigurationSection("data." + uuid.toString()).getValues(false));
        PlayerStatistic playerStatistic = new PlayerStatistic(deserialize);
        playerStatistic.setId(uuid);
        Player player = Main.getInstance().getServer().getPlayer(uuid);
        if (player != null && !playerStatistic.getName().equals(player.getName())) {
            playerStatistic.setName(player.getName());
        }
        this.playerStatistic.put(uuid, playerStatistic);
        updateScore(playerStatistic);
        return playerStatistic;
    }

    private void loadYml(File ymlFile) {
        try {
            Main.getInstance().getLogger().info("Loading statistics from YAML-File ...");

            YamlConfiguration config;

            this.databaseFile = ymlFile;

            if (!ymlFile.exists()) {
                ymlFile.getParentFile().mkdirs();
                ymlFile.createNewFile();

                config = new YamlConfiguration();
                config.createSection("data");
                config.save(ymlFile);
            } else {
                config = YamlConfiguration.loadConfiguration(ymlFile);
            }

            this.fileDatabase = config;

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void storeDatabaseStatistic(PlayerStatistic playerStatistic) {
        try (Connection connection = Main.getDatabaseManager().getConnection()) {
            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection
                    .prepareStatement(Main.getDatabaseManager().getWriteObjectSql());

            preparedStatement.setString(1, playerStatistic.getId().toString());
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

    public void storeStatistic(PlayerStatistic statistic) {
        BedwarsSavePlayerStatisticEvent savePlayerStatisticEvent = new BedwarsSavePlayerStatisticEvent(statistic);
        Main.getInstance().getServer().getPluginManager().callEvent(savePlayerStatisticEvent);

        if (savePlayerStatisticEvent.isCancelled()) {
            return;
        }

        if (Main.getConfigurator().config.getString("statistics.type").equalsIgnoreCase("database")) {
            this.storeDatabaseStatistic(statistic);
        } else {
            this.storeYamlStatistic(statistic);
        }
    }

    private synchronized void storeYamlStatistic(PlayerStatistic statistic) {
        this.fileDatabase.set("data." + statistic.getId().toString(), statistic.serialize());
        try {
            this.fileDatabase.save(this.databaseFile);
        } catch (Exception ex) {
            Main.getInstance().getLogger().warning("Couldn't store statistic data for player with uuid: " + statistic.getId().toString());
        }
    }

    public void unloadStatistic(OfflinePlayer player) {
        if (Main.getConfigurator().config.getString("statistics.type").equalsIgnoreCase("database")) {
            this.playerStatistic.remove(player.getUniqueId());
        }
    }

    public void updateScore(PlayerStatistic playerStatistic) {
        allScores.put(playerStatistic.getId(), new AbstractMap.SimpleEntry<>(playerStatistic.getName(), playerStatistic.getScore()));
        if (Main.getLeaderboardHolograms() != null) {
            Main.getLeaderboardHolograms().updateEntries();
        }
    }
}
