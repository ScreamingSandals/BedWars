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

package org.screamingsandals.bedwars.config;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.ServiceDependencies;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.parameters.ConfigFile;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@ServiceDependencies(dependsOn = {
        MainConfig.class
})
public class RecordSave {
    @ConfigFile(value = "database/record.yml", old = "record.yml")
    private final YamlConfigurationLoader loader;
    private ConfigurationNode records;

    public static RecordSave getInstance() {
        return ServiceManager.get(RecordSave.class);
    }

    @OnPostEnable
    public void load() {
        try {
            records = loader.load();
        } catch (ConfigurateException e) {
            e.printStackTrace();
            records = loader.createNode();
        }
    }

    public void saveRecord(Record record) {
        try {
            var recordNode = records.node("record", record.getGame());
            recordNode.node("time").set(record.getTime());
            recordNode.node("team").set(record.getTeam());
            recordNode.node("winners").set(record.getWinners());

            loader.save(records);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    public Optional<Record> getRecord(String game) {
        var recordNode = records.node("record", game);
        if (recordNode.isMap()) {
            return Optional.of(Record.builder()
                    .game(game)
                    .time(recordNode.node("time").getInt())
                    .team(recordNode.node("team").getString())
                    .winners(recordNode.node("winners").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList()))
                    .build());
        }
        return Optional.empty();
    }

    @Data
    @Builder
    public static class Record {
        private final String game;
        private final int time;
        private final String team;
        private final List<String> winners;
    }
}
