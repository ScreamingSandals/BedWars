package org.screamingsandals.bedwars.api.game;

import org.screamingsandals.bedwars.api.*;
import org.screamingsandals.bedwars.api.boss.StatusBar;
import org.screamingsandals.bedwars.api.config.Configuration;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.api.utils.DelayFactory;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;


/**
 * @author Bedwars Team
 */
public interface Game {
    /**
     * @return Arena name
     */
	String getName();

    /**
     * @return GameStatus of the arena
     */
	GameStatus getStatus();

    /**
     *
     */
	void start();

    /**
     *
     */
	void stop();

    /**
     * @return true if GameStatus is different than DISABLED
     */
    default boolean isActivated() {
        return getStatus() != GameStatus.DISABLED;
    }

    // PLAYER MANAGEMENT

    /**
     * @param player
     */
	void joinToGame(Player player);

    /**
     * @param player
     */
	void leaveFromGame(Player player);

    /**
     * @param player
     * @param team
     */
	void selectPlayerTeam(Player player, Team team);

    /**
     * @param player
     */
	void selectPlayerRandomTeam(Player player);

    /**
     * @return defined world of the game
     */
	World getGameWorld();

    /**
     * @return
     */
	Location getPos1();

    /**
     * @return
     */
	Location getPos2();

    /**
     * @return
     */
	Location getSpectatorSpawn();

    /**
     * @return configured time of the game
     */
	int getGameTime();

    /**
     * @return configured minimal players to start the game
     */
	int getMinPlayers();

    /**
     * @return configured maximal players of the arena
     */
	int getMaxPlayers();

    /**
     * @return players in game
     */
	int countConnectedPlayers();

    /**
     * @return list of players in game
     */
	List<Player> getConnectedPlayers();

    /**
     * @return list of game stores
     */
	List<GameStore> getGameStores();

    /**
     * @return
     */
	int countGameStores();

    /**
     * @return Team instance from the name
     */
    Team getTeamFromName(String name);

    /**
     * @return
     */
	List<Team> getAvailableTeams();

    /**
     * @return
     */
	int countAvailableTeams();

    /**
     * @return
     */
	List<RunningTeam> getRunningTeams();

    /**
     * @return
     */
	int countRunningTeams();

    /**
     * @param player
     * @return
     */
	RunningTeam getTeamOfPlayer(Player player);

    /**
     * @param player
     * @return
     */
	boolean isPlayerInAnyTeam(Player player);

    /**
     * @param player
     * @param team
     * @return
     */
	boolean isPlayerInTeam(Player player, RunningTeam team);

    /**
     * @param location
     * @return
     */
	boolean isLocationInArena(Location location);

    /**
     * @param location
     * @return
     */
	boolean isBlockAddedDuringGame(Location location);

    /**
     * @return
     */
	List<SpecialItem> getActivedSpecialItems();

    /**
     * @param type
     * @return
     */
	List<SpecialItem> getActivedSpecialItems(Class<? extends SpecialItem> type);

    /**
     * @param team
     * @return
     */
	List<SpecialItem> getActivedSpecialItemsOfTeam(Team team);

    /**
     * @param team
     * @param type
     * @return
     */
	List<SpecialItem> getActivedSpecialItemsOfTeam(Team team, Class<? extends SpecialItem> type);

    /**
     * @param team
     * @return
     */
	SpecialItem getFirstActivedSpecialItemOfTeam(Team team);

    /**
     * @param team
     * @param type
     * @return
     */
	SpecialItem getFirstActivedSpecialItemOfTeam(Team team, Class<? extends SpecialItem> type);

    /**
     * @param player
     * @return
     */
	List<SpecialItem> getActivedSpecialItemsOfPlayer(Player player);

    /**
     * @param player
     * @param type
     * @return
     */
	List<SpecialItem> getActivedSpecialItemsOfPlayer(Player player, Class<? extends SpecialItem> type);

    /**
     * @param player
     * @return
     */
	SpecialItem getFirstActivedSpecialItemOfPlayer(Player player);

    /**
     * @param player
     * @param type
     * @return
     */
	SpecialItem getFirstActivedSpecialItemOfPlayer(Player player, Class<? extends SpecialItem> type);

    /**
     * @return
     */
    List<DelayFactory> getActiveDelays();

    /**
     * @param player
     * @return
     */
    List<DelayFactory> getActiveDelaysOfPlayer(Player player);

    /**
     * @param player
     * @param specialItem
     * @return
     */
    DelayFactory getActiveDelay(Player player, Class<? extends SpecialItem> specialItem);

    /**
     * @param delayFactory
     */
    void registerDelay(DelayFactory delayFactory);

    /**
     * @param delayFactory
     */
    void unregisterDelay(DelayFactory delayFactory);

    /**
     * @param player
     * @param specialItem
     * @return
     */
    boolean isDelayActive(Player player, Class<? extends SpecialItem> specialItem);

    /**
     * @param item
     */
	void registerSpecialItem(SpecialItem item);

    /**
     * @param item
     */
	void unregisterSpecialItem(SpecialItem item);

    /**
     * @param item
     * @return
     */
	boolean isRegisteredSpecialItem(SpecialItem item);

    /**
     * @return
     */
	List<ItemSpawner> getItemSpawners();

    /**
     * @return
     */
	Region getRegion();

    /**
     * @return
     */
	StatusBar getStatusBar();

    // LOBBY

    /**
     * @return
     */
	World getLobbyWorld();

    /**
     * @return
     */
	Location getLobbySpawn();

    /**
     * @return
     */
	int getLobbyCountdown();

    /**
     * @return
     */
	int countTeamChests();

    /**
     * @param team
     * @return
     */
	int countTeamChests(RunningTeam team);

    /**
     * @param location
     * @return
     */
	RunningTeam getTeamOfChest(Location location);

    /**
     * @param block
     * @return
     */
	RunningTeam getTeamOfChest(Block block);

    /**
     * @param entity
     * @return
     */
	boolean isEntityShop(Entity entity);

    /**
     * @return
     */
	boolean getBungeeEnabled();

    /**
     * @return
     */
	ArenaTime getArenaTime();

    /**
     * @return
     */
	WeatherType getArenaWeather();

    /**
     * @return
     */
	BarColor getLobbyBossBarColor();

    /**
     * @return
     */
	BarColor getGameBossBarColor();

    /**
     * @return
     */
    boolean isProtectionActive(Player player);

    int getPostGameWaiting();

    default boolean hasCustomPrefix() {
        return getCustomPrefix() != null;
    }

    String getCustomPrefix();

    /**
     * Returns configuration container for this game
     *
     * @return game's configuration container
     * @since 0.3.0
     */
    ConfigurationContainer getConfigurationContainer();

    /**
     * Checks if game is in edit mode
     *
     * @return true if game is in edit mode
     * @since 0.3.0
     */
    boolean isInEditMode();

    /**
     * This methods allows you to save the arena to config (useful when using custom config options)
     *
     * @since 0.3.0
     */
    void saveToConfig();

    @Deprecated
    default InGameConfigBooleanConstants getCompassEnabled() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.COMPASS, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedCompassEnabled() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.COMPASS, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getJoinRandomTeamAfterLobby() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.JOIN_RANDOM_TEAM_AFTER_LOBBY, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedJoinRandomTeamAfterLobby() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.JOIN_RANDOM_TEAM_AFTER_LOBBY, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getJoinRandomTeamOnJoin() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.JOIN_RANDOM_TEAM_ON_JOIN, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedJoinRandomTeamOnJoin() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.JOIN_RANDOM_TEAM_ON_JOIN, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getAddWoolToInventoryOnJoin() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.ADD_WOOL_TO_INVENTORY_ON_JOIN, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedAddWoolToInventoryOnJoin() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.ADD_WOOL_TO_INVENTORY_ON_JOIN, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getPreventKillingVillagers() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.PROTECT_SHOP, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedPreventKillingVillagers() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.PROTECT_SHOP, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getPlayerDrops() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.PLAYER_DROPS, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedPlayerDrops() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.PLAYER_DROPS, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getFriendlyfire() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.FRIENDLY_FIRE, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedFriendlyfire() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.FRIENDLY_FIRE, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getColoredLeatherByTeamInLobby() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.COLORED_LEATHER_BY_TEAM_IN_LOBBY, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedColoredLeatherByTeamInLobby() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.COLORED_LEATHER_BY_TEAM_IN_LOBBY, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getKeepInventory() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.KEEP_INVENTORY, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedKeepInventory() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.KEEP_INVENTORY, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getKeepArmor() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.KEEP_ARMOR, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedKeepArmor() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.KEEP_ARMOR, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getCrafting() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.CRAFTING, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedCrafting() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.CRAFTING, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getLobbyBossbar() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.LOBBY_BOSSBAR, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedLobbyBossbar() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.LOBBY_BOSSBAR, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getGameBossbar() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.GAME_BOSSBAR, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedGameBossbar() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.GAME_BOSSBAR, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getScoreboard() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.GAME_SCOREBOARD, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedScoreaboard() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.GAME_SCOREBOARD, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getLobbyScoreboard() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.LOBBY_SCOREBOARD, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedLobbyScoreaboard() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.LOBBY_SCOREBOARD, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getPreventSpawningMobs() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.PREVENT_SPAWNING_MOBS, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedPreventSpawningMobs() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.PREVENT_SPAWNING_MOBS, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getSpawnerHolograms() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.SPAWNER_HOLOGRAMS, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedSpawnerHolograms() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.SPAWNER_HOLOGRAMS, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getSpawnerDisableMerge() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.SPAWNER_DISABLE_MERGE, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedSpawnerDisableMerge() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.SPAWNER_DISABLE_MERGE, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getGameStartItems() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.ENABLE_GAME_START_ITEMS, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedGameStartItems() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.ENABLE_GAME_START_ITEMS, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getPlayerRespawnItems() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.ENABLE_PLAYER_RESPAWN_ITEMS, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedPlayerRespawnItems() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.ENABLE_PLAYER_RESPAWN_ITEMS, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getSpawnerHologramsCountdown() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedSpawnerHologramsCountdown() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getDamageWhenPlayerIsNotInArena() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedDamageWhenPlayerIsNotInArena() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getRemoveUnusedTargetBlocks() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.REMOVE_UNUSED_TARGET_BLOCKS, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedRemoveUnusedTargetBlocks() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.REMOVE_UNUSED_TARGET_BLOCKS, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getAllowBlockFalling() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.BLOCK_FALLING, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedAllowBlockFalling() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.BLOCK_FALLING, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getHoloAboveBed() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.HOLOGRAMS_ABOVE_BEDS, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedHoloAboveBed() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.HOLOGRAMS_ABOVE_BEDS, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getSpectatorJoin() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.SPECTATOR_JOIN, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedSpectatorJoin() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.SPECTATOR_JOIN, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getStopTeamSpawnersOnDie() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedStopTeamSpawnersOnDie() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getAnchorAutoFill() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.ANCHOR_AUTO_FILL, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedAnchorAutoFill() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.ANCHOR_AUTO_FILL, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getAnchorDecreasing() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.ANCHOR_DECREASING, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedAnchorDecreasing() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.ANCHOR_DECREASING, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getCakeTargetBlockEating() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.CAKE_TARGET_BLOCK_EATING, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedCakeTargetBlockEating() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.CAKE_TARGET_BLOCK_EATING, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }

    @Deprecated
    default InGameConfigBooleanConstants getTargetBlockExplosions() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.TARGET_BLOCK_EXPLOSIONS, Boolean.class);
        if (opt.isPresent() && opt.get().isSet()) {
            return InGameConfigBooleanConstants.valueOf(opt.get().get().toString().toUpperCase());
        }
        return InGameConfigBooleanConstants.INHERIT;
    }

    @Deprecated
    default boolean getOriginalOrInheritedTargetBlockExplosions() {
        Optional<Configuration<Boolean>> opt = getConfigurationContainer().get(ConfigurationContainer.TARGET_BLOCK_EXPLOSIONS, Boolean.class);
        if (opt.isPresent()) {
            return opt.get().get();
        }
        return false;
    }
}
