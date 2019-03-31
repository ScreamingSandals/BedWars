package misat11.bw.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

	private List<Inventory> inv = new ArrayList<Inventory>();
	private HashMap<ItemStack, Category> categoryInventories = new HashMap<ItemStack, Category>();
	private ItemStack backItem, pageBackItem, pageForwardItem, cosmeticItem;

	public static final int ITEMS_ON_PAGE = 36;

	public static class Category {
		public final List<Inventory> inv = new ArrayList<Inventory>();
		public final HashMap<ItemStack, Map<String, Object>> items = new HashMap<ItemStack, Map<String, Object>>();
		public int lastpos = 0;
		public int onPage = 0;

		private Category() {
		}

	}

	public ShopMenu(Plugin p) {
		int lastpos = 0;

		List<Map<String, Object>> categoriesList = (List<Map<String, Object>>) Main.getConfigurator().shopconfig
				.getList("data");

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

		createMainInventoryOnPage(1); // Force first page

		for (Map<String, Object> category : categoriesList) {
			ItemStack categoryItem = (ItemStack) category.get("stack");

			int positionC = lastpos;
			int linebreakC = 0;
			int pagebreakC = 0;
			if (category.containsKey("linebreak")) {
				String lnBreak = (String) category.get("linebreak");
				if ("before".equalsIgnoreCase(lnBreak)) {
					linebreakC = 1;
				} else if ("after".equalsIgnoreCase(lnBreak)) {
					linebreakC = 2;
				} else if ("both".equalsIgnoreCase(lnBreak)) {
					linebreakC = 3;
				}
			}
			if (category.containsKey("pagebreak")) {
				String pgBreak = (String) category.get("pagebreak");
				if ("before".equalsIgnoreCase(pgBreak)) {
					pagebreakC = 1;
				} else if ("after".equalsIgnoreCase(pgBreak)) {
					pagebreakC = 2;
				} else if ("both".equalsIgnoreCase(pgBreak)) {
					pagebreakC = 3;
				}
			}
			if (pagebreakC == 1 || pagebreakC == 3) {
				positionC += (ITEMS_ON_PAGE - (positionC % ITEMS_ON_PAGE));
			}
			if (category.containsKey("row")) {
				positionC = positionC - (positionC % ITEMS_ON_PAGE) + (((int) category.get("row") - 1) * 9)
						+ (positionC % 9);
			}
			if (category.containsKey("column")) {
				Object cl = category.get("column");
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

				positionC = (positionC - (positionC % 9)) + column;
			}
			if (linebreakC == 1 || linebreakC == 3) {
				positionC += (9 - (positionC % 9));
			}
			if (category.containsKey("skip")) {
				positionC += (int) category.get("skip");
			}
			createMainInventoryOnPage((positionC / ITEMS_ON_PAGE) + 1).setItem(9 + positionC % ITEMS_ON_PAGE,
					categoryItem);

			List<Map<String, Object>> categoryItemsList = (List<Map<String, Object>>) category.get("items");
			if (categoryItemsList != null) {
				Category categoryClass = new Category();
				categoryClass.onPage = (positionC / ITEMS_ON_PAGE) + 1;
				createInventoryOnPage(1, categoryClass, categoryItem.getItemMeta().getDisplayName()); // Force first page
				
				categoryInventories.put(categoryItem, categoryClass);
				for (Map<String, Object> item : categoryItemsList) {
					ItemStack stack = ((ItemStack) item.get("stack")).clone();
					if (item.containsKey("price") && item.containsKey("price-type")) {
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
					}
					int position = categoryClass.lastpos;
					int linebreak = 0;
					int pagebreak = 0;
					if (item.containsKey("linebreak")) {
						String lnBreak = (String) item.get("linebreak");
						if ("before".equalsIgnoreCase(lnBreak)) {
							linebreak = 1;
						} else if ("after".equalsIgnoreCase(lnBreak)) {
							linebreak = 2;
						} else if ("both".equalsIgnoreCase(lnBreak)) {
							linebreak = 3;
						}
					}
					if (item.containsKey("pagebreak")) {
						String pgBreak = (String) item.get("pagebreak");
						if ("before".equalsIgnoreCase(pgBreak)) {
							pagebreak = 1;
						} else if ("after".equalsIgnoreCase(pgBreak)) {
							pagebreak = 2;
						} else if ("both".equalsIgnoreCase(pgBreak)) {
							pagebreak = 3;
						}
					}
					if (pagebreak == 1 || pagebreak == 3) {
						position += (ITEMS_ON_PAGE - (position % ITEMS_ON_PAGE));
					}
					if (item.containsKey("row")) {
						position = position - (position % ITEMS_ON_PAGE) + (((int) item.get("row") - 1) * 9)
								+ (position % 9);
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
					createInventoryOnPage((position / ITEMS_ON_PAGE) + 1, categoryClass,
							categoryItem.getItemMeta().getDisplayName()).setItem(9 + position % ITEMS_ON_PAGE, stack);
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
			}
			int nextPosition = positionC;
			if (pagebreakC >= 2) {
				nextPosition += (ITEMS_ON_PAGE - (nextPosition % ITEMS_ON_PAGE));
			}
			if (linebreakC >= 2) {
				nextPosition += (9 - (nextPosition % 9));
			}
			if (pagebreakC < 2 && linebreakC < 2) {
				nextPosition++;
			}
			lastpos = nextPosition;
		}

		inv.get(inv.size() - 1).setItem(53, cosmeticItem);

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

	private Inventory createMainInventoryOnPage(int page) {
		if (inv.size() >= page) {
			return inv.get(page - 1);
		}

		Inventory mainInventory = Bukkit.getServer().createInventory(null, 54, "[BW] Shop - " + page);

		inv.add(mainInventory);

		for (int a = 0; a <= 8; a++) {
			mainInventory.setItem(a, cosmeticItem);
		}

		if (page > 1) {
			mainInventory.setItem(45, pageBackItem);
			inv.get(page - 2).setItem(53, pageForwardItem);
		} else {
			mainInventory.setItem(45, cosmeticItem);
		}

		for (int a = 1; a <= 7; a++) {
			mainInventory.setItem(45 + a, cosmeticItem);
		}

		return mainInventory;
	}

	public void show(Player p) {
		p.openInventory(this.inv.get(0));
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		if (e.isCancelled())
			return;

		for (int i = 0; i < this.inv.size(); i++) {
			Inventory inv = this.inv.get(i);
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
				Category category = categoryInventories.get(stack);
				if (category == null) {
					if (stack.equals(pageBackItem) && i > 0) {
						player.openInventory(this.inv.get(i - 1));
					} else if (stack.equals(pageForwardItem) && i < (this.inv.size() - 1)) {
						player.openInventory(this.inv.get(i + 1));
					}
					return;
				}
				player.closeInventory();
				player.openInventory(category.inv.get(0));
				return;
			}
		}

		for (Category cat : categoryInventories.values()) {
			for (int i = 0; i < cat.inv.size(); i++) {
				Inventory inv = cat.inv.get(i);
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
							player.openInventory(this.inv.get(cat.onPage - 1));
						} else if (stack.equals(pageBackItem) && i > 0) {
							player.openInventory(cat.inv.get(i - 1));
						} else if (stack.equals(pageForwardItem) && i < (cat.inv.size() - 1)) {
							player.openInventory(cat.inv.get(i + 1));
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