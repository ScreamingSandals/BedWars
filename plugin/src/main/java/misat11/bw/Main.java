package misat11.bw;

import static misat11.lib.lang.I18n.i18n;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import misat11.bw.api.BedwarsAPI;
import misat11.bw.api.GameStatus;
import misat11.bw.api.GameStore;
import misat11.bw.commands.BwCommand;
import misat11.bw.database.DatabaseManager;
import misat11.bw.game.Game;
import misat11.bw.game.GamePlayer;
import misat11.bw.game.ItemSpawnerType;
import misat11.bw.holograms.HolographicDisplaysInteraction;
import misat11.bw.holograms.IHologramInteraction;
import misat11.bw.listener.LuckyBlockAddonListener;
import misat11.bw.listener.Player112Listener;
import misat11.bw.listener.Player19Listener;
import misat11.bw.listener.PlayerListener;
import misat11.bw.listener.SignListener;
import misat11.bw.listener.TrapListener;
import misat11.bw.listener.VillagerListener;
import misat11.bw.listener.WarpPowderListener;
import misat11.bw.listener.WorldListener;
import misat11.bw.placeholderapi.BedwarsExpansion;
import misat11.bw.statistics.PlayerStatisticManager;
import misat11.bw.utils.Configurator;
import misat11.bw.utils.GameSign;
import misat11.bw.utils.ShopMenu;
import misat11.bw.utils.SignManager;
import misat11.lib.lang.I18n;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;

public class Main extends JavaPlugin implements BedwarsAPI {
	private static Main instance;
	private String version, nmsVersion;
	private boolean isSpigot, snapshot, isVault, isLegacy, isNMS;
	private Economy econ = null;
	private HashMap<String, Game> games = new HashMap<String, Game>();
	private HashMap<Player, GamePlayer> playersInGame = new HashMap<Player, GamePlayer>();
	private HashMap<Entity, Game> entitiesInGame = new HashMap<Entity, Game>();
	private Configurator configurator;
	private ShopMenu menu;
	private SignManager signManager;
	private HashMap<String, ItemSpawnerType> spawnerTypes = new HashMap<String, ItemSpawnerType>();
	private DatabaseManager databaseManager;
	private PlayerStatisticManager playerStatisticsManager;
	private IHologramInteraction hologramInteraction;

	public static Main getInstance() {
		return instance;
	}

	public static Configurator getConfigurator() {
		return instance.configurator;
	}

	public static String getVersion() {
		return instance.version;
	}

	public static boolean isSnapshot() {
		return instance.snapshot;
	}

	public static boolean isSpigot() {
		return instance.isSpigot;
	}

	public static boolean isVault() {
		return instance.isVault;
	}

	public static boolean isLegacy() {
		return instance.isLegacy;
	}

	public static boolean isNMS() {
		return instance.isNMS;
	}

	public static String getNMSVersion() {
		return isNMS() ? instance.nmsVersion : null;
	}

	public static void depositPlayer(Player player, double coins) {
		if (isVault() && instance.configurator.config.getBoolean("vault.enable")) {
			EconomyResponse response = instance.econ.depositPlayer(player, coins);
			if (response.transactionSuccess()) {
				player.sendMessage(i18n("vault_deposite").replace("%coins%", Double.toString(coins)).replace(
						"%currency%",
						(coins == 1 ? instance.econ.currencyNameSingular() : instance.econ.currencyNamePlural())));
			}
		}
	}

	public static int getVaultKillReward() {
		return instance.configurator.config.getInt("vault.reward.kill");
	}

	public static int getVaultWinReward() {
		return instance.configurator.config.getInt("vault.reward.win");
	}

	public static Game getGame(String string) {
		return instance.games.get(string);
	}

	public static boolean isGameExists(String string) {
		return instance.games.containsKey(string);
	}

	public static void addGame(Game game) {
		instance.games.put(game.getName(), game);
	}

	public static void removeGame(Game game) {
		instance.games.remove(game.getName());
	}

	public static Game getInGameEntity(Entity entity) {
		return instance.entitiesInGame.containsKey(entity) ? instance.entitiesInGame.get(entity) : null;
	}

	public static void registerGameEntity(Entity entity, Game game) {
		instance.entitiesInGame.put(entity, game);
	}

	public static void unregisterGameEntity(Entity entity) {
		if (instance.entitiesInGame.containsKey(entity))
			instance.entitiesInGame.remove(entity);
	}

	public static boolean isPlayerInGame(Player player) {
		if (instance.playersInGame.containsKey(player))
			if (instance.playersInGame.get(player).isInGame())
				return true;
		return false;
	}

	public static GamePlayer getPlayerGameProfile(Player player) {
		if (instance.playersInGame.containsKey(player))
			return instance.playersInGame.get(player);
		GamePlayer gPlayer = new GamePlayer(player);
		instance.playersInGame.put(player, gPlayer);
		return gPlayer;
	}

	public static void unloadPlayerGameProfile(Player player) {
		if (instance.playersInGame.containsKey(player)) {
			instance.playersInGame.get(player).changeGame(null);
			instance.playersInGame.remove(player);
		}
	}

	public static boolean isPlayerGameProfileRegistered(Player player) {
		return instance.playersInGame.containsKey(player);
	}

	public static void sendGameListInfo(Player player) {
		for (Game game : instance.games.values()) {
			player.sendMessage((game.getStatus() == GameStatus.DISABLED ? "§c" : "§a") + game.getName() + "§f "
					+ game.countPlayers());
		}
	}

	public static void openStore(Player player, GameStore store) {
		instance.menu.show(player, store);
	}

	public static boolean isFarmBlock(Material mat) {
		if (instance.configurator.config.getBoolean("farmBlocks.enable")) {
			List<String> list = (List<String>) instance.configurator.config.getList("farmBlocks.blocks");
			if (list.contains(mat.name())) {
				return true;
			}
		}
		return false;
	}

	public static boolean isCommandAllowedInGame(String commandPref) {
		if ("/bw".equals(commandPref) || "/bedwars".equals(commandPref)) {
			return true;
		}
		List<String> commands = instance.configurator.config.getStringList("allowed-commands");
		for (String comm : commands) {
			if (!comm.startsWith("/")) {
				comm = "/" + comm;
			}
			if (comm.equals(commandPref)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isSignRegistered(Location location) {
		return instance.signManager.isSignRegistered(location);
	}

	public static void unregisterSign(Location location) {
		instance.signManager.unregisterSign(location);
	}

	public static boolean registerSign(Location location, String game) {
		return instance.signManager.registerSign(location, game);
	}

	public static GameSign getSign(Location location) {
		return instance.signManager.getSign(location);
	}

	public static List<GameSign> getSignsForGame(Game game) {
		return instance.signManager.getSignsForGame(game);
	}

	public static ItemSpawnerType getSpawnerType(String key) {
		return instance.spawnerTypes.get(key);
	}

	public static List<String> getAllSpawnerTypes() {
		return new ArrayList<String>(instance.spawnerTypes.keySet());
	}

	public static List<String> getGameNames() {
		List<String> list = new ArrayList<String>();
		for (Game game : instance.games.values()) {
			list.add(game.getName());
		}
		return list;
	}

	public static DatabaseManager getDatabaseManager() {
		return instance.databaseManager;
	}

	public static PlayerStatisticManager getPlayerStatisticsManager() {
		return instance.playerStatisticsManager;
	}

	public static boolean isPlayerStatisticsEnabled() {
		return instance.configurator.config.getBoolean("statistics.enabled");
	}

	public static boolean isHologramsEnabled() {
		return instance.configurator.config.getBoolean("holograms.enabled") && instance.hologramInteraction != null;
	}

	public static IHologramInteraction getHologramInteraction() {
		return instance.hologramInteraction;
	}

	public void onEnable() {
		instance = this;
		version = this.getDescription().getVersion();
		snapshot = version.toLowerCase().contains("pre");

		try {
			Class.forName("org.bukkit.craftbukkit.Main");
			isNMS = true;
		} catch (ClassNotFoundException e) {
			isNMS = false;
		}

		if (isNMS) {
			String packName = Bukkit.getServer().getClass().getPackage().getName();
			nmsVersion = packName.substring(packName.lastIndexOf('.') + 1);
		}

		try {
			Package spigotPackage = Package.getPackage("org.spigotmc");
			isSpigot = (spigotPackage != null);
		} catch (Exception e) {
			isSpigot = false;
		}

		if (!getServer().getPluginManager().isPluginEnabled("Vault")) {
			isVault = false;
		} else {
			setupEconomy();
			isVault = true;
		}

		String[] bukkitVersion = Bukkit.getBukkitVersion().split("-")[0].split("\\.");
		int versionNumber = 0;
		for (int i = 0; i < 2; i++) {
			versionNumber += Integer.parseInt(bukkitVersion[i]) * (i == 0 ? 100 : 1);
		}

		isLegacy = versionNumber < 113;

		configurator = new Configurator(this);

		configurator.createFiles();

		I18n.load(this, configurator.config.getString("locale"));

		signManager = new SignManager(configurator.signconfig, configurator.signconfigf);

		databaseManager = new DatabaseManager(configurator.config.getString("database.host"),
				configurator.config.getInt("database.port"), configurator.config.getString("database.user"),
				configurator.config.getString("database.password"), configurator.config.getString("database.db"),
				configurator.config.getString("database.table-prefix", "bw_"));

		if (isPlayerStatisticsEnabled()) {
			playerStatisticsManager = new PlayerStatisticManager();
			playerStatisticsManager.initialize();
		}

		try {
			if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
				new BedwarsExpansion().register();
			}

			if (configurator.config.getBoolean("holograms.enabled")) {
				// Holographic Displays
				if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
					hologramInteraction = new HolographicDisplaysInteraction();
				}
				
				if (hologramInteraction != null) {
					hologramInteraction.loadHolograms();
				}
			}
		} catch (Throwable t) {

		}

		BwCommand cmd = new BwCommand();
		getCommand("bw").setExecutor(cmd);
		getCommand("bw").setTabCompleter(cmd);
		getServer().getPluginManager().registerEvents(new PlayerListener(), this);
		if (versionNumber >= 109) {
			getServer().getPluginManager().registerEvents(new Player19Listener(), this);
		}
		if (versionNumber >= 112) {
			getServer().getPluginManager().registerEvents(new Player112Listener(), this);
		}
		getServer().getPluginManager().registerEvents(new VillagerListener(), this);
		getServer().getPluginManager().registerEvents(new SignListener(), this);
		getServer().getPluginManager().registerEvents(new WorldListener(), this);
		getServer().getPluginManager().registerEvents(new WarpPowderListener(), this);
		getServer().getPluginManager().registerEvents(new LuckyBlockAddonListener(), this);
		getServer().getPluginManager().registerEvents(new TrapListener(), this);

		getServer().getServicesManager().register(BedwarsAPI.class, this, this, ServicePriority.Normal);

		for (String spawnerN : configurator.config.getConfigurationSection("resources").getKeys(false)) {

			String name = Main.getConfigurator().config.getString("resources." + spawnerN + ".name");
			String translate = Main.getConfigurator().config.getString("resources." + spawnerN + ".translate");
			int interval = Main.getConfigurator().config.getInt("resources." + spawnerN + ".interval", 1);
			double spread = Main.getConfigurator().config.getDouble("resources." + spawnerN + ".spread");
			int damage = Main.getConfigurator().config.getInt("resources." + spawnerN + ".damage");
			String materialName = Main.getConfigurator().config.getString("resources." + spawnerN + ".material", "AIR");
			String colorName = Main.getConfigurator().config.getString("resources." + spawnerN + ".color", "WHITE");

			Material material = Material.valueOf(materialName);
			if (material == Material.AIR || material == null) {
				continue;
			}

			ChatColor color = ChatColor.valueOf(colorName);

			spawnerTypes.put(spawnerN.toLowerCase(), new ItemSpawnerType(spawnerN.toLowerCase(), name, translate,
					spread, material, color, interval, damage));
		}

		menu = new ShopMenu();

		Bukkit.getLogger().info("********************");
		Bukkit.getLogger().info("*     Bed Wars     *");
		Bukkit.getLogger().info("*    by Misat11    *");
		Bukkit.getLogger().info("*                  *");
		if (version.length() == 10) {
			Bukkit.getLogger().info("*                  *");
			Bukkit.getLogger().info("*    V" + version + "   *");
		} else {
			Bukkit.getLogger().info("*      V" + version + "      *");
		}
		Bukkit.getLogger().info("*                  *");
		if (snapshot == true) {
			Bukkit.getLogger().info("* SNAPSHOT VERSION *");
		} else {
			Bukkit.getLogger().info("*  STABLE VERSION  *");
		}
		Bukkit.getLogger().info("*                  *");

		if (isVault == true) {
			Bukkit.getLogger().info("*                  *");
			Bukkit.getLogger().info("*   Vault hooked   *");
			Bukkit.getLogger().info("*                  *");
		}

		if (isSpigot == false) {
			Bukkit.getLogger().info("*                  *");
			Bukkit.getLogger().info("*     WARNING:     *");
			Bukkit.getLogger().info("* You aren't using *");
			Bukkit.getLogger().info("*      Spigot      *");
			Bukkit.getLogger().info("*                  *");
			Bukkit.getLogger().info("* Please download! *");
			Bukkit.getLogger().info("*   spigotmc.org   *");
		}

		if (versionNumber < 109) {
			Bukkit.getLogger().info("*                  *");
			Bukkit.getLogger().info("*   You're using   *");
			Bukkit.getLogger().info("*       old        *");
			Bukkit.getLogger().info("*   game version   *");
			Bukkit.getLogger().info("*                  *");
			Bukkit.getLogger().info("*   We recommend   *");
			Bukkit.getLogger().info("*      to use      *");
			Bukkit.getLogger().info("*  1.9 and newer!  *");
			Bukkit.getLogger().info("*                  *");
		}

		Bukkit.getLogger().info("*                  *");
		Bukkit.getLogger().info("********************");

		File folder = new File(getDataFolder().toString(), "arenas");
		if (folder.exists()) {
			File[] listOfFiles = folder.listFiles();
			if (listOfFiles.length > 0) {
				for (int i = 0; i < listOfFiles.length; i++) {
					if (listOfFiles[i].isFile()) {
						Game.loadGame(listOfFiles[i]);
					}
				}
			}
		}

	}

	public void onDisable() {
		if (signManager != null) {
			signManager.save();
		}
		for (Game game : games.values()) {
			game.stop();
		}
		this.getServer().getServicesManager().unregisterAll(this);

		if (isHologramsEnabled() && hologramInteraction != null) {
			hologramInteraction.unloadHolograms();
		}
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}

		econ = rsp.getProvider();
		return econ != null;
	}

	@Override
	public List<misat11.bw.api.Game> getGames() {
		List<misat11.bw.api.Game> gms = new ArrayList<misat11.bw.api.Game>();
		for (Game game : games.values()) {
			gms.add(game);
		}
		return gms;
	}

	@Override
	public misat11.bw.api.Game getGameOfPlayer(Player player) {
		return isPlayerInGame(player) ? getPlayerGameProfile(player).getGame() : null;
	}

	@Override
	public boolean isGameWithNameExists(String name) {
		return games.containsKey(name);
	}

	@Override
	public misat11.bw.api.Game getGameByName(String name) {
		return games.get(name);
	}

	@Override
	public List<misat11.bw.api.ItemSpawnerType> getItemSpawnerTypes() {
		List<misat11.bw.api.ItemSpawnerType> list = new ArrayList<misat11.bw.api.ItemSpawnerType>();

		for (ItemSpawnerType type : spawnerTypes.values()) {
			list.add(type);
		}

		return list;
	}

	@Override
	public misat11.bw.api.ItemSpawnerType getItemSpawnerTypeByName(String name) {
		return spawnerTypes.get(name);
	}

	@Override
	public boolean isItemSpawnerTypeRegistered(String name) {
		return spawnerTypes.containsKey(name);
	}

	@Override
	public boolean isEntityInGame(Entity entity) {
		return entitiesInGame.containsKey(entity);
	}

	@Override
	public misat11.bw.api.Game getGameOfEntity(Entity entity) {
		return entitiesInGame.get(entity);
	}

	@Override
	public void registerEntityToGame(Entity entity, misat11.bw.api.Game game) {
		if (!(game instanceof Game)) {
			return;
		}
		entitiesInGame.put(entity, (Game) game);
	}

	@Override
	public void unregisterEntityFromGame(Entity entity) {
		if (entitiesInGame.containsKey(entity)) {
			entitiesInGame.remove(entity);
		}
	}

	@Override
	public boolean isPlayerPlayingAnyGame(Player player) {
		return isPlayerInGame(player);
	}
}
