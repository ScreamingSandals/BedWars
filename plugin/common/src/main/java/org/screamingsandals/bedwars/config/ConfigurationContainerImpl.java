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

package org.screamingsandals.bedwars.config;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.config.Configuration;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.config.ConfigurationKey;
import org.screamingsandals.bedwars.api.config.ConfigurationListKey;
import org.screamingsandals.lib.configurate.SLibSerializers;
import org.screamingsandals.lib.item.ItemStack;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationContainerImpl implements ConfigurationContainer {

    @Nullable
    @Getter
    @Setter
    private ConfigurationContainer parentContainer;
    private final Map<List<String>, Class<?>> registered = new HashMap<>();
    private final Map<List<String>, Class<?>> registeredList = new HashMap<>();
    @Getter
    private final BasicConfigurationNode saved = BasicConfigurationNode.root(ConfigurationOptions.defaults().serializers(SLibSerializers::makeSerializers));
    private final Map<List<String>, String[]> globalConfigKeys = new HashMap<>();
    @Getter
    private final Map<List<String>, String[]> descriptionKeys = new HashMap<>();

    @Override
    public <T> Optional<Configuration<T>> get(ConfigurationKey<T> keyObject) {
        var key = keyObject.getKey();
        Class<T> type = keyObject.getType();
        if (registered.containsKey(key) && (type.isAssignableFrom(registered.get(key))
                        || (type == String.class && registered.get(key).isEnum())
        )) {
            try {
                return Optional.of(new ConfigurationImpl<>(keyObject, this,
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
                return Optional.of(new ListConfigurationImpl<>(keyObject, this,
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

    public String getStringDataTypeForCommand(List<String> key) {
        if (registered.containsKey(key)) {
            return registered.get(key).getSimpleName();
        }
        if (registeredList.containsKey(key)) {
            return "List<" + registeredList.get(key).getSimpleName() + ">";
        }
        return "Object";
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

    public List<String> getJoinedRegisteredKeysForConfigCommandEnums() {
        return registered.entrySet()
                .stream()
                .filter(e -> e.getValue().isEnum())
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
            if (object != null && object.getClass().isEnum()) {
                object = object.toString(); // save enums as strings (thanks Configurate, rly)
            }
            if (object instanceof ItemStack) {
                saved.node(key).set(ItemStack.class, object);
            } else {
                saved.node(key).set(object);
            }
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }

    public <T> void updateList(List<String> key, Class<T> type, List<T> object) {
        try {
            remove(key);
            saved.node(key).setList(type, object);
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
            var o = opt.get().get();
            return o != null ? o : defaultObject;
        }
    }

    @Override
    public <T> List<T> getOrDefault(ConfigurationListKey<T> key, List<T> defaultValue) {
        var opt = get(key);
        if (opt.isEmpty()) {
            return defaultValue;
        } else {
            var o = opt.get().get();
            return o != null ? o : defaultValue;
        }
    }

    public void applyNode(ConfigurationNode configurationNode) {
        saved.from(configurationNode);

        migrateOld(configurationNode);

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

    protected void migrateOld(ConfigurationNode configurationNode) {

    }
}
