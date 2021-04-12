package org.screamingsandals.bedwars.statistics;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.screamingsandals.bedwars.api.statistics.LeaderboardEntry;
import org.screamingsandals.bedwars.api.statistics.PlayerStatisticsManager;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.database.DatabaseManager;
import org.screamingsandals.bedwars.events.SavePlayerStatisticEventImpl;
import org.screamingsandals.bedwars.holograms.LeaderboardHolograms;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.player.OfflinePlayerWrapper;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.player.event.SPlayerLeaveEvent;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;
import org.screamingsandals.lib.utils.annotations.parameters.ConfigFile;
import org.screamingsandals.lib.utils.logger.LoggerWrapper;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

@Service(dependsOn = {
        MainConfig.class,
        DatabaseManager.class,
        PlayerMapper.class
})
@RequiredArgsConstructor
public class PlayerStatisticManager implements PlayerStatisticsManager<OfflinePlayerWrapper> {
    @ConfigFile("database/bw_stats_players.yml")
    private final YamlConfigurationLoader loader;
    private final MainConfig mainConfig;
    private final DatabaseManager databaseManager;
    private final LoggerWrapper logger;

    private ConfigurationNode fileDatabase;
    private StatisticType statisticType;
    private final Map<UUID, PlayerStatistic> playerStatistic = new HashMap<>();
    private final Map<UUID, Integer> allScores = new HashMap<>();

    @ShouldRunControllable
    public static boolean isEnabled() {
        return MainConfig.getInstance().node("statistics", "enabled").getBoolean();
    }

    public static PlayerStatisticManager getInstance() {
        if (!isEnabled()) {
            throw new UnsupportedOperationException("PlayerStatisticManager are not enabled!");
        }
        return ServiceManager.get(PlayerStatisticManager.class);
    }

    public PlayerStatistic getStatistic(OfflinePlayerWrapper player) {
        if (player == null) {
            return null;
        }

        if (!this.playerStatistic.containsKey(player.getUuid())) {
            return this.loadStatistic(player.getUuid());
        }

        return this.playerStatistic.get(player.getUuid());
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
    }

    @OnEvent
    public void onLeave(SPlayerLeaveEvent event) {
        unloadStatistic(event.getPlayer());
    }

    public void initializeDatabase() {
        logger.info("Loading statistics from database ...");

        try {
            databaseManager.initialize();

            try (Connection connection = databaseManager.getConnection()) {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection
                        .prepareStatement(databaseManager.getCreateTableSql());
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
            try (Connection connection = databaseManager.getConnection()) {
                connection.setAutoCommit(false);
                PreparedStatement preparedStatement = connection
                        .prepareStatement(databaseManager.getScoresSql());
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

    public List<LeaderboardEntry<OfflinePlayerWrapper>> getLeaderboard(int count) {
        return allScores.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(count)
                .map(entry -> new org.screamingsandals.bedwars.statistics.LeaderboardEntry(PlayerMapper.getOfflinePlayer(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
    }

    private PlayerStatistic loadDatabaseStatistic(UUID uuid) {
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

        PlayerStatistic playerStatistic;

        if (deserialize.isEmpty()) {
            playerStatistic = new PlayerStatistic(uuid);
        } else {
            playerStatistic = new PlayerStatistic(deserialize);
        }

        PlayerMapper
                .getPlayer(uuid)
                .map(PlayerWrapper::getName)
                .ifPresent(playerStatistic::setName);

        this.playerStatistic.put(playerStatistic.getId(), playerStatistic);
        this.allScores.put(uuid, playerStatistic.getScore());
        return playerStatistic;
    }

    public PlayerStatistic loadStatistic(UUID uuid) {
        if (statisticType == StatisticType.DATABASE) {
            return this.loadDatabaseStatistic(uuid);
        } else {
            return this.loadYamlStatistic(uuid);
        }
    }

    private PlayerStatistic loadYamlStatistic(UUID uuid) {
        if (this.fileDatabase == null || this.fileDatabase.node("data", uuid.toString()).empty()) {
            var playerStatistic = new PlayerStatistic(uuid);
            this.playerStatistic.put(uuid, playerStatistic);
            return playerStatistic;
        }

        var node = this.fileDatabase.node("data", uuid.toString());
        var playerStatistic = new PlayerStatistic(node);
        playerStatistic.setId(uuid);
        PlayerMapper
                .getPlayer(uuid)
                .map(PlayerWrapper::getName)
                .ifPresent(playerStatistic::setName);
        this.playerStatistic.put(uuid, playerStatistic);
        this.allScores.put(uuid, playerStatistic.getScore());
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

    private void storeDatabaseStatistic(PlayerStatistic playerStatistic) {
        try (var connection = databaseManager.getConnection()) {
            connection.setAutoCommit(false);

            var preparedStatement = connection
                    .prepareStatement(databaseManager.getWriteObjectSql());

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

    private synchronized void storeYamlStatistic(PlayerStatistic statistic) {
        var node = this.fileDatabase.node("data", statistic.getId().toString());
        statistic.serializeTo(node);
        try {
            this.loader.save(this.fileDatabase);
        } catch (Exception ex) {
            logger.warn("Couldn't store statistic data for player with uuid: {}", statistic.getId().toString());
        }
    }

    public void unloadStatistic(OfflinePlayerWrapper player) {
        if (statisticType == StatisticType.DATABASE) {
            this.playerStatistic.remove(player.getUuid());
        }
    }

    public void updateScore(PlayerStatistic playerStatistic) {
        allScores.put(playerStatistic.getId(), playerStatistic.getScore());
        if (LeaderboardHolograms.isEnabled()) {
            LeaderboardHolograms.getInstance().updateEntries();
        }
    }
    
    public enum StatisticType {
        DATABASE,
        YAML
    }
}
