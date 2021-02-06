package org.screamingsandals.bedwars.config;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class RecordSave {
    private final YamlConfigurationLoader loader;
    private ConfigurationNode records;
    private boolean modified;

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
