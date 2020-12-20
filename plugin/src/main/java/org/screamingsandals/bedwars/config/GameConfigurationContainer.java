package org.screamingsandals.bedwars.config;

import lombok.Getter;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.config.Configuration;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;

import java.util.*;

public class GameConfigurationContainer implements ConfigurationContainer {

    private final Map<String, Class<?>> registered = new HashMap<>();
    @Getter
    private final Map<String, Object> saved = new HashMap<>();
    private final Map<String, String> globalConfigKeys = new HashMap<>();

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
        register(ConfigurationContainer.LOBBY_BOSSBAR, Boolean.class, "bossbar.lobby.enable");
        register(ConfigurationContainer.GAME_BOSSBAR, Boolean.class, "bossbar.game.enable");
        register(ConfigurationContainer.GAME_SCOREBOARD, Boolean.class, "scoreboard.enable");
        register(ConfigurationContainer.LOBBY_SCOREBOARD, Boolean.class, "lobby-scoreboard.enabled");
        register(ConfigurationContainer.PREVENT_SPAWNING_MOBS, Boolean.class, ConfigurationContainer.PREVENT_SPAWNING_MOBS);
        register(ConfigurationContainer.SPAWNER_HOLOGRAMS, Boolean.class, ConfigurationContainer.SPAWNER_HOLOGRAMS);
        register(ConfigurationContainer.SPAWNER_DISABLE_MERGE, Boolean.class, ConfigurationContainer.SPAWNER_DISABLE_MERGE);
        register(ConfigurationContainer.ENABLE_GAME_START_ITEMS, Boolean.class, ConfigurationContainer.ENABLE_GAME_START_ITEMS);
        register(ConfigurationContainer.ENABLE_PLAYER_RESPAWN_ITEMS, Boolean.class, ConfigurationContainer.ENABLE_PLAYER_RESPAWN_ITEMS);
        register(ConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM, Boolean.class, ConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM);
        register(ConfigurationContainer.DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA, Boolean.class, ConfigurationContainer.DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA);
        register(ConfigurationContainer.REMOVE_UNUSED_TARGET_BLOCKS, Boolean.class, ConfigurationContainer.REMOVE_UNUSED_TARGET_BLOCKS);
        register(ConfigurationContainer.BLOCK_FALLING, Boolean.class, ConfigurationContainer.BLOCK_FALLING);
        register(ConfigurationContainer.HOLOGRAMS_ABOVE_BEDS, Boolean.class, ConfigurationContainer.HOLOGRAMS_ABOVE_BEDS);
        register(ConfigurationContainer.SPECTATOR_JOIN, Boolean.class, ConfigurationContainer.SPECTATOR_JOIN);
        register(ConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE, Boolean.class, ConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE);
        register(ConfigurationContainer.ANCHOR_AUTO_FILL, Boolean.class, "target-block.respawn-anchor.fill-on-start");
        register(ConfigurationContainer.ANCHOR_DECREASING, Boolean.class, "target-block.respawn-anchor.enable-decrease");
        register(ConfigurationContainer.CAKE_TARGET_BLOCK_EATING, Boolean.class, "target-block.cake.destroy-by-eating");
        register(ConfigurationContainer.TARGET_BLOCK_EXPLOSIONS, Boolean.class, "target-block.allow-destroying-with-explosions");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<Configuration<T>> get(String key, Class<T> type) {
        if (registered.containsKey(key) && type.isAssignableFrom(registered.get(key))) {
            if (saved.get(key) instanceof String && type.isAssignableFrom(Boolean.class)) {
                convertOldFormat(key);
            }

            return Optional.of(new GameConfiguration<>(this, key,
                    globalConfigKeys.containsKey(key) ? (T) Main.getConfigurator().config.get(globalConfigKeys.get(key)) : null)
            );
        }

        return Optional.empty();
    }

    private void convertOldFormat(String key) {
        String value = saved.get(key).toString();
        if (value.equalsIgnoreCase("true")) {
            saved.put(key, true);
        } else if (value.equalsIgnoreCase("false")) {
            saved.put(key, false);
        } else {
            saved.remove(key);
        }
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

    public <T> void register(String key, Class<T> typeToBeSaved, String globalKey) {
        if (register(key, typeToBeSaved)) {
            globalConfigKeys.put(key, globalKey);
        }
    }

    public void update(String key, Object object) {
        saved.put(key, object);
    }

    public boolean has(String key) {return saved.containsKey(key);}

    public Object getSaved(String key) {
        return saved.get(key);
    }

    public void remove(String key) {
        saved.remove(key);
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
}
