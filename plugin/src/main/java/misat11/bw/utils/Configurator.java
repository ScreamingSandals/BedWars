package misat11.bw.utils;

import misat11.bw.Main;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Configurator {

	public File configf, shopconfigf, signconfigf, recordconfigf;
	public FileConfiguration config, shopconfig, signconfig, recordconfig;

	public final File datafolder;
	public final Main main;

	public Configurator(Main main) {
		this.datafolder = main.getDataFolder();
		this.main = main;
	}

	/**
	 * Move this out of Configurator
	 */
	@Deprecated
	private void configMigration() {
		if (shopconfig.isSet("shop-items")) {
			main.getLogger().info("Migrating shop.yml from version 1 to version 2 ...");

			List<Map<String, Object>> newData = new ArrayList<>();

			Set<String> s = Main.getConfigurator().shopconfig.getConfigurationSection("shop-items").getKeys(false);

			for (String i : s) {
				ConfigurationSection category = Main.getConfigurator().shopconfig
						.getConfigurationSection("shop-items." + i);
				ItemStack categoryItem = new ItemStack(Material.valueOf(category.getString("item")), 1,
						(short) category.getInt("damage", 0));
				ItemMeta categoryItemMeta = categoryItem.getItemMeta();
				categoryItemMeta.setLore(category.getStringList("lore"));
				categoryItemMeta.setDisplayName(category.getString("name"));
				categoryItem.setItemMeta(categoryItemMeta);

				Map<String, Object> map = new HashMap<>();

				map.put("stack", categoryItem);
				map.put("items", category.getList("items"));

				newData.add(map);
			}

			shopconfig.set("data", newData);

			shopconfig.set("shop-items", null);

			try {
				shopconfig.save(shopconfigf);
			} catch (IOException e) {
				e.printStackTrace();
			}
			main.getLogger().info("Shop.yml successfully migrated from version 1 to version 2!");
		}
	}

	public void createFiles() {
		datafolder.mkdirs();

		configf = new File(datafolder, "config.yml");
		shopconfigf = new File(datafolder, "shop.yml");
		signconfigf = new File(datafolder, "sign.yml");
		recordconfigf = new File(datafolder, "record.yml");

		if (!configf.exists()) {
			try {
				configf.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		/* Move this out of Configurator */
		if (!shopconfigf.exists()) {
			if (Main.isLegacy()) {
				main.saveResource("shop_legacy.yml", false);
				new File(datafolder, "shop_legacy.yml").renameTo(shopconfigf);
			} else {
				main.saveResource("shop.yml", false);
			}
		}
		if (!signconfigf.exists()) {
			try {
				signconfigf.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!recordconfigf.exists()) {
			try {
				recordconfigf.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = new YamlConfiguration();
		shopconfig = new YamlConfiguration();
		signconfig = new YamlConfiguration();
		recordconfig = new YamlConfiguration();
		try {
			config.load(configf);
			shopconfig.load(shopconfigf);
			signconfig.load(signconfigf);
			recordconfig.load(recordconfigf);
		} catch (IOException | InvalidConfigurationException e) {
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
		checkOrSetConfig(modify, "prevent-killing-villagers", true);
		checkOrSetConfig(modify, "compass-enabled", true);
		checkOrSetConfig(modify, "join-randomly-on-lobby-join", false);
		checkOrSetConfig(modify, "add-wool-to-inventory-on-join", true);
		checkOrSetConfig(modify, "prevent-spawning-mobs", true);
		checkOrSetConfig(modify, "spawner-holograms", true);
		checkOrSetConfig(modify, "spawner-disable-merge", true);
		checkOrSetConfig(modify, "prevent-lobby-spawn-mobs-in-radius", 16);
		checkOrSetConfig(modify, "spawner-holo-height", 0.25);
		checkOrSetConfig(modify, "spawner-holograms-countdown", true);
		checkOrSetConfig(modify, "damage-when-player-is-not-in-arena", false);
		checkOrSetConfig(modify, "remove-unused-target-blocks", true);
		checkOrSetConfig(modify, "allow-block-falling", true);
		checkOrSetConfig(modify, "game-start-items", false);
		checkOrSetConfig(modify, "player-respawn-items", false);
		checkOrSetConfig(modify, "gived-game-start-items", new ArrayList<>());
		checkOrSetConfig(modify, "gived-player-respawn-items", new ArrayList<>());
		checkOrSetConfig(modify, "disable-hunger", false);
		checkOrSetConfig(modify, "automatic-coloring-in-shop", true);
		checkOrSetConfig(modify, "sell-max-64-per-click-in-shop", true);
		checkOrSetConfig(modify, "destroy-placed-blocks-by-explosion", true);
<<<<<<< HEAD
=======
		checkOrSetConfig(modify, "holo-above-bed", true);

		checkOrSetConfig(modify, "allowed-commands", new ArrayList<>());
		checkOrSetConfig(modify, "change-allowed-commands-to-blacklist", false);
>>>>>>> master

		checkOrSetConfig(modify, "bungee.enabled", false);
		checkOrSetConfig(modify, "bungee.serverRestart", true);
		checkOrSetConfig(modify, "bungee.serverStop", false);
		checkOrSetConfig(modify, "bungee.server", "hub");
		checkOrSetConfig(modify, "bungee.auto-game-connect", false);

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
		checkOrSetConfig(modify, "items.startgame", "DIAMOND");
		checkOrSetConfig(modify, "items.shopback", "BARRIER");
		checkOrSetConfig(modify, "items.shopcosmetic",
				Main.isLegacy() ? new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (short) 7)
						: "GRAY_STAINED_GLASS_PANE");
		checkOrSetConfig(modify, "items.pageback", "ARROW");
		checkOrSetConfig(modify, "items.pageforward", "ARROW");

		checkOrSetConfig(modify, "vault.enable", true);
		checkOrSetConfig(modify, "vault.reward.kill", 5);
		checkOrSetConfig(modify, "vault.reward.win", 20);
<<<<<<< HEAD
		checkOrSetConfig(modify, "resources", new ArrayList<>());

		checkOrSetConfig(modify, "respawn.protection-enabled", true);
		checkOrSetConfig(modify, "respawn.protection-time",10);
=======

		checkOrSetConfig(modify, "resources", new HashMap<String, Object>() {
			{
				put("bronze", new HashMap<String, Object>() {
					{
						put("material", Main.isLegacy() ? "CLAY_BRICK" : "BRICK");
						/* Config migration 1 -> 2: if spawners.bronze exists, get this value */
						put("interval", config.getInt("spawners.bronze", 1));
						put("name", "Bronze");
						put("translate", "resource_bronze");
						put("color", "DARK_RED");
						put("spread", 1.0);
					}
				});
				put("iron", new HashMap<String, Object>() {
					{
						put("material", "IRON_INGOT");
						/* Config migration 1 -> 2: if spawners.iron exists, get this value */
						put("interval", config.getInt("spawners.iron", 10));
						put("name", "Iron");
						put("translate", "resource_iron");
						put("color", "GRAY");
						put("spread", 1.0);
					}
				});
				put("gold", new HashMap<String, Object>() {
					{
						put("material", "GOLD_INGOT");
						/* Config migration 1 -> 2: if spawners.gold exists, get this value */
						put("interval", config.getInt("spawners.gold", 20));
						put("name", "Gold");
						put("translate", "resource_gold");
						put("color", "GOLD");
						put("spread", 1.0);
					}
				});
			}
		});
		if (config.contains("spawners")) {
			/* Config migration 1 -> 2: After creating resources, remove old spawners */
			modify.set(true);
			config.set("spawners", null);
		}
		
		System.out.println(config.get("resources"));

		checkOrSetConfig(modify, "respawn.protection-enabled", true);
		checkOrSetConfig(modify, "respawn.protection-time", 10);
>>>>>>> master

		checkOrSetConfig(modify, "specials.action-bar-messages", true);
		checkOrSetConfig(modify, "specials.rescue-platform.is-breakable", false);
		checkOrSetConfig(modify, "specials.rescue-platform.delay", 0);
		checkOrSetConfig(modify, "specials.rescue-platform.break-time", 10);
		checkOrSetConfig(modify, "specials.rescue-platform.distance", 1);
		checkOrSetConfig(modify, "specials.rescue-platform.material", "GLASS");
		checkOrSetConfig(modify, "specials.protection-wall.is-breakable", false);
		checkOrSetConfig(modify, "specials.protection-wall.delay", 20);
		checkOrSetConfig(modify, "specials.protection-wall.break-time", 0);
		checkOrSetConfig(modify, "specials.protection-wall.width", 5);
		checkOrSetConfig(modify, "specials.protection-wall.height", 3);
		checkOrSetConfig(modify, "specials.protection-wall.distance", 2);
<<<<<<< HEAD
		checkOrSetConfig(modify, "specials.protection-wall.material", "CUT_SANDSTONE");
		checkOrSetConfig(modify, "specials.tntsheep.speed", 4.0);
		checkOrSetConfig(modify, "specials.tntsheep.follow-range", 10.0);
		checkOrSetConfig(modify, "specials.tntsheep.max-target-distance", 32);
		checkOrSetConfig(modify, "specials.tntsheep.explosion-time", 8);
=======
		checkOrSetConfig(modify, "specials.protection-wall.material", Main.isLegacy() ? "SANDSTONE" : "CUT_SANDSTONE");
		checkOrSetConfig(modify, "specials.tnt-sheep.speed", 2.0);
		checkOrSetConfig(modify, "specials.tnt-sheep.follow-range", 10.0);
		checkOrSetConfig(modify, "specials.tnt-sheep.max-target-distance", 32);
		checkOrSetConfig(modify, "specials.tnt-sheep.explosion-time", 8);
>>>>>>> master
		checkOrSetConfig(modify, "specials.arrow-blocker.protection-time", 10);
		checkOrSetConfig(modify, "specials.arrow-blocker.delay", 5);
		checkOrSetConfig(modify, "specials.warp-powder.teleport-time", 6);
		checkOrSetConfig(modify, "specials.warp-powder.delay", 0);
		checkOrSetConfig(modify, "specials.magnet-shoes.probability", 75);
		checkOrSetConfig(modify, "specials.golem.speed", 0.25);
		checkOrSetConfig(modify, "specials.golem.follow-range", 10);
		checkOrSetConfig(modify, "specials.golem.health", 20);
		checkOrSetConfig(modify, "specials.golem.name-format", "%teamcolor%%team% Golem");
		checkOrSetConfig(modify, "specials.golem.show-name", true);
		checkOrSetConfig(modify, "specials.golem.delay", 0);
<<<<<<< HEAD
=======
		checkOrSetConfig(modify, "specials.golem.collidable", false);
>>>>>>> master

		checkOrSetConfig(modify, "tnt.auto-ignite", false);
		checkOrSetConfig(modify, "tnt.explosion-time", 8);

		checkOrSetConfig(modify, "sounds.on_bed_destroyed", "ENTITY_ENDER_DRAGON_GROWL");
		checkOrSetConfig(modify, "sounds.on_countdown", "UI_BUTTON_CLICK");
		checkOrSetConfig(modify, "sounds.on_game_start", "ENTITY_PLAYER_LEVELUP");
		checkOrSetConfig(modify, "sounds.on_team_kill", "ENTITY_PLAYER_LEVELUP");
		checkOrSetConfig(modify, "sounds.on_player_kill", "ENTITY_PLAYER_BIG_FALL");
		checkOrSetConfig(modify, "sounds.on_item_buy", "ENTITY_ITEM_PICKUP");
		checkOrSetConfig(modify, "sounds.on_upgrade_buy", "ENTITY_EXPERIENCE_ORB_PICKUP");

<<<<<<< HEAD
		checkOrSetConfig(modify, "game-effects.end", new HashMap<String, Object>());
=======
		checkOrSetConfig(modify, "game-effects.end", new HashMap<String, Object>() {
			{
				put("type", "Firework");
				put("power", 1);
				put("effects", new ArrayList<Object>() {
					{
						add(FireworkEffect.builder().with(FireworkEffect.Type.BALL).trail(false).flicker(false)
								.withColor(Color.WHITE).withFade(Color.WHITE).build());
					}
				});
			}
		});
>>>>>>> master
		checkOrSetConfig(modify, "game-effects.start", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.kill", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.teamkill", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.lobbyjoin", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.lobbyleave", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.respawn", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.beddestroy", new HashMap<String, Object>());
		checkOrSetConfig(modify, "game-effects.warppowdertick", new HashMap<String, Object>());

		checkOrSetConfig(modify, "lobby-scoreboard.enabled", true);
		checkOrSetConfig(modify, "lobby-scoreboard.title", "§eBEDWARS");
		checkOrSetConfig(modify, "lobby-scoreboard.content", Arrays.asList(" ", "§fMap: §2%arena%",
				"§fPlayers: §2%players%§f/§2%maxplayers%", " ", "§fWaiting ...", " "));

		checkOrSetConfig(modify, "statistics.enabled", true);
		checkOrSetConfig(modify, "statistics.type", "yaml");
		checkOrSetConfig(modify, "statistics.show-on-game-end", false);
		checkOrSetConfig(modify, "statistics.bed-destroyed-kills", false);
		checkOrSetConfig(modify, "statistics.scores.kill", 10);
		checkOrSetConfig(modify, "statistics.scores.die", 0);
		checkOrSetConfig(modify, "statistics.scores.win", 50);
		checkOrSetConfig(modify, "statistics.scores.bed-destroy", 25);
		checkOrSetConfig(modify, "statistics.scores.lose", 0);
		checkOrSetConfig(modify, "statistics.scores.record", 100);

		checkOrSetConfig(modify, "database.host", "localhost");
		checkOrSetConfig(modify, "database.port", 3306);
		checkOrSetConfig(modify, "database.db", "databse");
		checkOrSetConfig(modify, "database.user", "root");
		checkOrSetConfig(modify, "database.password", "secret");
		checkOrSetConfig(modify, "database.table-prefix", "bw_");

		checkOrSetConfig(modify, "bossbar.use-xp-bar", false);
		checkOrSetConfig(modify, "bossbar.lobby.enable", true);
		checkOrSetConfig(modify, "bossbar.lobby.color", "YELLOW");
		checkOrSetConfig(modify, "bossbar.lobby.style", "SEGMENTED_20");
		checkOrSetConfig(modify, "bossbar.game.enable", true);
		checkOrSetConfig(modify, "bossbar.game.color", "GREEN");
		checkOrSetConfig(modify, "bossbar.game.style", "SEGMENTED_20");

		checkOrSetConfig(modify, "holograms.enabled", true);
		checkOrSetConfig(modify, "holograms.headline", "Your §eBEDWARS§f stats");

		checkOrSetConfig(modify, "chat.override", true);
		checkOrSetConfig(modify, "chat.format", "<%teamcolor%%name%§r> ");
		checkOrSetConfig(modify, "chat.separate-game-chat", false);
		checkOrSetConfig(modify, "chat.send-death-messages-just-in-game", true);
		checkOrSetConfig(modify, "chat.send-custom-death-messages", true);
		checkOrSetConfig(modify, "chat.default-team-chat-while-running", true);
		checkOrSetConfig(modify, "chat.all-chat-prefix", "@a");
		checkOrSetConfig(modify, "chat.team-chat-prefix", "@t");
		checkOrSetConfig(modify, "chat.all-chat", "[ALL] ");
		checkOrSetConfig(modify, "chat.team-chat", "[TEAM] ");
		checkOrSetConfig(modify, "chat.death-chat", "[DEATH] ");

		checkOrSetConfig(modify, "rewards.enabled", false);
<<<<<<< HEAD
		checkOrSetConfig(modify, "rewards.player-win", new ArrayList<>());
		checkOrSetConfig(modify, "rewards.player-end-game", new ArrayList<>());
		checkOrSetConfig(modify, "rewards.player-destroy-bed", new ArrayList<>());
		checkOrSetConfig(modify, "rewards.player-kill", new ArrayList<>());
=======
		checkOrSetConfig(modify, "rewards.player-win", new ArrayList<String>() {
			{
				add("/example {player} 200");
			}
		});
		checkOrSetConfig(modify, "rewards.player-end-game", new ArrayList<String>() {
			{
				add("/example {player} {score}");
			}
		});
		checkOrSetConfig(modify, "rewards.player-destroy-bed", new ArrayList<String>() {
			{
				add("/example {player} {score}");
			}
		});
		checkOrSetConfig(modify, "rewards.player-kill", new ArrayList<String>() {
			{
				add("/example {player} 10");
			}
		});
>>>>>>> master

		checkOrSetConfig(modify, "lore.generate-automatically", true);
		checkOrSetConfig(modify, "lore.text",
				Arrays.asList("§7Price:", "§7%price% %resource%", "§7Amount:", "§7%amount%"));
		checkOrSetConfig(modify, "sign", Arrays.asList("§c§l[BedWars]", "%arena%", "%status%", "%players%"));

		checkOrSetConfig(modify, "hotbar.selector", 0);
		checkOrSetConfig(modify, "hotbar.color", 1);
		checkOrSetConfig(modify, "hotbar.start", 2);
		checkOrSetConfig(modify, "hotbar.leave", 8);

		checkOrSetConfig(modify, "breakable.enabled", false);
		checkOrSetConfig(modify, "breakable.blocks", new ArrayList<>());

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
			if (value instanceof Map) {
				config.createSection(path, (Map<?, ?>) value);
			} else {
				config.set(path, value);
			}
			modify.set(true);
		}
	}

	public ItemStack readDefinedItem(String item, String def) {
		ItemStack material = new ItemStack(Material.valueOf(def));

		if (config.isSet("items." + item)) {
			Object obj = config.get("items." + item);
			if (obj instanceof ItemStack) {
				material = (ItemStack) obj;
			} else {
				material.setType(Material.valueOf((String) obj));
			}
		}

		return material;
	}
}
