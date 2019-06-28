package misat11.bw.statistics;

// From BedwarsRel

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import misat11.bw.Main;
import misat11.bw.api.events.BedwarsSavePlayerStatisticEvent;

public class PlayerStatisticManager {

	private File databaseFile = null;
	private FileConfiguration fileDatabase = null;
	private Map<UUID, PlayerStatistic> playerStatistic = null;

	public PlayerStatisticManager() {
		this.playerStatistic = new HashMap<>();
		this.fileDatabase = null;
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
		if (!Main.getConfigurator().config.getBoolean("statistics.enabled", false)) {
			return;
		}

		if (Main.getConfigurator().config.getString("statistics.type").equalsIgnoreCase("database")) {
			this.initializeDatabase();
		} else {
			File file = new File(Main.getInstance().getDataFolder() + "/database/bw_stats_players.yml");
			this.loadYml(file);
		}
	}

	public void initializeDatabase() {
		Main.getInstance().getLogger().info("Loading statistics from database ...");

		try {
			Main.getDatabaseManager().initialize();
			
			Connection connection = Main.getDatabaseManager().getConnection();
			connection.setAutoCommit(false);
			PreparedStatement preparedStatement = connection
					.prepareStatement(Main.getDatabaseManager().getCreateTableSql());
			preparedStatement.executeUpdate();
			connection.commit();
			preparedStatement.close();
			connection.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private PlayerStatistic loadDatabaseStatistic(UUID uuid) {
		if (this.playerStatistic.containsKey(uuid)) {
			return this.playerStatistic.get(uuid);
		}
		HashMap<String, Object> deserialize = new HashMap<>();

		try {
			Connection connection = Main.getDatabaseManager().getConnection();
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
			connection.close();
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

		HashMap<String, Object> deserialize = new HashMap<>();
		deserialize.putAll(this.fileDatabase.getConfigurationSection("data." + uuid.toString()).getValues(false));
		PlayerStatistic playerStatistic = new PlayerStatistic(deserialize);
		playerStatistic.setId(uuid);
		Player player = Main.getInstance().getServer().getPlayer(uuid);
		if (player != null && !playerStatistic.getName().equals(player.getName())) {
			playerStatistic.setName(player.getName());
		}
		this.playerStatistic.put(uuid, playerStatistic);
		return playerStatistic;
	}

	private void loadYml(File ymlFile) {
		try {
			Main.getInstance().getLogger().info("Loading statistics from YAML-File ...");

			YamlConfiguration config = null;

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
		try {
			Connection connection = Main.getDatabaseManager().getConnection();
			connection.setAutoCommit(false);

			PreparedStatement preparedStatement = connection
					.prepareStatement(Main.getDatabaseManager().getWriteObjectSql());

			preparedStatement.setString(1, playerStatistic.getId().toString());
			preparedStatement.setString(2, playerStatistic.getName());
			preparedStatement.setInt(3, playerStatistic.getCurrentDeaths());
			preparedStatement.setInt(4, playerStatistic.getCurrentDestroyedBeds());
			preparedStatement.setInt(5, playerStatistic.getCurrentKills());
			preparedStatement.setInt(6, playerStatistic.getCurrentLoses());
			preparedStatement.setInt(7, playerStatistic.getCurrentScore());
			preparedStatement.setInt(8, playerStatistic.getCurrentWins());
			preparedStatement.executeUpdate();
			connection.commit();
			preparedStatement.close();
			connection.close();
			playerStatistic.addCurrentValues();
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
		statistic.addCurrentValues();
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
}
