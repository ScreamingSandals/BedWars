package org.screamingsandals.bedwars.config;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO: somehow manage to implement this into Configurate
// TODO: ScreamingLib candidate
public class Bukkit2Map {
    public static Map<String,Object> serialize(ConfigurationSerializable object) {
        var map = object.serialize().entrySet().stream()
                .map(obj -> {
                    if (obj.getValue() instanceof List) {
                        return new AbstractMap.SimpleEntry<>(obj.getKey(), ((List<?>) obj.getValue())
                                .stream()
                                .map(val -> {
                                    if (val instanceof ConfigurationSerializable) {
                                        return serialize((ConfigurationSerializable) val);
                                    }
                                    return val;
                                }).collect(Collectors.toList()));
                    } else if (obj.getValue() instanceof Map) {
                        return new AbstractMap.SimpleEntry<>(obj.getKey(), ((Map<?, ?>) obj.getValue())
                                .entrySet()
                                .stream()
                                .map(val -> {
                                    if (val.getValue() instanceof ConfigurationSerializable) {
                                        return new AbstractMap.SimpleEntry<>(val.getKey(), serialize((ConfigurationSerializable) val.getValue()));
                                    }
                                    return val;
                                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                    } else if (obj.getValue() instanceof ConfigurationSerializable) {
                        return new AbstractMap.SimpleEntry<>(obj.getKey(), serialize((ConfigurationSerializable) obj.getValue()));
                    }
                    return obj;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> (Object) entry.getValue()));
        var annotation = object.getClass().getAnnotation(SerializableAs.class);
        if (annotation != null && !annotation.value().isBlank()) {
            map.put("==", annotation.value());
        } else {
            map.put("==", object.getClass().getName());
        }
        return map;
    }
}
