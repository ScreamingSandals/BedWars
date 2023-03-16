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
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatistic implements ConfigurationSerializable, org.screamingsandals.bedwars.api.statistics.PlayerStatistic {
    private UUID uuid;
    private String name = "";
    private int deaths;
    private int destroyedBeds;
    private int kills;
    private int loses;
    private int score;
    private int wins;

    public PlayerStatistic(UUID uuid) {
        this.uuid = uuid;

        Player player = Bukkit.getPlayer(uuid);
        if (player != null && !this.name.equals(player.getName())) {
            this.name = player.getName();
        }
    }

    public PlayerStatistic(Map<String, Object> deserialize) {
        if (deserialize.containsKey("deaths")) {
            this.deaths = (int) deserialize.get("deaths");
        }
        if (deserialize.containsKey("destroyedBeds")) {
            this.destroyedBeds = (int) deserialize.get("destroyedBeds");
        }
        if (deserialize.containsKey("kills")) {
            this.kills = (int) deserialize.get("kills");
        }
        if (deserialize.containsKey("loses")) {
            this.loses = (int) deserialize.get("loses");
        }
        if (deserialize.containsKey("score")) {
            this.score = (int) deserialize.get("score");
        }
        if (deserialize.containsKey("wins")) {
            this.wins = (int) deserialize.get("wins");
        }
        if (deserialize.containsKey("name")) {
            this.name = (String) deserialize.get("name");
        }
        if (deserialize.containsKey("uuid")) {
            this.uuid = UUID.fromString((String) deserialize.get("uuid"));
        }
    }

    public int getGames() {
        return this.getWins() + this.getLoses();
    }

    @Override
    public void addDeaths(int deaths) {
        this.deaths += deaths;
    }

    @Override
    public void addDestroyedBeds(int destroyedBeds) {
        this.destroyedBeds += destroyedBeds;
    }

    @Override
    public void addKills(int kills) {
        this.kills += kills;
    }

    @Override
    public void addLoses(int loses) {
        this.loses += loses;
    }

    @Override
    public void addScore(int score) {
        this.score += score;
        Main.getPlayerStatisticsManager().updateScore(this);
    }

    @Override
    public void addWins(int wins) {
        this.wins += wins;
    }

    public UUID getId() {
        return this.uuid;
    }

    public void setId(UUID uuid) {
        this.uuid = uuid;
    }

    public double getKD() {
        double kd = 0.0;
        if (deaths == 0) {
            kd = kills;
        } else if (kills != 0) {
            kd = ((double) kills) / ((double) deaths);
        }
        kd = Math.round(kd * 100.0) / 100.0;

        return kd;
    }

    @Override
    public Map<String, Object> serialize() {
        HashMap<String, Object> playerStatistic = new HashMap<>();
        playerStatistic.put("deaths", this.deaths);
        playerStatistic.put("destroyedBeds", this.destroyedBeds);
        playerStatistic.put("kills", this.kills);
        playerStatistic.put("loses", this.loses);
        playerStatistic.put("score", this.score);
        playerStatistic.put("wins", this.wins);
        playerStatistic.put("name", this.name);
        return playerStatistic;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getDestroyedBeds() {
        return destroyedBeds;
    }

    public int getKills() {
        return kills;
    }

    public int getLoses() {
        return loses;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getWins() {
        return wins;
    }

    public void setName(String name) {
        this.name = name;
    }

}
