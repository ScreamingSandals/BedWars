/*
 * Copyright (C) 2025 ScreamingSandals
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

import org.screamingsandals.bedwars.api.config.ConfigurationKey;
import org.screamingsandals.bedwars.api.config.ConfigurationListKey;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.spectator.bossbar.BossBarColor;
import org.screamingsandals.lib.spectator.bossbar.BossBarDivision;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class GameConfigurationContainerImpl extends ConfigurationContainerImpl implements GameConfigurationContainer {

    // TODO: Properly expose in the API
    public static final ConfigurationKey<BossBarColor> BOSSBAR_LOBBY_COLOR = ConfigurationKey.of(BossBarColor.class, "bossbar", "lobby", "color");
    public static final ConfigurationKey<BossBarDivision> BOSSBAR_LOBBY_DIVISION = ConfigurationKey.of(BossBarDivision.class, "bossbar", "lobby", "division");
    public static final ConfigurationKey<BossBarColor> BOSSBAR_GAME_COLOR = ConfigurationKey.of(BossBarColor.class, "bossbar", "game", "color");
    public static final ConfigurationKey<BossBarDivision> BOSSBAR_GAME_DIVISION = ConfigurationKey.of(BossBarDivision.class, "bossbar", "game", "division");
    public static final ConfigurationListKey<ItemStack> GAME_START_ITEMS_ITEMS = ConfigurationListKey.of(ItemStack.class, "game-start-items", "items");
    public static final ConfigurationListKey<ItemStack> PLAYER_RESPAWN_ITEMS_ITEMS = ConfigurationListKey.of(ItemStack.class, "player-respawn-items", "items");

    {
        register(TEAM_JOIN_ITEM_ENABLED, "team-join-item-enabled");
        register(JOIN_RANDOM_TEAM_AFTER_LOBBY, "join-random-team-after-lobby");
        register(JOIN_RANDOM_TEAM_ON_JOIN, "join-random-team-on-join");
        register(ADD_WOOL_TO_INVENTORY_ON_JOIN, "add-wool-to-inventory-on-join");
        register(PREVENT_KILLING_VILLAGERS, "prevent-killing-villagers");
        register(PLAYER_DROPS, "player-drops");
        register(FRIENDLYFIRE, "friendlyfire");
        register(COLORED_LEATHER_BY_TEAM_IN_LOBBY, "in-lobby-colored-leather-by-team");
        register(KEEP_INVENTORY_ON_DEATH, "keep-inventory-on-death");
        register(KEEP_ARMOR_ON_DEATH, "keep-armor-on-death");
        register(ALLOW_CRAFTING, "allow-crafting");
        register(BOSSBAR_LOBBY_ENABLED,  "bossbar", "lobby", "enabled");
        register(BOSSBAR_LOBBY_COLOR, "bossbar", "lobby", "color");
        register(BOSSBAR_LOBBY_DIVISION, "bossbar", "lobby", "division");
        register(BOSSBAR_GAME_ENABLED,  "bossbar", "game", "enabled");
        register(BOSSBAR_GAME_COLOR, "bossbar", "game", "color");
        register(BOSSBAR_GAME_DIVISION, "bossbar", "game", "division");
        register(SIDEBAR_DATE_FORMAT, "sidebar", "date-format");
        register(SIDEBAR_GAME_ENABLED, "sidebar", "game", "enabled");
        register(SIDEBAR_GAME_LEGACY_SIDEBAR, "sidebar", "game", "legacy-sidebar");
        register(SIDEBAR_GAME_TITLE, "sidebar", "game", "title");
        register(SIDEBAR_GAME_TEAM_PREFIXES_TARGET_BLOCK_LOST, "sidebar", "game", "team-prefixes", "target-block-lost");
        register(SIDEBAR_GAME_TEAM_PREFIXES_ANCHOR_EMPTY, "sidebar", "game", "team-prefixes", "anchor-empty");
        register(SIDEBAR_GAME_TEAM_PREFIXES_TARGET_BLOCK_EXISTS, "sidebar", "game", "team-prefixes", "target-block-exists");
        register(SIDEBAR_GAME_TEAM_PREFIXES_TEAM_COUNT, "sidebar", "game", "team-prefixes", "team-count");
        register(SIDEBAR_GAME_TEAM_LINE, "sidebar", "game", "team-line");
        register(SIDEBAR_GAME_CONTENT, "sidebar", "game", "content");
        register(SIDEBAR_GAME_ADDITIONAL_CONTENT_SHOW_IF_TEAM_COUNT, "sidebar", "game", "additional-content", "show-if-team-count");
        register(SIDEBAR_GAME_ADDITIONAL_CONTENT_CONTENT, "sidebar", "game", "additional-content", "content");
        register(SIDEBAR_LOBBY_ENABLED,  "sidebar", "lobby", "enabled");
        register(SIDEBAR_LOBBY_TITLE, "sidebar", "lobby", "title");
        register(SIDEBAR_LOBBY_CONTENT, "sidebar", "lobby", "content");
        register(PREVENT_SPAWNING_MOBS, "prevent-spawning-mobs");
        register(SPAWNER_HOLOGRAMS, "spawner-holograms");
        register(SPAWNER_DISABLE_MERGE, "spawner-disable-merge");
        register(GAME_START_ITEMS_ENABLED, "game-start-items", "enabled");
        register(GAME_START_ITEMS_ITEMS, "game-start-items", "items");
        register(PLAYER_RESPAWN_ITEMS_ENABLED, "player-respawn-items", "enabled");
        register(PLAYER_RESPAWN_ITEMS_ITEMS, "player-respawn-items", "items");
        register(SPAWNER_COUNTDOWN_HOLOGRAM,  "spawner-holograms-countdown");
        register(DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA, "damage-when-player-is-not-in-arena");
        register(REMOVE_UNUSED_TARGET_BLOCKS, "remove-unused-target-blocks");
        register(ALLOW_BLOCK_FALLING, "allow-block-falling");
        register(HOLOGRAMS_ABOVE_BEDS, "holograms-above-bed");
        register(ALLOW_SPECTATOR_JOIN, "allow-spectator-join");
        register(STOP_TEAM_SPAWNERS_ON_DIE, "stop-team-spawners-on-die");
        register(TARGET_BLOCK_RESPAWN_ANCHOR_FILL_ON_START, "target-block", "respawn-anchor", "fill-on-start");
        register(TARGET_BLOCK_RESPAWN_ANCHOR_ENABLE_DECREASE, "target-block", "respawn-anchor", "enable-decrease");
        register(TARGET_BLOCK_CAKE_DESTROY_BY_EATING, "target-block", "cake", "destroy-by-eating");
        register(TARGET_BLOCK_ALLOW_DESTROYING_WITH_EXPLOSIONS, "target-block", "allow-destroying-with-explosions");
        register(INVISIBLE_LOBBY_ON_GAME_START, "invisible-lobby-on-game-start");
        register(ENABLE_BELOW_NAME_HEALTH_INDICATOR, "enable-below-name-health-indicator");
        register(USE_CERTAIN_POPULAR_SERVER_LIKE_HOLOGRAMS_FOR_SPAWNERS, "use-certain-popular-server-like-holograms-for-spawners");
        register(USE_TEAM_LETTER_PREFIXES_BEFORE_PLAYER_NAMES, "use-team-letter-prefixes-before-player-names");
        register(USE_CERTAIN_POPULAR_SERVER_TITLES, "use-certain-popular-server-titles");
        register(SHOW_GAME_INFO_ON_START, "show-game-info-on-start");
        register(DISABLE_HUNGER, "disable-hunger");
        register(PREVENT_SPECTATOR_FROM_FLYING_AWAY, "prevent-spectator-from-flying-away");
        register(DISABLE_DRAGON_EGG_TELEPORT, "disable-dragon-egg-teleport");
        register(DISABLE_CAKE_EATING, "disable-cake-eating");
        register(DISABLE_FLIGHT, "disable-flight");
        register(ALLOW_FAKE_DEATH, "allow-fake-death");
        register(RESET_FULL_SPAWNER_COUNTDOWN_AFTER_PICKING, "reset-full-spawner-countdown-after-picking");
        register(PLAYERS_CAN_WIN_GAME_ONLY_AFTER_SECONDS, "players-can-win-game-only-after-seconds");
        register(DEFAULT_SHOP_FILE);
        register(PREFIX, "prefix");
        register(ARENA_TIME);
        register(ECONOMY_ENABLED, "economy", "enabled");
        register(ECONOMY_RETURN_FEE, "economy", "return-fee");
        register(ECONOMY_REWARD_KILL, "economy", "reward", "kill");
        register(ECONOMY_REWARD_WIN, "economy", "reward", "win");
        register(ECONOMY_REWARD_FINAL_KILL, "economy", "reward", "final-kill");
        register(ECONOMY_REWARD_BED_DESTROY, "economy", "reward", "bed-destroy");
        register(TNT_JUMP_ENABLED, "tnt-jump", "enabled");
        register(TNT_JUMP_SOURCE_DAMAGE, "tnt-jump", "source-damage");
        register(TNT_JUMP_TEAM_DAMAGE, "tnt-jump", "team-damage");
        register(TNT_JUMP_LAUNCH_MULTIPLIER, "tnt-jump", "launch-multiplier");
        register(TNT_JUMP_REDUCE_Y, "tnt-jump", "reduce-y");
        register(TNT_JUMP_ACCELERATION_Y, "tnt-jump", "acceleration-y");
        register(TNT_JUMP_FALL_DAMAGE, "tnt-jump", "fall-damage");
        register(RESPAWN_PROTECTION_ENABLED, "respawn", "protection-enabled");
        register(RESPAWN_PROTECTION_TIME, "respawn", "protection-time");
        register(RESPAWN_SHOW_MESSAGES, "respawn", "show-messages");
        register(RESPAWN_COOLDOWN_ENABLED, "respawn-cooldown", "enabled");
        register(RESPAWN_COOLDOWN_TIME, "respawn-cooldown", "time");
        register(STATISTICS_BED_DESTROYED_KILLS, "statistics", "bed-destroyed-kills");
        register(STATISTICS_SCORES_KILL, "statistics", "scores", "kill");
        register(STATISTICS_SCORES_FINAL_KILL, "statistics", "scores", "final-kill");
        register(STATISTICS_SCORES_DIE, "statistics", "scores", "die");
        register(STATISTICS_SCORES_WIN, "statistics", "scores", "win");
        register(STATISTICS_SCORES_BED_DESTROY, "statistics", "scores", "bed-destroy");
        register(STATISTICS_SCORES_LOSE, "statistics", "scores", "lose");
        register(STATISTICS_SCORES_RECORD, "statistics", "scores", "record");
        register(KICK_PLAYERS_UPON_FINAL_DEATH_ENABLED, "kick-players-upon-final-death", "enabled");
        register(KICK_PLAYERS_UPON_FINAL_DEATH_ENABLED, "kick-players-upon-final-death", "delay");
        register(DESTROY_PLACED_BLOCKS_BY_EXPLOSION_ENABLED, "destroy-placed-blocks-by-explosion", "enabled");
        register(DESTROY_PLACED_BLOCKS_BY_EXPLOSION_BLACKLIST, "destroy-placed-blocks-by-explosion", "blacklist");
    }

    protected void migrateOld(ConfigurationNode configurationNode) {
        // Migration of older keys (if some key is changed, please add new migrateOldAbsoluteKey; you can also add it multiple times to one key)
        try {
            // @formatter:off

            new ConfigGenerator(null, configurationNode).start()
                    .key(TEAM_JOIN_ITEM_ENABLED.getKey())
                        .migrateOldAbsoluteKey("compass-enabled")
                        .dontCreateDef()
                    .key(JOIN_RANDOM_TEAM_AFTER_LOBBY.getKey())
                        .migrateOldAbsoluteKey("join-randomly-after-lobby-timeout")
                        .dontCreateDef()
                    .key(JOIN_RANDOM_TEAM_ON_JOIN.getKey())
                        .migrateOldAbsoluteKey("join-randomly-on-lobby-join")
                        .dontCreateDef()
                    .key(BOSSBAR_LOBBY_ENABLED.getKey())
                        .migrateOldAbsoluteKey("lobbybossbar")
                        .dontCreateDef()
                    .key(BOSSBAR_GAME_ENABLED.getKey())
                        .migrateOldAbsoluteKey("bossbar")
                        .dontCreateDef()
                    .key(SIDEBAR_GAME_ENABLED.getKey())
                        .migrateOldAbsoluteKey("scoreboard")
                        .dontCreateDef()
                    .key(SIDEBAR_LOBBY_ENABLED.getKey())
                        .migrateOldAbsoluteKey("lobbyscoreboard")
                        .dontCreateDef()
                    // these two keys are now children of their former names
                    .key("game-start-items").moveIfAbsolute(n -> !n.virtual() && !n.isMap(), GAME_START_ITEMS_ENABLED.getKey())
                    .key("player-respawn-items").moveIfAbsolute(n -> !n.virtual() && !n.isMap(), PLAYER_RESPAWN_ITEMS_ENABLED.getKey())
                    .key(HOLOGRAMS_ABOVE_BEDS.getKey())
                        .migrateOldAbsoluteKey("holo-above-bed")
                        .dontCreateDef()
                    .key(TARGET_BLOCK_RESPAWN_ANCHOR_FILL_ON_START.getKey())
                        .migrateOldAbsoluteKey("anchor-auto-fill")
                        .dontCreateDef()
                    .key(TARGET_BLOCK_RESPAWN_ANCHOR_ENABLE_DECREASE.getKey())
                        .migrateOldAbsoluteKey("anchor-decreasing")
                        .dontCreateDef()
                    .key(TARGET_BLOCK_CAKE_DESTROY_BY_EATING.getKey())
                        .migrateOldAbsoluteKey("cake-target-block-eating")
                        .dontCreateDef()
                    .key(TARGET_BLOCK_ALLOW_DESTROYING_WITH_EXPLOSIONS.getKey())
                        .migrateOldAbsoluteKey("target-block-explosions")
                        .dontCreateDef()

            ;


            // @formatter:on
        } catch (SerializationException ex) {
            ex.printStackTrace();
        }
    }
}
