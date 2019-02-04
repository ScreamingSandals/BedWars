package misat11.bw.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import misat11.bw.Main;

public class Configurator {

	public File configf, shopconfigf, signconfigf;
	public FileConfiguration config, shopconfig, signconfig;
	
	public final File datafolder;
	public final Main main;
	
	public Configurator(Main main) {
		this.datafolder = main.getDataFolder();
		this.main = main;
	}
	
	private void configMigration() {
		if (config.getInt("version") < 2) {
			main.getLogger().info("Migrating config from version 1 to version 2 ...");
			if (Main.isLegacy()) {
				config.set("resources.bronze.material", "CLAY_BRICK");
			} else {
				config.set("resources.bronze.material", "BRICK");
			}
			config.set("resources.bronze.interval", config.getInt("spawners.bronze", 1));
			config.set("resources.bronze.name", "Bronze");
			config.set("resources.bronze.translate", "resource_bronze");
			config.set("resources.bronze.color", "DARK_RED");
			config.set("resources.bronze.spread", 1.0);

			config.set("resources.iron.material", "IRON_INGOT");
			config.set("resources.iron.interval", config.getInt("spawners.iron", 10));
			config.set("resources.iron.name", "Iron");
			config.set("resources.iron.translate", "resource_iron");
			config.set("resources.iron.color", "GRAY");
			config.set("resources.iron.spread", 1.0);

			config.set("resources.gold.material", "GOLD_INGOT");
			config.set("resources.gold.interval", config.getInt("spawners.gold", 20));
			config.set("resources.gold.name", "Gold");
			config.set("resources.gold.translate", "resource_gold");
			config.set("resources.gold.color", "GOLD");
			config.set("resources.gold.spread", 1.0);
			
			config.set("version", 2);
			
			config.set("spawners", null);
			
			try {
				config.save(configf);
			} catch (IOException e) {
				e.printStackTrace();
			}
			main.getLogger().info("Config successfully migrated from version 1 to version 2!");
		}
	}


	public void createFiles() {

		configf = new File(datafolder, "config.yml");
		shopconfigf = new File(datafolder, "shop.yml");
		signconfigf = new File(datafolder, "sign.yml");

		if (!configf.exists()) {
			if (Main.isLegacy()) {
				main.saveResource("config_legacy.yml", false);
				new File(datafolder, "config_legacy.yml").renameTo(configf);
			} else {
				main.saveResource("config.yml", false);
			}
		}
		if (!shopconfigf.exists()) {
			if (Main.isLegacy()) {
				main.saveResource("shop_legacy.yml", false);
				new File(datafolder, "shop_legacy.yml").renameTo(shopconfigf);
			} else {
				main.saveResource("shop.yml", false);
			}
		}
		if (!signconfigf.exists()) {
			main.saveResource("sign.yml", false);
		}
		config = new YamlConfiguration();
		shopconfig = new YamlConfiguration();
		signconfig = new YamlConfiguration();
		try {
			config.load(configf);
			shopconfig.load(shopconfigf);
			signconfig.load(signconfigf);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		configMigration();

		AtomicBoolean modify = new AtomicBoolean(false);
		checkOrSetConfig(modify, "locale", "en");
		checkOrSetConfig(modify, "allow-crafting", false);
		checkOrSetConfig(modify, "keep-inventory-on-death", false);
		checkOrSetConfig(modify, "in-lobby-colored-leather-by-team", true);
		checkOrSetConfig(modify, "jointeam-entity-show-name", true);
		checkOrSetConfig(modify, "friendlyfire", false);
		checkOrSetConfig(modify, "player-drops", true);
		checkOrSetConfig(modify, "join-randomly-after-lobby-timeout", false);
		checkOrSetConfig(modify, "spectator-gm3", false);
		checkOrSetConfig(modify, "allowed-commands", new ArrayList<>());
		checkOrSetConfig(modify, "farmBlocks.enable", true);
		checkOrSetConfig(modify, "farmBlocks.blocks", new ArrayList<>());
		checkOrSetConfig(modify, "scoreboard.enable", true);
		checkOrSetConfig(modify, "scoreboard.title", "§a%game%§r - %time%");
		checkOrSetConfig(modify, "scoreboard.bedLost", "§c\u2718");
		checkOrSetConfig(modify, "scoreboard.bedExists", "§a\u2714");
		checkOrSetConfig(modify, "scoreboard.teamTitle", "%bed%%color%%team%");
		checkOrSetConfig(modify, "title.fadeIn", 0);
		checkOrSetConfig(modify, "title.stay", 20);
		checkOrSetConfig(modify, "title.fadeOut", 0);
		checkOrSetConfig(modify, "items.jointeam", "COMPASS");
		checkOrSetConfig(modify, "items.leavegame", "SLIME_BALL");
		checkOrSetConfig(modify, "items.shopback", "BARRIER");
		checkOrSetConfig(modify, "vault.enable", true);
		checkOrSetConfig(modify, "vault.reward.kill", 5);
		checkOrSetConfig(modify, "vault.reward.win", 20);
		checkOrSetConfig(modify, "resources", new ArrayList<>());
		checkOrSetConfig(modify, "sounds.on_bed_destroyed", "ENTITY_ENDER_DRAGON_GROWL");
		checkOrSetConfig(modify, "sounds.on_countdown", "UI_BUTTON_CLICK");
		checkOrSetConfig(modify, "sounds.on_game_start", "ENTITY_PLAYER_LEVELUP");
		checkOrSetConfig(modify, "sounds.on_team_kill", "ENTITY_PLAYER_LEVELUP");
		checkOrSetConfig(modify, "game-effects.end", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.start", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.kill", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.teamkill", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.lobbyjoin", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.lobbyleave", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.respawn", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.beddestroy", new HashMap<String, Object>());
		checkOrSetConfig(modify, "lobby-scoreboard.enabled", true);
		checkOrSetConfig(modify, "lobby-scoreboard.title", "§eBEDWARS");
		checkOrSetConfig(modify, "lobby-scoreboard.content", Arrays.asList(" ", "§fMap: §2%arena%", "§fPlayers: §2%players%§f/§2%maxplayers%", " ", "§fWaiting ...", " "));
		checkOrSetConfig(modify, "version", 2);
		if (modify.get()) {
			try {
				config.save(configf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void checkOrSetConfig(AtomicBoolean modify, String path, Object value) {
		checkOrSet(modify, this.config, path, value);
	}
	
	private static void checkOrSet(AtomicBoolean modify, FileConfiguration config, String path, Object value) {
		if (!config.isSet(path)) {
			config.set(path, value);
			modify.set(true);
		}
	}
}
