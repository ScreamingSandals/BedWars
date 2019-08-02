package misat11.bw.utils;

import misat11.bw.Main;
import misat11.bw.api.GameStore;
import misat11.bw.api.ItemSpawnerType;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.api.events.BedwarsApplyPropertyToDisplayedItem;
import misat11.bw.api.events.BedwarsApplyPropertyToItem;
import misat11.bw.game.CurrentTeam;
import misat11.bw.game.Game;
import misat11.bw.game.GamePlayer;
import misat11.bw.game.ItemSpawner;
import misat11.bw.game.TeamColor;
import misat11.lib.sgui.MapReader;
import misat11.lib.sgui.PlayerItemInfo;
import misat11.lib.sgui.Property;
import misat11.lib.sgui.SimpleGuiFormat;
import misat11.lib.sgui.events.GenerateItemEvent;
import misat11.lib.sgui.events.PreActionEvent;
import misat11.lib.sgui.events.ShopTransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

	private ItemStack backItem, pageBackItem, pageForwardItem, cosmeticItem;
	private String shopName = i18nonly("item_shop_name", "[BW] Shop");
	private Map<String, SimpleGuiFormat> shopMap = new HashMap<>();

	public ShopMenu() {
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());

		backItem = Main.getConfigurator().readDefinedItem("shopback", "BARRIER");
		ItemMeta backItemMeta = backItem.getItemMeta();
		backItemMeta.setDisplayName(i18n("shop_back", false));
		backItem.setItemMeta(backItemMeta);

		pageBackItem = Main.getConfigurator().readDefinedItem("pageback", "ARROW");
		ItemMeta pageBackItemMeta = backItem.getItemMeta();
		pageBackItemMeta.setDisplayName(i18n("page_back", false));
		pageBackItem.setItemMeta(pageBackItemMeta);

		pageForwardItem = Main.getConfigurator().readDefinedItem("pageforward", "ARROW");
		ItemMeta pageForwardItemMeta = backItem.getItemMeta();
		pageForwardItemMeta.setDisplayName(i18n("page_forward", false));
		pageForwardItem.setItemMeta(pageForwardItemMeta);

		cosmeticItem = Main.getConfigurator().readDefinedItem("shopcosmetic", "AIR");

		loadNewShop("default", null, true);
	}

	private void loadNewShop(String name, String fileName, boolean useParent) {
		SimpleGuiFormat format = new SimpleGuiFormat(shopName, backItem, pageBackItem, pageForwardItem, cosmeticItem);
		try {
			if (useParent) {
				format.loadFromDataFolder(Main.getInstance().getDataFolder(), "shop.yml");
			}
			if (fileName != null) {
				format.loadFromDataFolder(Main.getInstance().getDataFolder(), fileName);
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		format.enableAnimations(Main.getInstance());
		format.enableGenericShop(true);
		
		format.registerPlaceholder("%team%", (key, player, arguments) -> {
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
					if ("spawner".equals(typ.toLowerCase())) {
						String customName = entity.getString("customName");
						double addLevels = entity.getDouble("levels");
						for (ItemSpawner spawner : game.getSpawners()) {
							if (customName.equals(spawner.customName)) {
								spawner.currentLevel += addLevels;
							}
						}
					}
				}
				player.sendMessage(i18n("buy_succes").replace("%item%", "UPGRADE").replace("%material%",
						price + " " + type.getItemName()));
				Sounds.playSound(player, player.getLocation(),
						Main.getConfigurator().config.getString("sounds.on_upgrade_buy"),
						Sounds.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			} else {
				player.sendMessage(i18n("buy_failed").replace("%item%", "UPGRADE").replace("%material%",
						price + " " + type.getItemName()));
			}
		} else {
			ItemStack newItem = event.getStack();
			int amount = newItem.getAmount();
			int price = event.getPrice();
			if (clickType.isShiftClick()) {
				int maxStackSize = newItem.getMaxStackSize();
				if (maxStackSize > amount) {
					double priceOfOne = (double) price / amount;
					price = (int) (priceOfOne * maxStackSize);
					newItem.setAmount(maxStackSize);
					amount = maxStackSize;
				}
			}
			String priceType = event.getType().toLowerCase();
			ItemSpawnerType type = Main.getSpawnerType(priceType);
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
		} catch (Throwable t) {
		}
		String normalName = stack.getType().name().replace("_", " ").toLowerCase();
		return normalName.substring(0, 1).toUpperCase() + normalName.substring(1);
	}

	@EventHandler
	public void onApplyPropertyToBoughtItem(BedwarsApplyPropertyToItem event) {
		if (event.getPropertyName().equalsIgnoreCase("applycolorbyteam")
				|| event.getPropertyName().equalsIgnoreCase("transform::applycolorbyteam")) {
			ItemStack stack = event.getStack();
			Material material = stack.getType();
			Player player = event.getPlayer();
			CurrentTeam team = (CurrentTeam) event.getGame().getTeamOfPlayer(player);
			TeamColor color = team.teamInfo.color;
			if (Main.getConfigurator().config.getBoolean("automatic-coloring-in-shop")) {
				if (Main.isLegacy()) {
					event.setStack(ColorChanger.changeLegacyStackColor(stack, color));
				} else {
					stack.setType(ColorChanger.changeStackColor(material, color));
				}
				ItemStack newStack = ColorChanger.changeLeatherArmorColor(stack, color);

				event.setStack(newStack);
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
				loadNewShop(name, file, parent);
			}
			SimpleGuiFormat shop = shopMap.get(name);
			shop.openForPlayer(p);
		} else {
			shopMap.get("default").openForPlayer(p);
		}
	}
}
