/*
 * Copyright (C) 2022 ScreamingSandals
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

package org.screamingsandals.bedwars.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.TimeZone;

public class DatabaseManager {
    private String tablePrefix;
    private String database;
    private HikariDataSource dataSource = null;
    private String host;
    private String password;
    private int port;
    private String user;
    private boolean useSSL;

    public DatabaseManager(String host, int port, String user, String password, String database, String tablePrefix, boolean useSSL) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;
        this.tablePrefix = tablePrefix;
        this.useSSL = useSSL;
    }

    public void initialize() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database
                + "?autoReconnect=true&serverTimezone=" + TimeZone.getDefault().getID() + "&useSSL=" + useSSL);
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
                + "stats_players(uuid, name, deaths, destroyedBeds, kills, loses, score, wins) VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE uuid=VALUES(uuid),name=VALUES(name),deaths=VALUES(deaths),destroyedBeds=VALUES(destroyedBeds),kills=VALUES(kills),loses=VALUES(loses),score=VALUES(score),wins=VALUES(wins)";
    }

    public String getScoresSql() {
        return "SELECT uuid, score, name FROM " + tablePrefix + "stats_players";
    }

    public String getTablePrefix() {
        return tablePrefix;
    }
}