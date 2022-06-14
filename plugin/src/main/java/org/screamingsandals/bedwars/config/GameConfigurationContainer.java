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

package org.screamingsandals.bedwars.config;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.config.Configuration;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.config.ConfigurationKey;
import org.screamingsandals.bedwars.api.config.ConfigurationListKey;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GameConfigurationContainer implements ConfigurationContainer {

    @Nullable
    @Getter
    @Setter
    private ConfigurationContainer parentContainer;
    private final Map<List<String>, Class<?>> registered = new HashMap<>();
    private final Map<List<String>, Class<?>> registeredList = new HashMap<>();
    @Getter
    private final BasicConfigurationNode saved = BasicConfigurationNode.root();
    private final Map<List<String>, String[]> globalConfigKeys = new HashMap<>();

    {
        register(ConfigurationContainer.TEAM_JOIN_ITEM_ENABLED, "team-join-item-enabled");
        register(ConfigurationContainer.JOIN_RANDOM_TEAM_AFTER_LOBBY, "join-random-team-after-lobby");
        register(ConfigurationContainer.JOIN_RANDOM_TEAM_ON_JOIN, "join-random-team-on-join");
        register(ConfigurationContainer.ADD_WOOL_TO_INVENTORY_ON_JOIN, "add-wool-to-inventory-on-join");
        register(ConfigurationContainer.PREVENT_KILLING_VILLAGERS, "prevent-killing-villagers");
        register(ConfigurationContainer.PLAYER_DROPS, "player-drops");
        register(ConfigurationContainer.FRIENDLYFIRE, "friendlyfire");
        register(ConfigurationContainer.COLORED_LEATHER_BY_TEAM_IN_LOBBY, "in-lobby-colored-leather-by-team");
        register(ConfigurationContainer.KEEP_INVENTORY_ON_DEATH, "keep-inventory-on-death");
        register(ConfigurationContainer.KEEP_ARMOR_ON_DEATH, "keep-armor-on-death");
        register(ConfigurationContainer.ALLOW_CRAFTING, "allow-crafting");
        register(ConfigurationContainer.BOSSBAR_LOBBY_ENABLED,  "bossbar", "lobby", "enabled");
        register(ConfigurationContainer.BOSSBAR_GAME_ENABLED,  "bossbar", "game", "enabled");
        register(ConfigurationContainer.SIDEBAR_DATE_FORMAT, "sidebar", "date-format");
        register(ConfigurationContainer.SIDEBAR_GAME_ENABLED, "sidebar", "game", "enabled");
        register(ConfigurationContainer.SIDEBAR_GAME_LEGACY_SIDEBAR, "sidebar", "game", "legacy-sidebar");
        register(ConfigurationContainer.SIDEBAR_GAME_TITLE, "sidebar", "game", "title");
        register(ConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_TARGET_BLOCK_LOST, "sidebar", "game", "team-prefixes", "target-block-lost");
        register(ConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_ANCHOR_EMPTY, "sidebar", "game", "team-prefixes", "anchor-empty");
        register(ConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_TARGET_BLOCK_EXISTS, "sidebar", "game", "team-prefixes", "target-block-exists");
        register(ConfigurationContainer.SIDEBAR_GAME_TEAM_PREFIXES_TEAM_COUNT, "sidebar", "game", "team-prefixes", "team-count");
        register(ConfigurationContainer.SIDEBAR_GAME_TEAM_LINE, "sidebar", "game", "team-line");
        register(ConfigurationContainer.SIDEBAR_GAME_CONTENT, "sidebar", "game", "content");
        register(ConfigurationContainer.SIDEBAR_LOBBY_ENABLED,  "sidebar", "lobby", "enabled");
        register(ConfigurationContainer.SIDEBAR_LOBBY_TITLE, "sidebar", "lobby", "title");
        register(ConfigurationContainer.SIDEBAR_LOBBY_CONTENT, "sidebar", "lobby", "content");
        register(ConfigurationContainer.PREVENT_SPAWNING_MOBS, "prevent-spawning-mobs");
        register(ConfigurationContainer.SPAWNER_HOLOGRAMS, "spawner-holograms");
        register(ConfigurationContainer.SPAWNER_DISABLE_MERGE, "spawner-disable-merge");
        register(ConfigurationContainer.GAME_START_ITEMS_ENABLED, "game-start-items", "enabled");
        register(ConfigurationContainer.PLAYER_RESPAWN_ITEMS_ENABLED, "player-respawn-items", "enabled");
        register(ConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM,  "spawner-holograms-countdown");
        register(ConfigurationContainer.DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA, "damage-when-player-is-not-in-arena");
        register(ConfigurationContainer.REMOVE_UNUSED_TARGET_BLOCKS, "remove-unused-target-blocks");
        register(ConfigurationContainer.ALLOW_BLOCK_FALLING, "allow-block-falling");
        register(ConfigurationContainer.HOLOGRAMS_ABOVE_BEDS, "holograms-above-bed");
        register(ConfigurationContainer.ALLOW_SPECTATOR_JOIN, "allow-spectator-join");
        register(ConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE, "stop-team-spawners-on-die");
        register(ConfigurationContainer.TARGET_BLOCK_RESPAWN_ANCHOR_FILL_ON_START, "target-block", "respawn-anchor", "fill-on-start");
        register(ConfigurationContainer.TARGET_BLOCK_RESPAWN_ANCHOR_ENABLE_DECREASE, "target-block", "respawn-anchor", "enable-decrease");
        register(ConfigurationContainer.TARGET_BLOCK_CAKE_DESTROY_BY_EATING, "target-block", "cake", "destroy-by-eating");
        register(ConfigurationContainer.TARGET_BLOCK_ALLOW_DESTROYING_WITH_EXPLOSIONS, "target-block", "allow-destroying-with-explosions");
        register(ConfigurationContainer.INVISIBLE_LOBBY_ON_GAME_START, "invisible-lobby-on-game-start");
        register(ConfigurationContainer.ENABLE_BELOW_NAME_HEALTH_INDICATOR, "enable-below-name-health-indicator");
        register(ConfigurationContainer.USE_CERTAIN_POPULAR_SERVER_LIKE_HOLOGRAMS_FOR_SPAWNERS, "use-certain-popular-server-like-holograms-for-spawners");
        register(ConfigurationContainer.USE_TEAM_LETTER_PREFIXES_BEFORE_PLAYER_NAMES, "use-team-letter-prefixes-before-player-names");
        register(ConfigurationContainer.DEFAULT_SHOP_FILE);
    }

    @Override
    public <T> Optional<Configuration<T>> get(ConfigurationKey<T> keyObject) {
        var key = keyObject.getKey();
        Class<T> type = keyObject.getType();
        if (registered.containsKey(key) && type.isAssignableFrom(registered.get(key))) {
            try {
                return Optional.of(new GameConfiguration<>(keyObject, this,
                        globalConfigKeys.containsKey(key) ? MainConfig.getInstance().node((Object[]) globalConfigKeys.get(key)).get(type) : null)
                );
            } catch (SerializationException e) {
                e.printStackTrace();
            }
        } else if (registeredList.containsKey(key) && type.isAssignableFrom(List.class)) {
            //noinspection unchecked,rawtypes
            return (Optional<Configuration<T>>) (Optional) get(ConfigurationListKey.of(Object.class, key));
        }

        return Optional.empty();
    }

    @Override
    public <T> Optional<Configuration<List<T>>> get(ConfigurationListKey<T> keyObject) {
        var key = keyObject.getKey();
        Class<T> type = keyObject.getType();
        if (registeredList.containsKey(key) && type.isAssignableFrom(registeredList.get(key))) {
            try {
                return Optional.of(new GameListConfiguration<>(keyObject, this,
                        globalConfigKeys.containsKey(key) ? MainConfig.getInstance().node((Object[]) globalConfigKeys.get(key)).getList(type) : null)
                );
            } catch (SerializationException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    @Override
    public <T> boolean register(ConfigurationKey<T> keyObject) {
        var key = keyObject.getKey();
        var typeToBeSaved = keyObject.getType();
        for (var k : key) {
            if (k.contains(".") || k.contains(":")) {
                return false;
            }
        }
        if (registeredList.containsKey(key)) {
            return false;
        }
        if (!registered.containsKey(key)) {
            registered.put(key, typeToBeSaved);
            return true;
        }
        return false;
    }

    @Override
    public <T> boolean register(ConfigurationListKey<T> keyObject) {
        var key = keyObject.getKey();
        var typeToBeSaved = keyObject.getType();
        for (var k : key) {
            if (k.contains(".") || k.contains(":")) {
                return false;
            }
        }
        if (registered.containsKey(key)) {
            return false;
        }
        if (!registeredList.containsKey(key)) {
            registeredList.put(key, typeToBeSaved);
            return true;
        }
        return false;
    }

    @Override
    public List<ConfigurationKey<?>> getRegisteredKeys() {
        return registered.entrySet()
                .stream()
                .map(e -> ConfigurationKey.of(e.getValue(), e.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ConfigurationListKey<?>> getRegisteredListKeys() {
        return registeredList.entrySet()
                .stream()
                .map(e -> ConfigurationListKey.of(e.getValue(), e.getKey()))
                .collect(Collectors.toList());
    }

    public List<String> getJoinedRegisteredKeysForConfigCommandList(Class<?> type) {
        return registeredList.entrySet()
                .stream()
                .filter(e -> e.getValue() == type)
                .map(e -> String.join(".", e.getKey()))
                .collect(Collectors.toList());
    }

    public List<String> getJoinedRegisteredKeysForConfigCommand(Class<?> type) {
        return registered.entrySet()
                .stream()
                .filter(e -> e.getValue() == type)
                .map(e -> String.join(".", e.getKey()))
                .collect(Collectors.toList());
    }

    public List<String> getJoinedRegisteredKeysForConfigCommand() {
        return Stream.concat(
                    registered.keySet()
                        .stream()
                        .map(str -> String.join(".", str)),
                    registeredList.keySet()
                        .stream()
                        .map(str -> String.join(".", str))
                ).collect(Collectors.toList());
    }

    public <T> void register(ConfigurationKey<T> key, String... globalKey) {
        if (register(key)) {
            globalConfigKeys.put(key.getKey(), globalKey);
        }
    }

    public <T> void register(ConfigurationListKey<T> key, String... globalKey) {
        if (register(key)) {
            globalConfigKeys.put(key.getKey(), globalKey);
        }
    }

    public void update(List<String> key, Object object) {
        try {
            remove(key);
            saved.node(key).set(object);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }

    public boolean has(List<String> key) {
        return !saved.node(key).virtual() && !saved.node(key).isNull();
    }

    public <T> T getSaved(ConfigurationKey<T> key) {
        try {
            return saved.node(key.getKey()).get(key.getType());
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ConfigurationNode getSavedNode(List<String> key) {
        return saved.node(key);
    }

    public <T> List<T> getSaved(ConfigurationListKey<T> key) {
        try {
            return saved.node(key.getKey()).getList(key.getType());
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void remove(List<String> key) {
        try {
            saved.node(key).set(null);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T getOrDefault(ConfigurationKey<T> key, T defaultObject) {
        var opt = get(key);
        if (opt.isEmpty()) {
            return defaultObject;
        } else {
            return opt.get().get();
        }
    }

    @Override
    public <T> List<T> getOrDefault(ConfigurationListKey<T> key, List<T> defaultValue) {
        var opt = get(key);
        if (opt.isEmpty()) {
            return defaultValue;
        } else {
            return opt.get().get();
        }
    }

    public void applyNode(ConfigurationNode configurationNode) {
        saved.from(configurationNode);

        // Migration of older keys (if some key is changed, please add new migrateOldAbsoluteKey; you can also add it multiple times to one key)
        try {
            // @formatter:off

            new ConfigGenerator(null, configurationNode).start()
                    .key(ConfigurationContainer.TEAM_JOIN_ITEM_ENABLED.getKey())
                        .migrateOldAbsoluteKey("compass-enabled")
                        .dontCreateDef()
                    .key(ConfigurationContainer.JOIN_RANDOM_TEAM_AFTER_LOBBY.getKey())
                        .migrateOldAbsoluteKey("join-randomly-after-lobby-timeout")
                        .dontCreateDef()
                    .key(ConfigurationContainer.JOIN_RANDOM_TEAM_ON_JOIN.getKey())
                        .migrateOldAbsoluteKey("join-randomly-on-lobby-join")
                        .dontCreateDef()
                    .key(ConfigurationContainer.BOSSBAR_LOBBY_ENABLED.getKey())
                        .migrateOldAbsoluteKey("lobbybossbar")
                        .dontCreateDef()
                    .key(ConfigurationContainer.BOSSBAR_GAME_ENABLED.getKey())
                        .migrateOldAbsoluteKey("bossbar")
                        .dontCreateDef()
                    .key(ConfigurationContainer.SIDEBAR_GAME_ENABLED.getKey())
                        .migrateOldAbsoluteKey("scoreboard")
                        .dontCreateDef()
                    .key(ConfigurationContainer.SIDEBAR_LOBBY_ENABLED.getKey())
                        .migrateOldAbsoluteKey("lobbyscoreboard")
                        .dontCreateDef()
                    // these two keys are now children of their former names
                    .key("game-start-items").moveIfAbsolute(n -> !n.virtual() && !n.isMap(), ConfigurationContainer.GAME_START_ITEMS_ENABLED.getKey())
                    .key("player-respawn-items").moveIfAbsolute(n -> !n.virtual() && !n.isMap(), ConfigurationContainer.PLAYER_RESPAWN_ITEMS_ENABLED.getKey())
                    .key(ConfigurationContainer.HOLOGRAMS_ABOVE_BEDS.getKey())
                        .migrateOldAbsoluteKey("holo-above-bed")
                        .dontCreateDef()
                    .key(ConfigurationContainer.TARGET_BLOCK_RESPAWN_ANCHOR_FILL_ON_START.getKey())
                        .migrateOldAbsoluteKey("anchor-auto-fill")
                        .dontCreateDef()
                    .key(ConfigurationContainer.TARGET_BLOCK_RESPAWN_ANCHOR_ENABLE_DECREASE.getKey())
                        .migrateOldAbsoluteKey("anchor-decreasing")
                        .dontCreateDef()
                    .key(ConfigurationContainer.TARGET_BLOCK_CAKE_DESTROY_BY_EATING.getKey())
                        .migrateOldAbsoluteKey("cake-target-block-eating")
                        .dontCreateDef()
                    .key(ConfigurationContainer.TARGET_BLOCK_ALLOW_DESTROYING_WITH_EXPLOSIONS.getKey())
                        .migrateOldAbsoluteKey("target-block-explosions")
                        .dontCreateDef()

            ;


            // @formatter:on
        } catch (SerializationException ex) {
            ex.printStackTrace();
        }

        registered.forEach((key, aClass) -> {
            if (aClass == Boolean.class) { // only do migrations for boolean types
                var node = saved.node(key);
                if (!node.empty()) {
                    var raw = node.raw();
                    if (raw instanceof CharSequence) {
                        if ("true".equalsIgnoreCase(raw.toString())) {
                            update(key, true);
                        } else if ("false".equalsIgnoreCase(raw.toString())) {
                            update(key, false);
                        } else {
                            remove(key);
                        }
                    }
                }
            }
        });
    }
}
