package misat11.bw.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimeZone;

public class DatabaseManager {

	private String tablePrefix = "bw_";
	private String database = null;
	private HikariDataSource dataSource = null;
	private String host = null;
	private String password = null;
	private int port = 3306;
	private String user = null;

	public DatabaseManager(String host, int port, String user, String password, String database, String tablePrefix) {
		this.host = host;
		this.port = port;
		this.user = user;
		this.password = password;
		this.database = database;
		this.tablePrefix = tablePrefix;
	}

	public void initialize() {
		HikariConfig config = new HikariConfig();
		config.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database
				+ "?autoReconnect=true&serverTimezone=" + TimeZone.getDefault().getID());
		config.setUsername(this.user);
		config.setPassword(this.password);
		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		this.dataSource = new HikariDataSource(config);
	}

	public Connection getConnection() {
		try {
			return this.dataSource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getCreateTableSql() {
		return "CREATE TABLE IF NOT EXISTS `" + tablePrefix
				+ "stats_players` (`kills` int(11) NOT NULL DEFAULT '0', `wins` int(11) NOT NULL DEFAULT '0', `score` int(11) NOT NULL DEFAULT '0', `loses` int(11) NOT NULL DEFAULT '0', `name` varchar(255) NOT NULL, `destroyedBeds` int(11) NOT NULL DEFAULT '0', `uuid` varchar(255) NOT NULL, `deaths` int(11) NOT NULL DEFAULT '0', PRIMARY KEY (`uuid`))";
	}

	public String getReadObjectSql() {
		return "SELECT * FROM " + tablePrefix + "stats_players WHERE uuid = ? LIMIT 1";
	}

	public String getWriteObjectSql() {
		return "INSERT INTO " + tablePrefix
				+ "stats_players(uuid, name, deaths, destroyedBeds, kills, loses, score, wins) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE uuid=VALUES(uuid),name=VALUES(name),deaths=deaths+VALUES(deaths),destroyedBeds=destroyedBeds+VALUES(destroyedBeds),kills=kills+VALUES(kills),loses=loses+VALUES(loses),score=score+VALUES(score),wins=wins+VALUES(wins)";
	}

	public String getTablePrefix() {
		return tablePrefix;
	}
}