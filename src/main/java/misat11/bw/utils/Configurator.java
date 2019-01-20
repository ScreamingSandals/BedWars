package misat11.bw.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

		AtomicBoolean modify = new AtomicBoolean(false);
		checkOrSetConfig(modify, "locale", "en");
		checkOrSetConfig(modify, "allow-crafting", false);
		checkOrSetConfig(modify, "keep-inventory-on-death", false);
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
		checkOrSetConfig(modify, "vault.enable", true);
		checkOrSetConfig(modify, "vault.reward.kill", 5);
		checkOrSetConfig(modify, "vault.reward.win", 20);
		checkOrSetConfig(modify, "spawners.bronze", 1);
		checkOrSetConfig(modify, "spawners.iron", 10);
		checkOrSetConfig(modify, "spawners.gold", 20);
		checkOrSetConfig(modify, "version", 1);
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
