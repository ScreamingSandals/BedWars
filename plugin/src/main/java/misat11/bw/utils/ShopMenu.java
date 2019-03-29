package misat11.bw.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import misat11.bw.Main;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.game.ItemSpawnerType;

import static misat11.bw.utils.I18n.i18n;

public class ShopMenu implements Listener {

	private Inventory mainInventory;
	private HashMap<Integer, Category> categoryInventories = new HashMap<Integer, Category>();
	private ItemStack backItem, pageBackItem, pageForwardItem, cosmeticItem;

	public static final int ITEMS_ON_PAGE = 36;

	public static class Category {
		public final List<Inventory> inv = new ArrayList<Inventory>();
		public final HashMap<ItemStack, Map<String, Object>> items = new HashMap<ItemStack, Map<String, Object>>();
		public int lastpos = 0;

		private Category() {
		}

	}

	public ShopMenu(Plugin p) {
		mainInventory = Bukkit.getServer().createInventory(null, 54, "[BW] Shop");
		int lastpos = 0;

		Set<String> s = Main.getConfigurator().shopconfig.getConfigurationSection("shop-items").getKeys(false);

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

		for (String i : s) {
			ConfigurationSection category = Main.getConfigurator().shopconfig
					.getConfigurationSection("shop-items." + i);

			ItemStack categoryItem = new ItemStack(Material.valueOf(category.getString("item")), 1,
					(short) category.getInt("damage", 0));
			ItemMeta categoryItemMeta = categoryItem.getItemMeta();
			categoryItemMeta.setLore(category.getStringList("lore"));
			categoryItemMeta.setDisplayName(category.getString("name"));
			categoryItem.setItemMeta(categoryItemMeta);
			Category categoryClass = new Category();

			createInventoryOnPage(1, categoryClass, category.getString("name")); // Force first page

			List<Map<String, Object>> categoryItemsList = (List<Map<String, Object>>) category.getList("items");
			for (Map<String, Object> item : categoryItemsList) {
				ItemStack stack = ((ItemStack) item.get("stack")).clone();
				ItemMeta stackMeta = stack.getItemMeta();
				List<String> lore = new ArrayList<String>();
				if (stackMeta.hasLore()) {
					lore = stackMeta.getLore();
				}
				int price = (int) item.get("price");
				ItemSpawnerType type = Main.getSpawnerType(((String) item.get("price-type")).toLowerCase());
				if (type == null) {
					continue;
				}

				lore.add(i18n("price", false));
				lore.add(price + " " + type.getItemName());
				lore.add(i18n("amount", false));
				lore.add(Integer.toString(stack.getAmount()));
				stackMeta.setLore(lore);
				stack.setItemMeta(stackMeta);
				categoryClass.items.put(stack, item);
				int position = categoryClass.lastpos;
				int linebreak = 0;
				int pagebreak = 0;
				if (item.containsKey("linebreak")) {
					String lnBreak = (String) item.get("linebreak");
					if (lnBreak.equalsIgnoreCase("before")) {
						linebreak = 1;
					} else if (lnBreak.equalsIgnoreCase("after")) {
						linebreak = 2;
					} else if (lnBreak.equalsIgnoreCase("both")) {
						linebreak = 3;
					}
				}
				if (item.containsKey("pagebreak")) {
					String pgBreak = (String) item.get("pagebreak");
					if (pgBreak.equalsIgnoreCase("before")) {
						pagebreak = 1;
					} else if (pgBreak.equalsIgnoreCase("after")) {
						pagebreak = 2;
					} else if (pgBreak.equalsIgnoreCase("both")) {
						pagebreak = 3;
					}
				}
				if (pagebreak == 1 || pagebreak == 3) {
					position += (ITEMS_ON_PAGE - (position % ITEMS_ON_PAGE));
				}
				if (item.containsKey("row")) {
					position = 9 + ((int) item.get("row") * 9) + (position % 9);
				}
				if (item.containsKey("column")) {
					Object cl = item.get("column");
					int column = 0;
					if ("left".equals(cl) || "first".equals(cl)) {
						column = 0;
					} else if ("middle".equals(cl) || "center".equals(cl)) {
						column = 4;
					} else if ("right".equals(cl) || "last".equals(cl)) {
						column = 8;
					} else {
						column = (int) cl;
					}

					position = (position - (position % 9)) + column;
				}
				if (linebreak == 1 || linebreak == 3) {
					position += (9 - (position % 9));
				}
				if (item.containsKey("skip")) {
					position += (int) item.get("skip");
				}
				createInventoryOnPage((position / ITEMS_ON_PAGE) + 1, categoryClass, category.getString("name"))
						.setItem(position, stack);
				int nextPosition = position;
				if (pagebreak >= 2) {
					nextPosition += (ITEMS_ON_PAGE - (nextPosition % ITEMS_ON_PAGE));
				}
				if (linebreak >= 2) {
					nextPosition += (9 - (nextPosition % 9));
				}
				if (pagebreak < 2 && linebreak < 2) {
					nextPosition++;
				}
				categoryClass.lastpos = nextPosition;
			}

			categoryClass.inv.get(categoryClass.inv.size() - 1).setItem(53, cosmeticItem);

			mainInventory.setItem(lastpos, categoryItem);
			categoryInventories.put(lastpos, categoryClass);
			lastpos++;
		}

		Bukkit.getServer().getPluginManager().registerEvents(this, p);
	}

	private Inventory createInventoryOnPage(int page, Category category, String categoryName) {
		if (category.inv.size() >= page) {
			return category.inv.get(page - 1);
		}

		Inventory categoryInventory = Bukkit.getServer().createInventory(null, 54,
				"[BW] Shop: " + categoryName + "§r - " + page);

		category.inv.add(categoryInventory);

		categoryInventory.setItem(0, backItem);

		for (int a = 1; a <= 8; a++) {
			categoryInventory.setItem(a, cosmeticItem);
		}

		if (page > 1) {
			categoryInventory.setItem(45, pageBackItem);
			category.inv.get(page - 2).setItem(53, pageForwardItem);
		} else {
			categoryInventory.setItem(45, cosmeticItem);
		}

		for (int a = 1; a <= 7; a++) {
			categoryInventory.setItem(45 + a, cosmeticItem);
		}

		return categoryInventory;
	}

	public void show(Player p) {
		p.openInventory(mainInventory);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.isCancelled())
			return;

		if (e.getInventory().equals(mainInventory)) {
			e.setCancelled(true);
			if (!(e.getWhoClicked() instanceof Player)) {
				e.getWhoClicked().closeInventory();
				return; // How this happened?
			}
			Player player = (Player) e.getWhoClicked();
			if (!Main.isPlayerInGame(player)) {
				player.closeInventory();
				return;
			}
			int clickedSlot = e.getSlot();
			Category category = categoryInventories.get(clickedSlot);
			if (category == null) {
				return;
			}
			player.closeInventory();
			player.openInventory(category.inv.get(0));
			return;
		}

		for (Category cat : categoryInventories.values()) {
			for (Inventory inv : cat.inv) {
				if (e.getInventory().equals(inv)) {
					e.setCancelled(true);
					if (!(e.getWhoClicked() instanceof Player)) {
						e.getWhoClicked().closeInventory();
						return; // How this happened?
					}
					Player player = (Player) e.getWhoClicked();
					if (!Main.isPlayerInGame(player)) {
						player.closeInventory();
						return;
					}
					int clickedSlot = e.getSlot();
					if (clickedSlot < 0) {
						return;
					}
					ItemStack stack = inv.getItem(clickedSlot);
					if (stack == null) {
						return;
					}
					Map<String, Object> itemInfo = null;
					if (Main.isLegacy()
							&& (stack.getType() == Material.ENDER_CHEST || stack.getType() == Material.CHEST)) {
						// FIX: unable to buy chest in legacy versions
						for (ItemStack item : cat.items.keySet()) {
							if (item.getType() == stack.getType()) {
								itemInfo = cat.items.get(item);
								break;
							}
						}
					} else {
						itemInfo = cat.items.get(stack);
					}
					if (itemInfo == null) {
						if (stack.equals(backItem)) {
							player.openInventory(mainInventory);
						}
						return;
					}
					int price = (int) itemInfo.get("price");
					ItemSpawnerType type = Main.getSpawnerType((String) itemInfo.get("price-type"));
					ItemStack materialItem = type.getStack();
					materialItem.setAmount(price);
					ItemStack newItem = (ItemStack) itemInfo.get("stack");
					if (player.getInventory().containsAtLeast(materialItem, price)) {
						if (itemInfo.containsKey("properties")) {
							Object properties = itemInfo.get("properties");
							if (properties instanceof List) {
								List<Object> propertiesList = (List<Object>) properties;
								for (Object obj : propertiesList) {
									if (obj instanceof Map) {
										Map<String, Object> propertyMap = (Map<String, Object>) obj;
										if (propertyMap.get("name") instanceof String) {
											BedwarsApplyPropertyToBoughtItem applyEvent = new BedwarsApplyPropertyToBoughtItem(
													Main.getPlayerGameProfile(player).getGame(), player, newItem,
													propertyMap);
											Main.getInstance().getServer().getPluginManager().callEvent(applyEvent);

											newItem = applyEvent.getStack();
										}
									}
								}
							}
						}
						player.getInventory().removeItem(materialItem);
						player.getInventory().addItem(newItem);
						player.sendMessage(i18n("buy_succes").replace("%item%",
								newItem.getItemMeta().hasDisplayName() ? newItem.getItemMeta().getDisplayName()
										: newItem.getType().name().toLowerCase()));
						Sounds.playSound(player, player.getLocation(),
								Main.getConfigurator().config.getString("sounds.on_item_buy"),
								Sounds.ENTITY_ITEM_PICKUP, 1, 1);
					} else {
						player.sendMessage(i18n("buy_failed").replace("%item%",
								newItem.getItemMeta().hasDisplayName() ? newItem.getItemMeta().getDisplayName()
										: newItem.getType().name().toLowerCase()));
					}
					return;
				}
			}
		}
	}
}