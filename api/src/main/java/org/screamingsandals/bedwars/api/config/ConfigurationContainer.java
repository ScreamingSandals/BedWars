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

package org.screamingsandals.bedwars.api.config;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
@ApiStatus.NonExtendable
public interface ConfigurationContainer {

    String COMPASS = "compass-enabled";
    String JOIN_RANDOM_TEAM_AFTER_LOBBY = "join-randomly-after-lobby-timeout";
    String JOIN_RANDOM_TEAM_ON_JOIN = "join-randomly-on-lobby-join";
    String ADD_WOOL_TO_INVENTORY_ON_JOIN = "add-wool-to-inventory-on-join";
    String PROTECT_SHOP = "prevent-killing-villagers";
    String PLAYER_DROPS = "player-drops";
    String FRIENDLY_FIRE = "friendlyfire";
    String COLORED_LEATHER_BY_TEAM_IN_LOBBY = "in-lobby-colored-leather-by-team";
    String KEEP_INVENTORY = "keep-inventory-on-death";
    String KEEP_ARMOR = "keep-armor-on-death";
    String CRAFTING = "allow-crafting";
    String LOBBY_BOSSBAR = "lobbybossbar";
    String GAME_BOSSBAR = "bossbar";
    String GAME_SCOREBOARD = "scoreboard";
    String LOBBY_SCOREBOARD = "lobbyscoreboard";
    String PREVENT_SPAWNING_MOBS = "prevent-spawning-mobs";
    String SPAWNER_HOLOGRAMS = "spawner-holograms";
    String SPAWNER_DISABLE_MERGE = "spawner-disable-merge";
    String ENABLE_GAME_START_ITEMS = "game-start-items";
    String ENABLE_PLAYER_RESPAWN_ITEMS = "player-respawn-items";
    String SPAWNER_COUNTDOWN_HOLOGRAM = "spawner-holograms-countdown";
    String DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA = "damage-when-player-is-not-in-arena";
    String REMOVE_UNUSED_TARGET_BLOCKS = "remove-unused-target-blocks";
    String BLOCK_FALLING = "allow-block-falling";
    String HOLOGRAMS_ABOVE_BEDS = "holo-above-bed";
    String SPECTATOR_JOIN = "allow-spectator-join";
    String STOP_TEAM_SPAWNERS_ON_DIE = "stop-team-spawners-on-die";
    String ANCHOR_AUTO_FILL = "anchor-auto-fill";
    String ANCHOR_DECREASING = "anchor-decreasing";
    String CAKE_TARGET_BLOCK_EATING = "cake-target-block-eating";
    String TARGET_BLOCK_EXPLOSIONS = "target-block-explosions";
    String INVISIBLE_LOBBY_ON_GAME_START = "invisible-lobby-on-game-start";
    String HEALTH_INDICATOR = "health-indicator";
    String CERTAIN_POPULAR_SERVER_HOLOGRAMS = "certain-popular-server-holograms";

    String DEFAULT_SHOP_FILE = "default-shop-file";

    /**
     * Gets configuration from the key
     *
     * @param key Key of the configuration
     * @param type Type of the configuration
     * @return configuration or empty optional
     */
    <T> Optional<Configuration<T>> get(String key, Class<T> type);

    /**
     * Registers new configuration type. This allows addons to save information directly to game.
     *
     * @param key Key of new configuration, it's invalid to use dots or colons
     * @param typeToBeSaved type which will be used for saving
     * @return true on success
     */
    <T> boolean register(String key, Class<T> typeToBeSaved);

    /**
     * Gets all keys known by this configuration container
     *
     * @return list of all registered keys
     */
    List<String> getRegisteredKeys();

    /**
     * Gets the value from configuration or returns back the default value
     *
     * @param key Key of the configuration
     * @param type Type of the configuration
     * @param defaultValue Default value if the configuration won't be found
     * @return object from configuration if registered; otherwise defaultValue
     */
    <T> T getOrDefault(String key, Class<T> type, T defaultValue);

    @Nullable
    ConfigurationContainer getParentContainer();
}
