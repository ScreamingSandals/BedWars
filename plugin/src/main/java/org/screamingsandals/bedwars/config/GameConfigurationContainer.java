package org.screamingsandals.bedwars.config;

import lombok.Getter;
import org.screamingsandals.bedwars.api.config.Configuration;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;

public class GameConfigurationContainer implements ConfigurationContainer {

    private final Map<String, Class<?>> registered = new HashMap<>();
    @Getter
    private final BasicConfigurationNode saved = BasicConfigurationNode.root();
    private final Map<String, String[]> globalConfigKeys = new HashMap<>();

    {
        register(ConfigurationContainer.COMPASS, Boolean.class, ConfigurationContainer.COMPASS);
        register(ConfigurationContainer.JOIN_RANDOM_TEAM_AFTER_LOBBY, Boolean.class, ConfigurationContainer.JOIN_RANDOM_TEAM_AFTER_LOBBY);
        register(ConfigurationContainer.JOIN_RANDOM_TEAM_ON_JOIN, Boolean.class, ConfigurationContainer.JOIN_RANDOM_TEAM_ON_JOIN);
        register(ConfigurationContainer.ADD_WOOL_TO_INVENTORY_ON_JOIN, Boolean.class, ConfigurationContainer.ADD_WOOL_TO_INVENTORY_ON_JOIN);
        register(ConfigurationContainer.PROTECT_SHOP, Boolean.class, ConfigurationContainer.PROTECT_SHOP);
        register(ConfigurationContainer.PLAYER_DROPS, Boolean.class, ConfigurationContainer.PLAYER_DROPS);
        register(ConfigurationContainer.FRIENDLY_FIRE, Boolean.class, ConfigurationContainer.FRIENDLY_FIRE);
        register(ConfigurationContainer.COLORED_LEATHER_BY_TEAM_IN_LOBBY, Boolean.class, ConfigurationContainer.COLORED_LEATHER_BY_TEAM_IN_LOBBY);
        register(ConfigurationContainer.KEEP_INVENTORY, Boolean.class, ConfigurationContainer.KEEP_INVENTORY);
        register(ConfigurationContainer.KEEP_ARMOR, Boolean.class, ConfigurationContainer.KEEP_ARMOR);
        register(ConfigurationContainer.CRAFTING, Boolean.class, ConfigurationContainer.CRAFTING);
        register(ConfigurationContainer.LOBBY_BOSSBAR, Boolean.class, "bossbar", "lobby", "enabled");
        register(ConfigurationContainer.GAME_BOSSBAR, Boolean.class, "bossbar", "game", "enabled");
        register(ConfigurationContainer.GAME_SCOREBOARD, Boolean.class, "scoreboard", "enabled");
        register(ConfigurationContainer.LOBBY_SCOREBOARD, Boolean.class, "lobby-scoreboard", "enabled");
        register(ConfigurationContainer.PREVENT_SPAWNING_MOBS, Boolean.class, ConfigurationContainer.PREVENT_SPAWNING_MOBS);
        register(ConfigurationContainer.SPAWNER_HOLOGRAMS, Boolean.class, ConfigurationContainer.SPAWNER_HOLOGRAMS);
        register(ConfigurationContainer.SPAWNER_DISABLE_MERGE, Boolean.class, ConfigurationContainer.SPAWNER_DISABLE_MERGE);
        register(ConfigurationContainer.ENABLE_GAME_START_ITEMS, Boolean.class, "game-start-items", "enabled");
        register(ConfigurationContainer.ENABLE_PLAYER_RESPAWN_ITEMS, Boolean.class, "player-respawn-items", "enabled");
        register(ConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM, Boolean.class, ConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM);
        register(ConfigurationContainer.DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA, Boolean.class, ConfigurationContainer.DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA);
        register(ConfigurationContainer.REMOVE_UNUSED_TARGET_BLOCKS, Boolean.class, ConfigurationContainer.REMOVE_UNUSED_TARGET_BLOCKS);
        register(ConfigurationContainer.BLOCK_FALLING, Boolean.class, ConfigurationContainer.BLOCK_FALLING);
        register(ConfigurationContainer.HOLOGRAMS_ABOVE_BEDS, Boolean.class, ConfigurationContainer.HOLOGRAMS_ABOVE_BEDS);
        register(ConfigurationContainer.SPECTATOR_JOIN, Boolean.class, ConfigurationContainer.SPECTATOR_JOIN);
        register(ConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE, Boolean.class, ConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE);
        register(ConfigurationContainer.ANCHOR_AUTO_FILL, Boolean.class, "target-block", "respawn-anchor", "fill-on-start");
        register(ConfigurationContainer.ANCHOR_DECREASING, Boolean.class, "target-block", "respawn-anchor", "enable-decrease");
        register(ConfigurationContainer.CAKE_TARGET_BLOCK_EATING, Boolean.class, "target-block", "cake", "destroy-by-eating");
        register(ConfigurationContainer.TARGET_BLOCK_EXPLOSIONS, Boolean.class, "target-block", "allow-destroying-with-explosions");
        register(ConfigurationContainer.INVISIBLE_LOBBY_ON_GAME_START, Boolean.class, "invisible-lobby-on-game-start");
        register(ConfigurationContainer.HEALTH_INDICATOR, Boolean.class, "enable-below-name-health-indicator");
        register(ConfigurationContainer.HYPIXEL_HOLOGRAMS, Boolean.class, "use-hypixel-like-holograms-for-spawners");
        register(ConfigurationContainer.NEW_GAME_SCOREBOARD, Boolean.class, "scoreboard", "new-scoreboard", "enabled");
    }

    @Override
    public <T> Optional<Configuration<T>> get(String key, Class<T> type) {
        if (registered.containsKey(key) && type.isAssignableFrom(registered.get(key))) {
            try {
                return Optional.of(new GameConfiguration<>(type,this, key,
                        globalConfigKeys.containsKey(key) ? MainConfig.getInstance().node((Object[]) globalConfigKeys.get(key)).get(type) : null)
                );
            } catch (SerializationException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }

    @Override
    public <T> boolean register(String key, Class<T> typeToBeSaved) {
        if (key.contains(".") || key.contains(":")) {
            return false;
        }
        if (!registered.containsKey(key)) {
            registered.put(key, typeToBeSaved);
            return true;
        }
        return false;
    }

    @Override
    public List<String> getRegisteredKeys() {
        return new ArrayList<>(registered.keySet());
    }

    public <T> void register(String key, Class<T> typeToBeSaved, String... globalKey) {
        if (register(key, typeToBeSaved)) {
            globalConfigKeys.put(key, globalKey);
        }
    }

    public void update(String key, Object object) {
        try {
            remove(key);
            saved.node(key).set(object);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }

    public boolean has(String key) {return !saved.node(key).empty();}

    public <T> T getSaved(String key, Class<T> type) {
        try {
            return saved.node(key).get(type);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void remove(String key) {
        try {
            saved.node(key).set(null);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T getOrDefault(String key, Class<T> type, T defaultObject) {
        var opt = get(key, type);
        if (opt.isEmpty()) {
            return defaultObject;
        } else {
            return opt.get().get();
        }
    }

    public Class<?> getType(String key) {
        return registered.get(key);
    }

    public void applyNode(ConfigurationNode configurationNode) {
        saved.from(configurationNode);

        registered.forEach((key, aClass) -> {
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
        });
    }
}
