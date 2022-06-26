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

import org.screamingsandals.bedwars.api.ArenaTime;

/**
 * @author ScreamingSandals
 * @since 0.3.0
 */
public interface GameConfigurationContainer extends ConfigurationContainer {
    ConfigurationKey<Boolean> TEAM_JOIN_ITEM_ENABLED = ConfigurationKey.of(Boolean.class, "team-join-item-enabled");
    ConfigurationKey<Boolean> JOIN_RANDOM_TEAM_AFTER_LOBBY = ConfigurationKey.of(Boolean.class, "join-random-team-after-lobby");
    ConfigurationKey<Boolean> JOIN_RANDOM_TEAM_ON_JOIN = ConfigurationKey.of(Boolean.class, "join-random-team-on-join");
    ConfigurationKey<Boolean> ADD_WOOL_TO_INVENTORY_ON_JOIN = ConfigurationKey.of(Boolean.class, "add-wool-to-inventory-on-join");
    ConfigurationKey<Boolean> PREVENT_KILLING_VILLAGERS = ConfigurationKey.of(Boolean.class, "prevent-killing-villagers");
    ConfigurationKey<Boolean> PLAYER_DROPS = ConfigurationKey.of(Boolean.class, "player-drops");
    ConfigurationKey<Boolean> FRIENDLYFIRE = ConfigurationKey.of(Boolean.class, "friendlyfire");
    ConfigurationKey<Boolean> COLORED_LEATHER_BY_TEAM_IN_LOBBY = ConfigurationKey.of(Boolean.class, "in-lobby-colored-leather-by-team");
    ConfigurationKey<Boolean> KEEP_INVENTORY_ON_DEATH = ConfigurationKey.of(Boolean.class, "keep-inventory-on-death");
    ConfigurationKey<Boolean> KEEP_ARMOR_ON_DEATH = ConfigurationKey.of(Boolean.class, "keep-armor-on-death");
    ConfigurationKey<Boolean> ALLOW_CRAFTING = ConfigurationKey.of(Boolean.class, "allow-crafting");
    ConfigurationKey<Boolean> PREVENT_SPAWNING_MOBS = ConfigurationKey.of(Boolean.class, "prevent-spawning-mobs");
    ConfigurationKey<Boolean> SPAWNER_HOLOGRAMS = ConfigurationKey.of(Boolean.class, "spawner-holograms");
    ConfigurationKey<Boolean> SPAWNER_DISABLE_MERGE = ConfigurationKey.of(Boolean.class, "spawner-disable-merge");
    ConfigurationKey<Boolean> SPAWNER_COUNTDOWN_HOLOGRAM = ConfigurationKey.of(Boolean.class, "spawner-holograms-countdown");
    ConfigurationKey<Boolean> DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA = ConfigurationKey.of(Boolean.class, "damage-when-player-is-not-in-arena");
    ConfigurationKey<Boolean> REMOVE_UNUSED_TARGET_BLOCKS = ConfigurationKey.of(Boolean.class, "remove-unused-target-blocks");
    ConfigurationKey<Boolean> ALLOW_BLOCK_FALLING = ConfigurationKey.of(Boolean.class, "allow-block-falling");
    ConfigurationKey<Boolean> HOLOGRAMS_ABOVE_BEDS = ConfigurationKey.of(Boolean.class, "holograms-above-bed");
    ConfigurationKey<Boolean> ALLOW_SPECTATOR_JOIN = ConfigurationKey.of(Boolean.class, "allow-spectator-join");
    ConfigurationKey<Boolean> STOP_TEAM_SPAWNERS_ON_DIE = ConfigurationKey.of(Boolean.class, "stop-team-spawners-on-die");
    ConfigurationKey<Boolean> INVISIBLE_LOBBY_ON_GAME_START = ConfigurationKey.of(Boolean.class, "invisible-lobby-on-game-start");
    ConfigurationKey<Boolean> ENABLE_BELOW_NAME_HEALTH_INDICATOR = ConfigurationKey.of(Boolean.class, "enable-below-name-health-indicator");
    ConfigurationKey<Boolean> USE_CERTAIN_POPULAR_SERVER_LIKE_HOLOGRAMS_FOR_SPAWNERS = ConfigurationKey.of(Boolean.class, "use-certain-popular-server-like-holograms-for-spawners");
    ConfigurationKey<Boolean> USE_TEAM_LETTER_PREFIXES_BEFORE_PLAYER_NAMES = ConfigurationKey.of(Boolean.class, "use-team-letter-prefixes-before-player-names");
    ConfigurationKey<Boolean> USE_CERTAIN_POPULAR_SERVER_TITLES = ConfigurationKey.of(Boolean.class, "use-certain-popular-server-titles");
    ConfigurationKey<Boolean> SHOW_GAME_INFO_ON_START = ConfigurationKey.of(Boolean.class, "show-game-info-on-start");

    ConfigurationKey<String> PREFIX = ConfigurationKey.of(String.class, "prefix");
    ConfigurationKey<String> DEFAULT_SHOP_FILE = ConfigurationKey.of(String.class, "default-shop-file");

    ConfigurationKey<ArenaTime> ARENA_TIME = ConfigurationKey.of(ArenaTime.class, "arena-time");

    ConfigurationKey<Boolean> BOSSBAR_LOBBY_ENABLED = ConfigurationKey.of(Boolean.class, "bossbar", "lobby", "enabled");
    ConfigurationKey<String> BOSSBAR_LOBBY_COLOR = ConfigurationKey.of(String.class, "bossbar", "lobby", "color"); // String in API, registered as enum
    ConfigurationKey<String> BOSSBAR_LOBBY_DIVISION = ConfigurationKey.of(String.class, "bossbar", "lobby", "division"); // String in API, registered as enum
    ConfigurationKey<Boolean> BOSSBAR_GAME_ENABLED = ConfigurationKey.of(Boolean.class, "bossbar", "game", "enabled");
    ConfigurationKey<String> BOSSBAR_GAME_COLOR = ConfigurationKey.of(String.class, "bossbar", "game", "color"); // String in API, registered as enum
    ConfigurationKey<String> BOSSBAR_GAME_DIVISION = ConfigurationKey.of(String.class, "bossbar", "game", "division"); // String in API, registered as enum

    ConfigurationKey<String> SIDEBAR_DATE_FORMAT = ConfigurationKey.of(String.class, "sidebar", "date-format");
    ConfigurationKey<Boolean> SIDEBAR_GAME_ENABLED = ConfigurationKey.of(Boolean.class, "sidebar", "game", "enabled");
    ConfigurationKey<Boolean> SIDEBAR_GAME_LEGACY_SIDEBAR = ConfigurationKey.of(Boolean.class, "sidebar", "game", "legacy-sidebar");
    ConfigurationKey<String> SIDEBAR_GAME_TITLE = ConfigurationKey.of(String.class, "sidebar", "game", "title");
    ConfigurationKey<String> SIDEBAR_GAME_TEAM_PREFIXES_TARGET_BLOCK_LOST = ConfigurationKey.of(String.class, "sidebar", "game", "team-prefixes", "target-block-lost");
    ConfigurationKey<String> SIDEBAR_GAME_TEAM_PREFIXES_ANCHOR_EMPTY = ConfigurationKey.of(String.class, "sidebar", "game", "team-prefixes", "anchor-empty");
    ConfigurationKey<String> SIDEBAR_GAME_TEAM_PREFIXES_TARGET_BLOCK_EXISTS = ConfigurationKey.of(String.class, "sidebar", "game", "team-prefixes", "target-block-exists");
    ConfigurationKey<String> SIDEBAR_GAME_TEAM_PREFIXES_TEAM_COUNT = ConfigurationKey.of(String.class, "sidebar", "game", "team-prefixes", "team-count");
    ConfigurationKey<String> SIDEBAR_GAME_TEAM_LINE = ConfigurationKey.of(String.class, "sidebar", "game", "team-line");
    ConfigurationListKey<String> SIDEBAR_GAME_CONTENT = ConfigurationListKey.of(String.class, "sidebar", "game", "content");
    ConfigurationKey<String> SIDEBAR_GAME_ADDITIONAL_CONTENT_SHOW_IF_TEAM_COUNT = ConfigurationKey.of(String.class, "sidebar", "game", "additional-content", "show-if-team-count");
    ConfigurationListKey<String> SIDEBAR_GAME_ADDITIONAL_CONTENT_CONTENT = ConfigurationListKey.of(String.class, "sidebar", "game", "additional-content", "content");
    ConfigurationKey<Boolean> SIDEBAR_LOBBY_ENABLED = ConfigurationKey.of(Boolean.class, "sidebar", "lobby", "enabled");
    ConfigurationKey<String> SIDEBAR_LOBBY_TITLE = ConfigurationKey.of(String.class, "sidebar", "lobby", "title");
    ConfigurationListKey<String> SIDEBAR_LOBBY_CONTENT = ConfigurationListKey.of(String.class, "sidebar", "lobby", "content");

    ConfigurationKey<Boolean> GAME_START_ITEMS_ENABLED = ConfigurationKey.of(Boolean.class, "game-start-items", "enabled");
    ConfigurationKey<Boolean> PLAYER_RESPAWN_ITEMS_ENABLED = ConfigurationKey.of(Boolean.class, "player-respawn-items", "enabled");

    ConfigurationKey<Boolean> TARGET_BLOCK_RESPAWN_ANCHOR_FILL_ON_START = ConfigurationKey.of(Boolean.class, "target-block", "respawn-anchor", "fill-on-start");
    ConfigurationKey<Boolean> TARGET_BLOCK_RESPAWN_ANCHOR_ENABLE_DECREASE = ConfigurationKey.of(Boolean.class, "target-block", "respawn-anchor", "enable-decrease");
    ConfigurationKey<Boolean> TARGET_BLOCK_CAKE_DESTROY_BY_EATING = ConfigurationKey.of(Boolean.class, "target-block", "cake", "destroy-by-eating");
    ConfigurationKey<Boolean> TARGET_BLOCK_ALLOW_DESTROYING_WITH_EXPLOSIONS = ConfigurationKey.of(Boolean.class, "target-block", "allow-destroying-with-explosions");
}
