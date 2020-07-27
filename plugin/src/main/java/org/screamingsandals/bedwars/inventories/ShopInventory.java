package org.screamingsandals.bedwars.inventories;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.events.*;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeRegistry;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.screamingsandals.bedwars.utils.Debugger;
import org.screamingsandals.bedwars.utils.Sounds;
import org.screamingsandals.lib.debug.Debug;
import org.screamingsandals.simpleinventories.SimpleInventories;
import org.screamingsandals.simpleinventories.events.GenerateItemEvent;
import org.screamingsandals.simpleinventories.events.PreActionEvent;
import org.screamingsandals.simpleinventories.events.ShopTransactionEvent;
import org.screamingsandals.simpleinventories.inventory.Options;
import org.screamingsandals.simpleinventories.item.ItemProperty;
import org.screamingsandals.simpleinventories.item.PlayerItemInfo;
import org.screamingsandals.simpleinventories.utils.MapReader;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static misat11.lib.lang.I18n.i18n;
import static misat11.lib.lang.I18n.i18nonly;

public class ShopInventory implements Listener {
	private Map<String, SimpleInventories> shopMap = new HashMap<>();
	private Options options = new Options(Main.getInstance());

	public ShopInventory() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());

		ItemStack backItem = Main.getConfigurator().readDefinedItem("shopback", "BARRIER");
		ItemMeta backItemMeta = backItem.getItemMeta();
		backItemMeta.setDisplayName(i18n("shop_back", false));
		backItem.setItemMeta(backItemMeta);
		options.setBackItem(backItem);

		ItemStack pageBackItem = Main.getConfigurator().readDefinedItem("pageback", "ARROW");
		ItemMeta pageBackItemMeta = backItem.getItemMeta();
		pageBackItemMeta.setDisplayName(i18n("page_back", false));
		pageBackItem.setItemMeta(pageBackItemMeta);
		options.setPageBackItem(pageBackItem);

		ItemStack pageForwardItem = Main.getConfigurator().readDefinedItem("pageforward", "ARROW");
		ItemMeta pageForwardItemMeta = backItem.getItemMeta();
		pageForwardItemMeta.setDisplayName(i18n("page_forward", false));
		pageForwardItem.setItemMeta(pageForwardItemMeta);
		options.setPageForwardItem(pageForwardItem);

		ItemStack cosmeticItem = Main.getConfigurator().readDefinedItem("shopcosmetic", "AIR");
		options.setCosmeticItem(cosmeticItem);
        
		options.setRows(Main.getConfigurator().config.getInt("shop.rows", 4));
		options.setRender_actual_rows(Main.getConfigurator().config.getInt("shop.render-actual-rows", 6));
		options.setRender_offset(Main.getConfigurator().config.getInt("shop.render-offset", 9));
		options.setRender_header_start(Main.getConfigurator().config.getInt("shop.render-header-start", 0));
		options.setRender_footer_start(Main.getConfigurator().config.getInt("shop.render-footer-start", 45));
		options.setItems_on_row(Main.getConfigurator().config.getInt("shop.items-on-row", 9));
		options.setShowPageNumber(Main.getConfigurator().config.getBoolean("shop.show-page-numbers", true));
		options.setInventoryType(InventoryType.valueOf(Main.getConfigurator().config.getString("shop.inventory-type", "CHEST")));
		
		options.setPrefix(i18nonly("item_shop_name", "[BW] Shop"));
		options.setGenericShop(true);
		options.setGenericShopPriceTypeRequired(true);
		options.setAnimationsEnabled(true);
		
		options.registerPlaceholder("team", (key, player, arguments) -> {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			CurrentTeam team = gPlayer.getGame().getPlayerTeam(gPlayer);
			if (arguments.length > 0) {
				String fa = arguments[0];
				switch (fa) {
				case "color":
					return team.teamInfo.color.name();
				case "chatcolor":
					return team.teamInfo.color.chatColor.toString();
				case "maxplayers":
					return Integer.toString(team.teamInfo.maxPlayers);
				case "players":
					return Integer.toString(team.players.size());
				case "hasBed":
					return Boolean.toString(team.isBed);
				}
			}
			return team.getName();
		});
		options.registerPlaceholder("spawner", (key, player, arguments) -> {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			Game game = gPlayer.getGame();
			if (arguments.length > 2) {
				String upgradeBy = arguments[0];
				String upgrade = arguments[1];
				UpgradeStorage upgradeStorage = UpgradeRegistry.getUpgrade("spawner");
				if (upgradeStorage == null) {
					return null;
				}
				List<Upgrade> upgrades = null;
				switch (upgradeBy) {
				case "name":
					upgrades = upgradeStorage.findItemSpawnerUpgrades(game, upgrade);
					break;
				case "team":
					upgrades = upgradeStorage.findItemSpawnerUpgrades(game, game.getPlayerTeam(gPlayer));
					break;
				}

				if (upgrades != null && !upgrades.isEmpty()) {
					String what = "level";
					if (arguments.length > 3) {
						what = arguments[2];
					}
					double heighest = Double.MIN_VALUE;
					switch (what) {
					case "level":
						for (Upgrade upgrad : upgrades) {
							if (upgrad.getLevel() > heighest) {
								heighest = upgrad.getLevel();
							}
						}
						return String.valueOf(heighest);
					case "initial":
						for (Upgrade upgrad : upgrades) {
							if (upgrad.getInitialLevel() > heighest) {
								heighest = upgrad.getInitialLevel();
							}
						}
						return String.valueOf(heighest);
					}
				}
			}
			return "";
		});

		loadNewShop("default", null, true);
	}

	public void show(Player player, GameStore store) {
		try {
			boolean parent = true;
			String file = null;
			if (store != null) {
				parent = store.getUseParent();
				file = store.getShopFile();
			}
			if (file != null) {
				if (file.endsWith(".yml")) {
					file = file.substring(0, file.length() - 4);
				}
				String name = (parent ? "+" : "-") + file;
				if (!shopMap.containsKey(name)) {
					if (new File(Main.getInstance().getDataFolder(), file + ".groovy").exists()) {
						loadNewShop(name, file + ".groovy", parent);
					} else {
						loadNewShop(name, file + ".yml", parent);
					}
				}
				SimpleInventories shop = shopMap.get(name);
				shop.openForPlayer(player);
			} else {
				shopMap.get("default").openForPlayer(player);
			}
		} catch (Throwable ignored) {
			player.sendMessage(i18nonly("prefix") + " Your shop.yml/shop.groovy is invalid! Check it out or contact us on Discord.");
		}
	}

	@EventHandler
	public void onGeneratingItem(GenerateItemEvent event) {
		if (!shopMap.containsValue(event.getFormat())) {
			return;
		}

		PlayerItemInfo item = event.getInfo();
		Player player = event.getPlayer();
		Game game = Main.getPlayerGameProfile(player).getGame();
		MapReader reader = item.getReader();
		if (reader.containsKey("price") && reader.containsKey("price-type")) {
			int price = reader.getInt("price");
			ItemSpawnerType type = Main.getSpawnerType((reader.getString("price-type")).toLowerCase());
			if (type == null) {
				return;
			}

			boolean enabled = Main.getConfigurator().config.getBoolean("lore.generate-automatically", true);
			enabled = reader.getBoolean("generate-lore", enabled);

			List<String> loreText = reader.getStringList("generated-lore-text",
				Main.getConfigurator().config.getStringList("lore.text"));

			if (enabled) {
				ItemStack stack = event.getStack();
				ItemMeta stackMeta = stack.getItemMeta();
				List<String> lore = new ArrayList<>();
				if (stackMeta.hasLore()) {
					lore = stackMeta.getLore();
				}
				for (String s : loreText) {
					s = s.replaceAll("%price%", Integer.toString(price));
					s = s.replaceAll("%resource%", type.getItemName());
					s = s.replaceAll("%amount%", Integer.toString(stack.getAmount()));
					lore.add(s);
				}
				stackMeta.setLore(lore);
				stack.setItemMeta(stackMeta);
				event.setStack(stack);
			}
			if (item.hasProperties()) {
				for (ItemProperty property : item.getProperties()) {
					if (property.hasName()) {
						ItemStack newItem = event.getStack();
						BedwarsApplyPropertyToDisplayedItem applyEvent = new BedwarsApplyPropertyToDisplayedItem(game,
							player, newItem, property.getReader(player).convertToMap());
						Main.getInstance().getServer().getPluginManager().callEvent(applyEvent);

						event.setStack(newItem);
					}
				}
			}
		}

	}

	@EventHandler
	public void onPreAction(PreActionEvent event) {
		if (!shopMap.containsValue(event.getFormat()) || event.isCancelled()) {
			return;
		}

		if (!Main.isPlayerInGame(event.getPlayer())) {
			event.setCancelled(true);
		}

		if (Main.getPlayerGameProfile(event.getPlayer()).isSpectator) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onShopTransaction(ShopTransactionEvent event) {
		if (!shopMap.containsValue(event.getFormat()) || event.isCancelled()) {
			return;
		}
		Game game = Main.getPlayerGameProfile(event.getPlayer()).getGame();

		MapReader reader = event.getItem().getReader();
		if (reader.containsKey("upgrade")) {
			handleUpgrade(event);
		} else {
			handleBuy(event);
		}
	}

	@EventHandler
	public void onApplyPropertyToBoughtItem(BedwarsApplyPropertyToItem event) {
		if (event.getPropertyName().equalsIgnoreCase("applycolorbyteam")
			|| event.getPropertyName().equalsIgnoreCase("transform::applycolorbyteam")) {
			Player player = event.getPlayer();
			CurrentTeam team = (CurrentTeam) event.getGame().getTeamOfPlayer(player);

			if (Main.getConfigurator().config.getBoolean("automatic-coloring-in-shop")) {
				event.setStack(Main.applyColor(team.teamInfo.color, event.getStack()));
			}
		}
	}

	private void loadNewShop(String name, String fileName, boolean useParent) {
		SimpleInventories format = new SimpleInventories(options);
		try {
			if (useParent) {
				String shopFileName = "shop.yml";
				if (Main.getConfigurator().config.getBoolean("turnOnExperimentalGroovyShop", false)) {
					shopFileName = "shop.groovy";
				}
				format.loadFromDataFolder(Main.getInstance().getDataFolder(), shopFileName);
			}
			if (fileName != null) {
				format.loadFromDataFolder(Main.getInstance().getDataFolder(), fileName);
			}
		} catch (Exception ignored) {
			Debug.warn("Wrong shop.yml/shop.groovy configuration!", true);
			Debug.warn("Your villagers won't work, check validity of your YAML/Groovy!", true);
		}

		format.generateData();
		shopMap.put(name, format);
	}

	private static String getNameOrCustomNameOfItem(ItemStack stack) {
		try {
			if (stack.hasItemMeta()) {
				ItemMeta meta = stack.getItemMeta();
				if (meta == null) {
					return "";
				}

				if (meta.hasDisplayName()) {
					return meta.getDisplayName();
				}
				if (meta.hasLocalizedName()) {
					return meta.getLocalizedName();
				}
			}
		} catch (Throwable ignored) {
		}

		String normalItemName = stack.getType().name().replace("_", " ").toLowerCase();
		String[] sArray = normalItemName.split(" ");
		StringBuilder stringBuilder = new StringBuilder();

		for (String s : sArray) {
			stringBuilder.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
		}
		return stringBuilder.toString().trim();
	}

	private void handleBuy(ShopTransactionEvent event) {
		Player player = event.getPlayer();
		Game game = Main.getPlayerGameProfile(event.getPlayer()).getGame();
		ClickType clickType = event.getClickType();
		MapReader mapReader = event.getItem().getReader();
		String priceType = event.getType().toLowerCase();
		ItemSpawnerType type = Main.getSpawnerType(priceType);
		ItemStack newItem = event.getStack();

		int amount = newItem.getAmount();
		int price = event.getPrice();
		int inInventory = 0;

		if (mapReader.containsKey("currency-changer")) {
			String changeItemToName = mapReader.getString("currency-changer");
			ItemSpawnerType changeItemType;
			if (changeItemToName == null) {
				return;
			}

			changeItemType = Main.getSpawnerType(changeItemToName);
			if (changeItemType == null) {
				return;
			}

			newItem = changeItemType.getStack();
		}

		if (clickType.isShiftClick() && newItem.getMaxStackSize() > 1) {
			double priceOfOne = (double) price / amount;
			double maxStackSize;
			int finalStackSize;

			for (ItemStack itemStack : event.getPlayer().getInventory().getStorageContents()) {
				if (itemStack != null && itemStack.isSimilar(type.getStack())) {
					inInventory = inInventory + itemStack.getAmount();
				}
			}
			if (Main.getConfigurator().config.getBoolean("sell-max-64-per-click-in-shop")) {
				maxStackSize = Math.min(inInventory / priceOfOne, newItem.getMaxStackSize());
			} else {
				maxStackSize = inInventory / priceOfOne;
			}

			finalStackSize = (int) maxStackSize;
			if (finalStackSize > amount) {
				price = (int) (priceOfOne * finalStackSize);
				newItem.setAmount(finalStackSize);
				amount = finalStackSize;
			}
		}

		ItemStack materialItem = type.getStack(price);
		if (event.hasPlayerInInventory(materialItem)) {
			if (event.hasProperties()) {
				for (ItemProperty property : event.getProperties()) {
					if (property.hasName()) {
						BedwarsApplyPropertyToBoughtItem applyEvent = new BedwarsApplyPropertyToBoughtItem(game, player,
							newItem, property.getReader(player).convertToMap());
						Main.getInstance().getServer().getPluginManager().callEvent(applyEvent);

						newItem = applyEvent.getStack();
					}
				}
			}

			event.sellStack(materialItem);
			Map<Integer, ItemStack> notFit = event.buyStack(newItem);
			if (!notFit.isEmpty()) {
				notFit.forEach((i, stack) -> player.getLocation().getWorld().dropItem(player.getLocation(), stack));
			}

			if (!Main.getConfigurator().config.getBoolean("removePurchaseMessages", false)) {
				player.sendMessage(i18n("buy_succes").replace("%item%", amount + "x " + getNameOrCustomNameOfItem(newItem))
						.replace("%material%", price + " " + type.getItemName()));
			}
			Sounds.playSound(player, player.getLocation(),
				Main.getConfigurator().config.getString("sounds.on_item_buy"), Sounds.ENTITY_ITEM_PICKUP, 1, 1);
		} else {
			if (!Main.getConfigurator().config.getBoolean("removePurchaseMessages", false)) {
				player.sendMessage(i18n("buy_failed").replace("%item%", amount + "x " + getNameOrCustomNameOfItem(newItem))
						.replace("%material%", price + " " + type.getItemName()));
			}
		}
	}

	private void handleUpgrade(ShopTransactionEvent event) {
		Player player = event.getPlayer();
		Game game = Main.getPlayerGameProfile(event.getPlayer()).getGame();
		MapReader mapReader = event.getItem().getReader();
		String priceType = event.getType().toLowerCase();
		ItemSpawnerType itemSpawnerType = Main.getSpawnerType(priceType);

		MapReader upgradeMapReader = mapReader.getMap("upgrade");
		List<MapReader> entities = upgradeMapReader.getMapList("entities");
		String itemName = upgradeMapReader.getString("shop-name", "UPGRADE");

		int price = event.getPrice();
		boolean sendToAll = false;
		boolean isUpgrade = true;
		ItemStack materialItem = itemSpawnerType.getStack(price);

		if (event.hasPlayerInInventory(materialItem)) {
			event.sellStack(materialItem);
			for (MapReader mapEntity : entities) {
				String configuredType = mapEntity.getString("type");
				if (configuredType == null) {
					return;
				}

				UpgradeStorage upgradeStorage = UpgradeRegistry.getUpgrade(configuredType);
				if (upgradeStorage != null) {

					// TODO: Learn SimpleGuiFormat upgrades pre-parsing and automatic renaming old
					// variables
					Team team = game.getTeamOfPlayer(event.getPlayer());
					double addLevels = mapEntity.getDouble("add-levels",
						mapEntity.getDouble("levels", 0) /* Old configuration */);
					/* You shouldn't use it in entities */
					if (mapEntity.containsKey("shop-name")) {
						itemName = mapEntity.getString("shop-name");
					}
					sendToAll = mapEntity.getBoolean("notify-team", false);

					List<Upgrade> upgrades = new ArrayList<>();

					if (mapEntity.containsKey("spawner-name")) {
						String customName = mapEntity.getString("spawner-name");
						upgrades = upgradeStorage.findItemSpawnerUpgrades(game, customName);
					} else if (mapEntity.containsKey("spawner-type")) {
						String mapSpawnerType = mapEntity.getString("spawner-type");
						ItemSpawnerType spawnerType = Main.getSpawnerType(mapSpawnerType);

						upgrades = upgradeStorage.findItemSpawnerUpgrades(game, team, spawnerType);
					} else if (mapEntity.containsKey("team-upgrade")) {
						boolean upgradeAllSpawnersInTeam = mapEntity.getBoolean("team-upgrade");

						if (upgradeAllSpawnersInTeam) {
							upgrades = upgradeStorage.findItemSpawnerUpgrades(game, team);
						}

					} else if (mapEntity.containsKey("customName")) { // Old configuration
						String customName = mapEntity.getString("customName");
						upgrades = upgradeStorage.findItemSpawnerUpgrades(game, customName);
					} else {
						isUpgrade = false;
						Debugger.warn("[BedWars]> Upgrade configuration is invalid.");
					}

					if (isUpgrade) {
						BedwarsUpgradeBoughtEvent bedwarsUpgradeBoughtEvent = new BedwarsUpgradeBoughtEvent(game,
							upgradeStorage, upgrades, player, addLevels);
						Bukkit.getPluginManager().callEvent(bedwarsUpgradeBoughtEvent);

						if (bedwarsUpgradeBoughtEvent.isCancelled()) {
							continue;
						}

						if (upgrades.isEmpty()) {
							continue;
						}

						for (Upgrade upgrade : upgrades) {
							BedwarsUpgradeImprovedEvent improvedEvent = new BedwarsUpgradeImprovedEvent(game,
								upgradeStorage, upgrade, upgrade.getLevel(), upgrade.getLevel() + addLevels);
							Bukkit.getPluginManager().callEvent(improvedEvent);
						}
					}
				}

				if (sendToAll) {
					for (Player player1 : game.getTeamOfPlayer(event.getPlayer()).getConnectedPlayers()) {
						if (!Main.getConfigurator().config.getBoolean("removePurchaseMessages", false)) {
							player1.sendMessage(i18n("buy_succes").replace("%item%", itemName).replace("%material%",
									price + " " + itemSpawnerType.getItemName()));
						}
						Sounds.playSound(player1, player1.getLocation(),
							Main.getConfigurator().config.getString("sounds.on_upgrade_buy"),
							Sounds.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
					}
				} else {
					if (!Main.getConfigurator().config.getBoolean("removePurchaseMessages", false)) {
						player.sendMessage(i18n("buy_succes").replace("%item%", itemName).replace("%material%",
								price + " " + itemSpawnerType.getItemName()));
					}
					Sounds.playSound(player, player.getLocation(),
						Main.getConfigurator().config.getString("sounds.on_upgrade_buy"),
						Sounds.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				}
			}
		} else {
			if (!Main.getConfigurator().config.getBoolean("removePurchaseMessages", false)) {
				player.sendMessage(i18n("buy_failed").replace("%item%", "UPGRADE").replace("%material%",
						price + " " + itemSpawnerType.getItemName()));
			}
		}
	}
}
