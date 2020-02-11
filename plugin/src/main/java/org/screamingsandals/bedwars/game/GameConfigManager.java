package org.screamingsandals.bedwars.game;

import java.util.HashMap;
import java.util.Map;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.ConfigVariables;

import static org.screamingsandals.bedwars.api.game.ConfigVariables.*;

public class GameConfigManager implements org.screamingsandals.bedwars.api.game.GameConfigManager {
	private static final Map<String, String> SAVE_KEYS = new HashMap<>();
	private static final Map<String, String> CONFIG_YML_KEYS = new HashMap<>();

	private final Map<String, Boolean> gameChanges = new HashMap<>();
	private final Game game;

	static {
		/*
		 * Parameters: first internal code key (defined in ConfigVariables),
		 * second in arena.yml, without path, just constant name (don't change it please!!),
		 * third is config.yml key with full path (if is not defined, save key is used)
		 */
		register(TEAM_SELECTOR, "compass-enabled");
		register(AUTOBALANCE_ON_GAME_START, "join-randomly-after-lobby-timeout");
		register(AUTOBALANCE_ON_JOIN, "join-randomly-on-lobby-join");
		register(SHOW_TEAM_COLOR_IN_INVENTORY, "add-wool-to-inventory-on-join");
		register(PROTECT_STORES, "prevent-killing-villagers");
		register(PLAYER_DROPS, "player-drops");
		register(FRIENDLYFIRE, "friendlyfire");
		register(COLORED_ARMOR_IN_LOBBY, "in-lobby-colored-leather-by-team");
		register(KEEP_INVENTORY, "keep-inventory-on-death");
		register(CRAFTING, "allow-crafting");
		register(LOBBY_BOSSBAR, "lobbybossbar", "bossbar.lobby.enable");
		register(GAME_BOSSBAR, "bossbar", "bossbar.game.enable");
		register(GAME_SCOREBOARD, "scoreboard", "scoreboard.enable");
		register(LOBBY_SCOREBOARD, "lobbyscoreboard", "lobby-scoreboard.enabled");
		register(PREVENT_SPAWNING_MOBS, "prevent-spawning-mobs");
		register(HOLOGRAMS_FOR_SPAWNERS, "spawner-holograms");
		register(DISABLE_SPAWNER_MERGING, "spawner-disable-merge");
		register(GIVE_ITEMS_ON_GAME_START, "game-start-items");
		register(GIVE_ITEMS_ON_PLAYER_RESPAWN, "player-respawn-items");
		register(COUNTDOWN_HOLOGRAMS_FOR_SPAWNERS, "spawner-holograms-countdown");
		register(DAMAGE_DESERTERS, "damage-when-player-is-not-in-arena");
		register(REMOVE_UNUSED_TARGET_BLOCKS, "remove-unused-target-blocks");
		register(BLOCK_FALLING, "allow-block-falling");
		register(HOLOGRAMS_FOR_BEDS, "holo-above-bed");
		register(SPECTATOR_JOIN_DURING_GAME, "allow-spectator-join");
		register(DISABLE_SPAWNERS_OF_DEAD_TEAMS, "stop-team-spawners-on-die");
	}

	public GameConfigManager(Game game) {
		this.game = game;
	}

	private static void register(String internalKey, String saveKey) {
		register(internalKey, saveKey, saveKey);
	}

	private static void register(String internalKey, String saveKey, String configYmlKey) {
		SAVE_KEYS.put(internalKey, saveKey);
		CONFIG_YML_KEYS.put(internalKey, configYmlKey);
	}
	
	private static void register(ConfigVariables internalKey, String saveKey) {
		register(internalKey, saveKey, saveKey);
	}
	
	private static void register(ConfigVariables internalKey, String saveKey, String configYmlKey) {
		register(transform(internalKey), saveKey, configYmlKey);
	}
	
	public static String transform(ConfigVariables internalKey) {
		return internalKey.name().toLowerCase().replaceAll("_", "");
	}

	@Override
	public boolean get(String key) {
		return gameChanges.containsKey(key) ? gameChanges.get(key)
			: Main.getMainConfig().getBoolean(CONFIG_YML_KEYS.get(key));
	}

	@Override
	public boolean isChanged(String key) {
		return gameChanges.containsKey(key);
	}

	@Override
	public boolean set(String key, boolean value) {
		if (!game.isActivated()) {
			gameChanges.put(key, value);
			return true;
		}
		return false;
	}

	@Override
	public boolean reset(String key) {
		if (!game.isActivated() && gameChanges.containsKey(key)) {
			gameChanges.remove(key);
			return true;
		}
		return false;
	}
	
	public Map<String, Boolean> changesMap() {
		return gameChanges;
	}

	@Override
	public boolean get(ConfigVariables key) {
		return get(transform(key));
	}

	@Override
	public boolean isChanged(ConfigVariables key) {
		return isChanged(transform(key));
	}

	@Override
	public boolean reset(ConfigVariables key) {
		return reset(transform(key));
	}

	@Override
	public boolean set(ConfigVariables key, boolean value) {
		return set(transform(key), value);
	}

}
