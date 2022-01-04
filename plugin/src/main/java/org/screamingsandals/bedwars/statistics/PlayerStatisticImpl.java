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

package org.screamingsandals.bedwars.statistics;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;
import org.screamingsandals.lib.player.PlayerMapper;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@ToString
public class PlayerStatisticImpl implements PlayerStatistic {
    @Setter
    private UUID uuid;
    @Setter
    private String name = "";
    private int deaths;
    private int destroyedBeds;
    private int kills;
    private int loses;
    private int score;
    private int wins;

    public PlayerStatisticImpl(UUID uuid) {
        this.uuid = uuid;

        PlayerMapper.getPlayer(uuid)
                .ifPresent(playerWrapper -> this.name = playerWrapper.getName());
    }

    public PlayerStatisticImpl(Map<String, Object> deserialize) {
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

    public PlayerStatisticImpl(ConfigurationNode configurationNode) {
        this.deaths = configurationNode.node("deaths").getInt();
        this.destroyedBeds = configurationNode.node("destroyedBeds").getInt();
        this.kills = configurationNode.node("kills").getInt();
        this.loses = configurationNode.node("loses").getInt();
        this.score = configurationNode.node("score").getInt();
        this.wins = configurationNode.node("wins").getInt();
        this.name = configurationNode.node("name").getString();
        this.uuid = UUID.fromString(Objects.requireNonNull(configurationNode.key()).toString());
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
        PlayerStatisticManager.getInstance().updateScore(this);
    }

    @Override
    public void addWins(int wins) {
        this.wins += wins;
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

    public void serializeTo(ConfigurationNode playerStatistic) {
        try {
            playerStatistic.node("deaths").set(this.deaths);
            playerStatistic.node("destroyedBeds").set(this.destroyedBeds);
            playerStatistic.node("kills").set(this.kills);
            playerStatistic.node("loses").set(this.loses);
            playerStatistic.node("score").set(this.score);
            playerStatistic.node("wins").set(this.wins);
            playerStatistic.node("name").set(this.name);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }
}
