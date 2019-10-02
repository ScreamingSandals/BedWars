package misat11.bw.utils;

import misat11.bw.Main;
import misat11.bw.api.GameStore;
import misat11.bw.api.ItemSpawnerType;
import misat11.bw.api.events.*;
import misat11.bw.api.upgrades.Upgrade;
import misat11.bw.api.upgrades.UpgradeRegistry;
import misat11.bw.api.upgrades.UpgradeStorage;
import misat11.bw.game.CurrentTeam;
import misat11.bw.game.Game;
import misat11.bw.game.GamePlayer;
import misat11.bw.game.ItemSpawner;
import misat11.lib.sgui.*;
import misat11.lib.sgui.events.GenerateItemEvent;
import misat11.lib.sgui.events.PreActionEvent;
import misat11.lib.sgui.events.ShopTransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static misat11.lib.lang.I18n.i18n;
import static misat11.lib.lang.I18n.i18nonly;

public class ShopMenu implements Listener {

	private Map<String, SimpleGuiFormat> shopMap = new HashMap<>();
	private Options options = new Options();

	public ShopMenu() {
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

		options.setRender_actual_rows(Main.getConfigurator().config.getInt("secretShopSetRows", 6));
		options.setPrefix(i18nonly("item_shop_name", "[BW] Shop"));
		options.setGenericShop(true);
		options.setGenericShopPriceTypeRequired(true);
		options.setAnimationsEnabled(true, Main.getInstance());
		options.registerPlaceholder("%team%", (key, player, arguments) -> {
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

		loadNewShop("default", null, true);
	}

	private void loadNewShop(String name, String fileName, boolean useParent) {
		SimpleGuiFormat format = new SimpleGuiFormat(options);
		try {
			if (useParent) {
				format.loadFromDataFolder(Main.getInstance().getDataFolder(), "shop.yml");
			}
			if (fileName != null) {
				format.loadFromDataFolder(Main.getInstance().getDataFolder(), fileName);
			}
		} catch (IOException | InvalidConfigurationException e) {
			System.out.println("Wrong shop.yml configuration!");
			e.printStackTrace();
		}

		format.generateData();

		shopMap.put(name, format);
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
				for (Property property : item.getProperties()) {
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

		Player player = event.getPlayer();
		Game game = Main.getPlayerGameProfile(event.getPlayer()).getGame();
		ClickType clickType = event.getClickType();
		String itemName = "UPGRADE";

		MapReader reader = event.getItem().getReader();
		if (reader.containsKey("upgrade") && game.isUpgradesEnabled()) {
			MapReader upgrade = reader.getMap("upgrade");
			List<MapReader> entities = upgrade.getMapList("entities");
			int price = event.getPrice();
			String priceType = event.getType().toLowerCase();
			ItemSpawnerType type = Main.getSpawnerType(priceType);
			ItemStack materialItem = type.getStack(price);
			if (event.hasPlayerInInventory(materialItem)) {
				event.sellStack(materialItem);
				for (MapReader entity : entities) {
					String typ = entity.getString("type");
					UpgradeStorage storage = UpgradeRegistry.getUpgrade(typ);
					if (storage != null) {
						String customName = entity.getString("customName");
						double addLevels = entity.getDouble("levels");
						itemName = entity.getString("shop-name");

						List<Upgrade> upgrades = storage.findUpgrade(game, customName);
						BedwarsUpgradeBoughtEvent boughEvent = new BedwarsUpgradeBoughtEvent(game, storage, upgrades,
								player, addLevels);
						Bukkit.getPluginManager().callEvent(boughEvent);

						if (boughEvent.isCancelled()) {
							continue;
						}

						for (Upgrade upgrad : upgrades) {
							BedwarsUpgradeImprovedEvent improvedEvent = new BedwarsUpgradeImprovedEvent(game, storage,
									upgrad, upgrad.getLevel(), upgrad.getLevel() + addLevels);
							Bukkit.getPluginManager().callEvent(improvedEvent);
							
							if (upgrad instanceof ItemSpawner) {
								BedwarsApplyUpgradeEvent deprecatedApplyUpgradeEvent = new BedwarsApplyUpgradeEvent(game, player, game.getTeamOfPlayer(player), (ItemSpawner) upgrad, upgrad.getLevel());
								Bukkit.getPluginManager().callEvent(deprecatedApplyUpgradeEvent);
								
								// Ignore isCancelled of this event
							}
						}
					}
				}

				player.sendMessage(i18n("buy_succes").replace("%item%", itemName).replace("%material%",
						price + " " + type.getItemName()));
				Sounds.playSound(player, player.getLocation(),
						Main.getConfigurator().config.getString("sounds.on_upgrade_buy"),
						Sounds.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			} else {
				player.sendMessage(i18n("buy_failed").replace("%item%", "UPGRADE").replace("%material%",
						price + " " + type.getItemName()));
			}
		} else {
			String priceType = event.getType().toLowerCase();
			ItemSpawnerType type = Main.getSpawnerType(priceType);
			ItemStack newItem = event.getStack();

			int amount = newItem.getAmount();
			int price = event.getPrice();
			int inInventory = 0;

			if (clickType.isShiftClick()) {
				double priceOfOne = (double) price / amount;
				double maxStackSize;
				int finalStackSize;

				for (ItemStack itemStack : event.getPlayer().getInventory().getStorageContents()) {
					if (itemStack == null) {
						continue;
					}

					if (itemStack.isSimilar(type.getStack())) {
						inInventory = inInventory + itemStack.getAmount();
					}
				}
				if (Main.getConfigurator().config.getBoolean("sell-max-64-per-click-in-shop")) {
					maxStackSize = Math.min(inInventory / priceOfOne, 64);
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
					for (Property property : event.getProperties()) {
						if (property.hasName()) {
							BedwarsApplyPropertyToBoughtItem applyEvent = new BedwarsApplyPropertyToBoughtItem(game,
									player, newItem, property.getReader(player).convertToMap());
							Main.getInstance().getServer().getPluginManager().callEvent(applyEvent);

							newItem = applyEvent.getStack();
						}
					}
				}
				event.sellStack(materialItem);
				event.buyStack(newItem);
				player.sendMessage(
						i18n("buy_succes").replace("%item%", amount + "x " + getNameOrCustomNameOfItem(newItem))
								.replace("%material%", price + " " + type.getItemName()));
				Sounds.playSound(player, player.getLocation(),
						Main.getConfigurator().config.getString("sounds.on_item_buy"), Sounds.ENTITY_ITEM_PICKUP, 1, 1);
			} else {
				player.sendMessage(
						i18n("buy_failed").replace("%item%", amount + "x " + getNameOrCustomNameOfItem(newItem))
								.replace("%material%", price + " " + type.getItemName()));
			}
		}
	}

	public static String getNameOrCustomNameOfItem(ItemStack stack) {
		try {
			if (stack.hasItemMeta()) {
				ItemMeta meta = stack.getItemMeta();
				if (meta.hasDisplayName()) {
					return meta.getDisplayName();
				}
				if (meta.hasLocalizedName()) {
					return meta.getLocalizedName();
				}
			}
		} catch (Throwable ignored) {
		}

		String normalName = stack.getType().name().replace("_", " ").toLowerCase();
		return normalName.substring(0, 1).toUpperCase() + normalName.substring(1);
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

	public void show(Player p, GameStore store) {
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
				loadNewShop(name, file + ".yml", parent);
			}
			SimpleGuiFormat shop = shopMap.get(name);
			shop.openForPlayer(p);
		} else {
			shopMap.get("default").openForPlayer(p);
		}
	}
}
