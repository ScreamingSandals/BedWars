package org.screamingsandals.bedwars.api.old;

import org.screamingsandals.bedwars.api.InGameConfigBooleanConstants;
import org.screamingsandals.bedwars.api.game.ConfigVariables;
import org.screamingsandals.bedwars.api.game.GameConfigManager;


/* 
 * OLD Game METHODS (LET IT HERE FOR COMPATIBLE WITH LATEST 0.2.x API) 
 * */

@SuppressWarnings("deprecation")
public interface Game0xx {
	@Deprecated
	default InGameConfigBooleanConstants getCompassEnabled() {
		ConfigVariables variable = ConfigVariables.TEAM_SELECTOR;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedCompassEnabled() {
		return getConfigManager().get(ConfigVariables.TEAM_SELECTOR);
	}

	@Deprecated
	default InGameConfigBooleanConstants getJoinRandomTeamAfterLobby() {
		ConfigVariables variable = ConfigVariables.AUTOBALANCE_ON_GAME_START;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedJoinRandomTeamAfterLobby() {
		return getConfigManager().get(ConfigVariables.AUTOBALANCE_ON_GAME_START);
	}

	@Deprecated
	default InGameConfigBooleanConstants getJoinRandomTeamOnJoin() {
		ConfigVariables variable = ConfigVariables.AUTOBALANCE_ON_JOIN;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedJoinRandomTeamOnJoin() {
		return getConfigManager().get(ConfigVariables.AUTOBALANCE_ON_JOIN);
	}

	@Deprecated
	default InGameConfigBooleanConstants getAddWoolToInventoryOnJoin() {
		ConfigVariables variable = ConfigVariables.SHOW_TEAM_COLOR_IN_INVENTORY;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedAddWoolToInventoryOnJoin() {
		return getConfigManager().get(ConfigVariables.SHOW_TEAM_COLOR_IN_INVENTORY);
	}

	@Deprecated
	default InGameConfigBooleanConstants getPreventKillingVillagers() {
		ConfigVariables variable = ConfigVariables.PROTECT_STORES;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedPreventKillingVillagers() {
		return getConfigManager().get(ConfigVariables.PROTECT_STORES);
	}

	@Deprecated
	default InGameConfigBooleanConstants getPlayerDrops() {
		ConfigVariables variable = ConfigVariables.PLAYER_DROPS;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedPlayerDrops() {
		return getConfigManager().get(ConfigVariables.PLAYER_DROPS);
	}

	@Deprecated
	default InGameConfigBooleanConstants getFriendlyfire() {
		ConfigVariables variable = ConfigVariables.FRIENDLYFIRE;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedFriendlyfire() {
		return getConfigManager().get(ConfigVariables.FRIENDLYFIRE);
	}

	@Deprecated
	default InGameConfigBooleanConstants getColoredLeatherByTeamInLobby() {
		ConfigVariables variable = ConfigVariables.COLORED_ARMOR_IN_LOBBY;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedColoredLeatherByTeamInLobby() {
		return getConfigManager().get(ConfigVariables.COLORED_ARMOR_IN_LOBBY);
	}

	@Deprecated
	default InGameConfigBooleanConstants getKeepInventory() {
		ConfigVariables variable = ConfigVariables.KEEP_INVENTORY;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedKeepInventory() {
		return getConfigManager().get(ConfigVariables.KEEP_INVENTORY);
	}

	@Deprecated
	default InGameConfigBooleanConstants getCrafting() {
		ConfigVariables variable = ConfigVariables.CRAFTING;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedCrafting() {
		return getConfigManager().get(ConfigVariables.CRAFTING);
	}

	@Deprecated
	default InGameConfigBooleanConstants getLobbyBossbar() {
		ConfigVariables variable = ConfigVariables.LOBBY_BOSSBAR;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedLobbyBossbar() {
		return getConfigManager().get(ConfigVariables.LOBBY_BOSSBAR);
	}

	@Deprecated
	default InGameConfigBooleanConstants getGameBossbar() {
		ConfigVariables variable = ConfigVariables.GAME_BOSSBAR;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedGameBossbar() {
		return getConfigManager().get(ConfigVariables.GAME_BOSSBAR);
	}

	@Deprecated
	default InGameConfigBooleanConstants getScoreboard() {
		ConfigVariables variable = ConfigVariables.GAME_SCOREBOARD;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedScoreaboard() {
		return getConfigManager().get(ConfigVariables.GAME_SCOREBOARD);
	}

	@Deprecated
	default InGameConfigBooleanConstants getLobbyScoreboard() {
		ConfigVariables variable = ConfigVariables.LOBBY_SCOREBOARD;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedLobbyScoreaboard() {
		return getConfigManager().get(ConfigVariables.LOBBY_SCOREBOARD);
	}

	@Deprecated
	default InGameConfigBooleanConstants getPreventSpawningMobs() {
		ConfigVariables variable = ConfigVariables.PREVENT_SPAWNING_MOBS;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedPreventSpawningMobs() {
		return getConfigManager().get(ConfigVariables.PREVENT_SPAWNING_MOBS);
	}

	@Deprecated
	default InGameConfigBooleanConstants getSpawnerHolograms() {
		ConfigVariables variable = ConfigVariables.HOLOGRAMS_FOR_SPAWNERS;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedSpawnerHolograms() {
		return getConfigManager().get(ConfigVariables.HOLOGRAMS_FOR_SPAWNERS);
	}

	@Deprecated
	default InGameConfigBooleanConstants getSpawnerDisableMerge() {
		ConfigVariables variable = ConfigVariables.DISABLE_SPAWNER_MERGING;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedSpawnerDisableMerge() {
		return getConfigManager().get(ConfigVariables.DISABLE_SPAWNER_MERGING);
	}

	@Deprecated
	default InGameConfigBooleanConstants getGameStartItems() {
		ConfigVariables variable = ConfigVariables.GIVE_ITEMS_ON_GAME_START;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedGameStartItems() {
		return getConfigManager().get(ConfigVariables.GIVE_ITEMS_ON_GAME_START);
	}

	@Deprecated
	default InGameConfigBooleanConstants getPlayerRespawnItems() {
		ConfigVariables variable = ConfigVariables.GIVE_ITEMS_ON_PLAYER_RESPAWN;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedPlayerRespawnItems() {
		return getConfigManager().get(ConfigVariables.GIVE_ITEMS_ON_PLAYER_RESPAWN);
	}

	@Deprecated
	default InGameConfigBooleanConstants getSpawnerHologramsCountdown() {
		ConfigVariables variable = ConfigVariables.HOLOGRAMS_FOR_SPAWNERS;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedSpawnerHologramsCountdown() {
		return getConfigManager().get(ConfigVariables.HOLOGRAMS_FOR_SPAWNERS);
	}

	@Deprecated
	default InGameConfigBooleanConstants getDamageWhenPlayerIsNotInArena() {
		ConfigVariables variable = ConfigVariables.DAMAGE_DESERTERS;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedDamageWhenPlayerIsNotInArena() {
		return getConfigManager().get(ConfigVariables.DAMAGE_DESERTERS);
	}

	@Deprecated
	default InGameConfigBooleanConstants getRemoveUnusedTargetBlocks() {
		ConfigVariables variable = ConfigVariables.REMOVE_UNUSED_TARGET_BLOCKS;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedRemoveUnusedTargetBlocks() {
		return getConfigManager().get(ConfigVariables.REMOVE_UNUSED_TARGET_BLOCKS);
	}

	@Deprecated
	default InGameConfigBooleanConstants getAllowBlockFalling() {
		ConfigVariables variable = ConfigVariables.BLOCK_FALLING;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedAllowBlockFalling() {
		return getConfigManager().get(ConfigVariables.BLOCK_FALLING);
	}

	@Deprecated
	default InGameConfigBooleanConstants getHoloAboveBed() {
		ConfigVariables variable = ConfigVariables.HOLOGRAMS_FOR_BEDS;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedHoloAboveBed() {
		return getConfigManager().get(ConfigVariables.HOLOGRAMS_FOR_BEDS);
	}

	@Deprecated
	default InGameConfigBooleanConstants getSpectatorJoin() {
		ConfigVariables variable = ConfigVariables.SPECTATOR_JOIN_DURING_GAME;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedSpectatorJoin() {
		return getConfigManager().get(ConfigVariables.SPECTATOR_JOIN_DURING_GAME);
	}

	@Deprecated
	default InGameConfigBooleanConstants getStopTeamSpawnersOnDie() {
		ConfigVariables variable = ConfigVariables.DISABLE_SPAWNERS_OF_DEAD_TEAMS;
		return resolveFor0xx(variable);
	}

	@Deprecated
	default boolean getOriginalOrInheritedStopTeamSpawnersOnDie() {
		return getConfigManager().get(ConfigVariables.DISABLE_SPAWNERS_OF_DEAD_TEAMS);
	}

	@Deprecated
	default InGameConfigBooleanConstants resolveFor0xx(ConfigVariables variable) {
		if (getConfigManager().isChanged(variable)) {
			return getConfigManager().get(variable) ? InGameConfigBooleanConstants.TRUE
				: InGameConfigBooleanConstants.FALSE;
		}
		return InGameConfigBooleanConstants.INHERIT;
	}

	/* From new Game.java, needed here for translator */
	GameConfigManager getConfigManager();
}
