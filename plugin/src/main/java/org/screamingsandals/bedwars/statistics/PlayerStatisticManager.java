package org.screamingsandals.bedwars.statistics;

import org.bukkit.Bukkit;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.events.BedwarsSavePlayerStatisticEvent;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.api.statistics.LeaderboardEntry;
import org.screamingsandals.bedwars.api.statistics.PlayerStatisticsManager;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.holograms.LeaderboardHolograms;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.sql.*;
import java.util.*;

public class PlayerStatisticManager implements PlayerStatisticsManager {
    private YamlConfigurationLoader loader;
    private File databaseFile = null;
    private ConfigurationNode fileDatabase;
    private Map<UUID, PlayerStatistic> playerStatistic;
    private Map<UUID, Integer> allScores = new HashMap<>();

    public PlayerStatisticManager() {
        this.playerStatistic = new HashMap<>();
    }

    public PlayerStatistic getStatistic(OfflinePlayer player) {
        if (player == null) {
            return null;
        }

        if (!this.playerStatistic.containsKey(player.getUniqueId())) {
            return this.loadStatistic(player.getUniqueId());
        }

        return this.playerStatistic.get(player.getUniqueId());
    }

    public void initialize() {
        if (!MainConfig.getInstance().node("statistics", "enabled").getBoolean()) {
            return;
        }

        if (MainConfig.getInstance().node("statistics", "type").getString("").equalsIgnoreCase("database")) {
            this.initializeDatabase();
        } else {
            var file = Main.getInstance().getPluginDescription().getDataFolder().resolve("database").resolve("bw_stats_players.yml").toFile();
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

        if (MainConfig.getInstance().node("statistics", "type").getString("").equalsIgnoreCase("database")) {
            try (Connection connection = Main.getDatabaseManager().getConnection()) {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection
                        .prepareStatement(Main.getDatabaseManager().getScoresSql());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.first()) {
                    do {
                        allScores.put(UUID.fromString(resultSet.getString("uuid")), resultSet.getInt("score"));
                    } while (resultSet.next());
                }
                connection.commit();
                preparedStatement.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            fileDatabase.node("data").childrenMap().forEach((key, node) -> {
                allScores.put(UUID.fromString(key.toString()), node.node("score").getInt());
            });
        }
    }

    public List<LeaderboardEntry> getLeaderboard(int count) {
        List<LeaderboardEntry> entries = new ArrayList<>();

        allScores.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(count)
                .forEach(entry -> entries.add(new org.screamingsandals.bedwars.statistics.LeaderboardEntry(Bukkit.getOfflinePlayer(entry.getKey()), entry.getValue())));

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
        Player player = Bukkit.getServer().getPlayer(uuid);
        if (player != null && !playerStatistic.getName().equals(player.getName())) {
            playerStatistic.setName(player.getName());
        }
        allScores.put(uuid, playerStatistic.getScore());

        this.playerStatistic.put(playerStatistic.getId(), playerStatistic);
        return playerStatistic;
    }

    public PlayerStatistic loadStatistic(UUID uuid) {
        if (MainConfig.getInstance().node("statistics", "type").getString("").equalsIgnoreCase("database")) {
            return this.loadDatabaseStatistic(uuid);
        } else {
            return this.loadYamlStatistic(uuid);
        }
    }

    private PlayerStatistic loadYamlStatistic(UUID uuid) {

        if (this.fileDatabase == null || this.fileDatabase.node("data", uuid.toString()).empty()) {
            PlayerStatistic playerStatistic = new PlayerStatistic(uuid);
            this.playerStatistic.put(uuid, playerStatistic);
            return playerStatistic;
        }

        var node = this.fileDatabase.node("data", uuid.toString());
        var playerStatistic = new PlayerStatistic(node);
        playerStatistic.setId(uuid);
        var player = Bukkit.getServer().getPlayer(uuid);
        if (player != null && !playerStatistic.getName().equals(player.getName())) {
            playerStatistic.setName(player.getName());
        }
        this.playerStatistic.put(uuid, playerStatistic);
        allScores.put(uuid, playerStatistic.getScore());
        return playerStatistic;
    }

    private void loadYml(File ymlFile) {
        try {
            Main.getInstance().getLogger().info("Loading statistics from YAML-File ...");

            loader = YamlConfigurationLoader.builder()
                    .file(ymlFile)
                    .build();

            this.databaseFile = ymlFile;

            if (!ymlFile.exists()) {
                ymlFile.getParentFile().mkdirs();
                ymlFile.createNewFile();

                this.fileDatabase = loader.createNode();
            } else {
                this.fileDatabase = loader.load();
            }

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
        Bukkit.getServer().getPluginManager().callEvent(savePlayerStatisticEvent);

        if (savePlayerStatisticEvent.isCancelled()) {
            return;
        }

        if (MainConfig.getInstance().node("statistics", "type").getString("").equalsIgnoreCase("database")) {
            this.storeDatabaseStatistic(statistic);
        } else {
            this.storeYamlStatistic(statistic);
        }
    }

    private synchronized void storeYamlStatistic(PlayerStatistic statistic) {
        var node = this.fileDatabase.node("data", statistic.getId().toString());
        statistic.serializeTo(node);
        try {
            this.loader.save(this.fileDatabase);
        } catch (Exception ex) {
            Main.getInstance().getLogger().warn("Couldn't store statistic data for player with uuid: " + statistic.getId().toString());
        }
    }

    public void unloadStatistic(OfflinePlayer player) {
        if (MainConfig.getInstance().node("statistics", "type").getString("").equalsIgnoreCase("database")) {
            this.playerStatistic.remove(player.getUniqueId());
        }
    }

    public void updateScore(PlayerStatistic playerStatistic) {
        allScores.put(playerStatistic.getId(), playerStatistic.getScore());
        if (LeaderboardHolograms.isEnabled()) {
            LeaderboardHolograms.getInstance().updateEntries();
        }
    }
}
