package misat11.bw.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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


	public void createFiles() {

		configf = new File(datafolder, "config.yml");
		shopconfigf = new File(datafolder, "shop.yml");
		signconfigf = new File(datafolder, "sign.yml");

		if (!configf.exists()) {
			configf.getParentFile().mkdirs();
			main.saveResource("config.yml", false);
		}
		if (!shopconfigf.exists()) {
			shopconfigf.getParentFile().mkdirs();
			main.saveResource("shop.yml", false);
		}
		if (!signconfigf.exists()) {
			signconfigf.getParentFile().mkdirs();
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

		boolean modify = checkOrSetConfig("locale", "en");
		modify = modify || checkOrSetConfig("allow-crafting", false);
		modify = modify || checkOrSetConfig("keep-inventory-on-death", false);
		modify = modify || checkOrSetConfig("allowed-commands", new ArrayList<>());
		modify = modify || checkOrSetConfig("farmBlocks.enable", true);
		modify = modify || checkOrSetConfig("farmBlocks.blocks", new ArrayList<>());
		modify = modify || checkOrSetConfig("scoreboard.enable", true);
		modify = modify || checkOrSetConfig("scoreboard.title", "§a%game%§r - %time%");
		modify = modify || checkOrSetConfig("scoreboard.bedLost", "§c\u2718");
		modify = modify || checkOrSetConfig("scoreboard.bedExists", "§a\u2714");
		modify = modify || checkOrSetConfig("scoreboard.teamTitle", "%bed%%color%%team%");
		modify = modify || checkOrSetConfig("title.fadeIn", 0);
		modify = modify || checkOrSetConfig("title.stay", 20);
		modify = modify || checkOrSetConfig("title.fadeOut", 0);
		modify = modify || checkOrSetConfig("items.jointeam", "COMPASS");
		modify = modify || checkOrSetConfig("items.leavegame", "SLIME_BALL");
		modify = modify || checkOrSetConfig("vault.enable", true);
		modify = modify || checkOrSetConfig("vault.reward.kill", 5);
		modify = modify || checkOrSetConfig("vault.reward.win", 20);
		modify = modify || checkOrSetConfig("spawners.bronze", 1);
		modify = modify || checkOrSetConfig("spawners.iron", 10);
		modify = modify || checkOrSetConfig("spawners.gold", 20);
		modify = modify || checkOrSetConfig("version", 1);
		if (modify) {
			try {
				config.save(configf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean checkOrSetConfig(String path, Object value) {
		return checkOrSet(this.config, path, value);
	}
	
	private boolean checkOrSetShop(String path, Object value) {
		return checkOrSet(this.shopconfig, path, value);
	}
	
	private static boolean checkOrSet(FileConfiguration config, String path, Object value) {
		if (!config.isSet(path)) {
			config.set(path, value);
			return true;
		}
		return false;
	}
}
