package misat11.bw.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import misat11.bw.Main;
import misat11.bw.game.Game;

public class SignManager {

	private final FileConfiguration config;
	private final File configFile;
	private final HashMap<Location, GameSign> signs = new HashMap<>();
	private boolean modify = false;

	public SignManager(FileConfiguration config, File configFile) {
		this.config = config;
		this.configFile = configFile;

		List<Map<String, Object>> conf = (List<Map<String, Object>>) config.getList("sign");
		for (Map<String, Object> c : conf) {
			String gameName = (String) c.get("game");
			Location loc = (Location) c.get("location");
			signs.put(loc, new GameSign(loc, gameName));
		}
	}

	public boolean isSignRegistered(Location location) {
		return signs.containsKey(location);
	}

	public void unregisterSign(Location location) {
		if (signs.containsKey(location)) {
			signs.remove(location);
			modify = true;
		}
	}

	public boolean registerSign(Location location, String game) {
		if (Main.isGameExists(game) || game.equalsIgnoreCase("leave")) {
			signs.put(location, new GameSign(location, game));
			modify = true;
			if (Main.isGameExists(game)) {
				new BukkitRunnable() {
					public void run() {
						Main.getGame(game).updateSigns();
					}
				}.runTask(Main.getInstance());
			}
			return true;
		}
		return false;
	}

	public GameSign getSign(Location location) {
		return signs.get(location);
	}
	
	public List<GameSign> getSignsForGame(Game game){
		List<GameSign> list = new ArrayList<>();
		for (GameSign sign : signs.values()) {
			if (game == sign.getGame()) {
				list.add(sign);
			}
		}
		return list;
	}

	public void save() {
		save(false);
	}

	public void save(boolean force) {
		if (modify || force) {
			List<Map<String, Object>> list = new ArrayList<>();
			for (GameSign sign : signs.values()) {
				Map<String, Object> map = new HashMap<>();
				map.put("location", sign.getLocation());
				map.put("game", sign.getGameName());
				list.add(map);
			}

			config.set("sign", list);

			try {
				config.save(configFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
