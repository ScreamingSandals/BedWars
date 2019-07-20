package misat11.bw.utils;

import misat11.bw.Main;
import misat11.bw.api.GameStore;
import misat11.bw.api.ItemSpawnerType;
import misat11.bw.api.TeamColor;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.game.CurrentTeam;
import misat11.bw.game.Game;
import misat11.bw.game.ItemSpawner;
import misat11.lib.sgui.PlayerItemInfo;
import misat11.lib.sgui.Property;
import misat11.lib.sgui.SimpleGuiFormat;
import misat11.lib.sgui.events.GenerateItemEvent;
import misat11.lib.sgui.events.PreActionEvent;
import misat11.lib.sgui.events.ShopTransactionEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static misat11.lib.lang.I18n.i18n;
import static misat11.lib.lang.I18n.i18nonly;

public class ShopMenu implements Listener {
	private ItemStack backItem, pageBackItem, pageForwardItem, cosmeticItem;
	private SimpleGuiFormat format;
	private String shopName = i18nonly("item_shop_name", "[BW] Shop");

	public ShopMenu() {
		List<Map<String, Object>> data = (List<Map<String, Object>>) Main.getConfigurator().shopconfig.getList("data");

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

		format = new SimpleGuiFormat(shopName, backItem, pageBackItem, pageForwardItem, cosmeticItem);
		format.load(data);
		format.enableAnimations(Main.getInstance());
		format.enableGenericShop(true);

		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());

		format.generateData();
	}

	public ShopMenu(String fileName, boolean useParent) {
		File file = new File(Main.getInstance().getDataFolder(), fileName + ".yml");

		YamlConfiguration config = new YamlConfiguration();

		try {
			config.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}

		List<Map<String, Object>> parent = (List<Map<String, Object>>) Main.getConfigurator().shopconfig
				.getList("data");

		List<Map<String, Object>> data = (List<Map<String, Object>>) config.getList("data");

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

		format = new SimpleGuiFormat(shopName, backItem, pageBackItem, pageForwardItem, cosmeticItem);
		if (useParent) {
			format.load(parent);
		}
		format.load(data);
		format.enableAnimations(Main.getInstance());
		format.enableGenericShop(true);

		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());

		format.generateData();
	}

	@EventHandler
	public void onGeneratingItem(GenerateItemEvent event) {
		if (event.getFormat() != format) {
			return;
		}

		PlayerItemInfo item = event.getInfo();
		Map<String, Object> originalItemData = item.getData();
		if (originalItemData.containsKey("price") && originalItemData.containsKey("price-type")) {
			int price = (int) originalItemData.get("price");
			ItemSpawnerType type = Main.getSpawnerType(((String) originalItemData.get("price-type")).toLowerCase());
			if (type == null) {
				return;
			}

			boolean enabled = Main.getConfigurator().config.getBoolean("lore.generate-automatically", true);
			enabled = (boolean) originalItemData.getOrDefault("generate-lore", enabled);

			List<String> loreText = (List<String>) originalItemData.getOrDefault("generated-lore-text",
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
		}

	}

	@EventHandler
	public void onPreAction(PreActionEvent event) {
		if (event.getFormat() != format || event.isCancelled()) {
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
		if (event.getFormat() != format || event.isCancelled()) {
			return;
		}

		Player player = event.getPlayer();
		Game game = Main.getPlayerGameProfile(event.getPlayer()).getGame();
		ClickType clickType = event.getClickType();

		Map<String, Object> originalItemData = event.getItem().getData();
		if (originalItemData.containsKey("upgrade") && game.isUpgradesEnabled()) {
			Map<String, Object> upgrade = (Map<String, Object>) originalItemData.get("upgrade");
			List<Map<String, Object>> entities = (List<Map<String, Object>>) upgrade.get("entities");
			int price = event.getPrice();
			String priceType = event.getType().toLowerCase();
			ItemSpawnerType type = Main.getSpawnerType(priceType);
			ItemStack materialItem = type.getStack(price);
			if (event.hasPlayerInInventory(materialItem)) {
				event.sellStack(materialItem);
				for (Map<String, Object> entity : entities) {
					String typ = (String) entity.get("type");
					if ("spawner".equals(typ.toLowerCase())) {
						String customName = (String) entity.get("customName");
						int addLevels = (int) entity.get("levels");
						for (ItemSpawner spawner : game.getSpawners()) {
							if (customName.equals(spawner.customName)) {
								spawner.currentLevel += addLevels;
							}
						}
					}
				}
				player.sendMessage(i18n("buy_succes").replace("%item%", "UPGRADE"));
				Sounds.playSound(player, player.getLocation(),
						Main.getConfigurator().config.getString("sounds.on_upgrade_buy"),
						Sounds.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			} else {
				player.sendMessage(i18n("buy_failed").replace("%item%", "UPGRADE"));
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
									player, newItem, property.getPropertyData());
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
	public void onApplyPropertyToBoughtItem(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("applycolorbyteam")) {
			ItemStack stack = event.getStack();
			Player player = event.getPlayer();
			CurrentTeam team = (CurrentTeam) event.getGame().getTeamOfPlayer(player);

			if (Main.getConfigurator().config.getBoolean("automatic-coloring-in-shop")) {
				stack.setType(changeStackColor(stack, team.getColor()));
				event.setStack(stack);
			}
		}
	}

	private final HashMap<String, ShopMenu> shopMenus = new HashMap<>();

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
			if (!shopMenus.containsKey(name)) {
				ShopMenu shopMenu = new ShopMenu(file, parent);
				shopMenus.put(name, shopMenu);
			}
			ShopMenu shop = shopMenus.get(name);
			shop.show(p, null);
		} else {
			format.openForPlayer(p);
		}
	}

	public static Material changeStackColor(ItemStack itemStack, TeamColor color) {
		Material material = itemStack.getType();
		List<String> materialList = new ArrayList<>();
		String materialName = material.toString();
		String newMaterialName = materialName.substring(materialName.indexOf("_")+1);

		materialList.add("_STAINED_CLAY");
		materialList.add("_WOOL");
		materialList.add("_CARPET");

		if (materialList.contains(material.toString())) {
			return Material.getMaterial(color + newMaterialName);
		}
		else if (material.toString().contains("GLASS")) {
			return Material.getMaterial(color + "_STAINED_GLASS");
		}

		else if (material.toString().contains("GLASS_PANE")) {
			return Material.getMaterial(color + "_STAINED_GLASS_PANE");
		}

		return material;

	}
}


