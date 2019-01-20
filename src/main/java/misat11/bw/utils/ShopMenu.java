package misat11.bw.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
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
import misat11.bw.game.Game;
import misat11.bw.game.ItemSpawnerType;

import static misat11.bw.utils.I18n.i18n;

public class ShopMenu implements Listener {

	private Inventory mainInventory;
	private HashMap<Integer, Category> categoryInventories = new HashMap<Integer, Category>();
	private ItemStack backItem;

	public static class Category {
		public final Inventory inv;
		public final HashMap<ItemStack, Map<String, Object>> items = new HashMap<ItemStack, Map<String, Object>>();
		public int lastpos = 9;

		private Category(Inventory inv) {
			this.inv = inv;
		}

	}

	public ShopMenu(Plugin p) {
		mainInventory = Bukkit.getServer().createInventory(null, 54, "[BW] Shop");
		int lastpos = 0;

		Set<String> s = Main.getConfigurator().shopconfig.getConfigurationSection("shop-items").getKeys(false);
		
		backItem = new ItemStack(Material.BARRIER);
		ItemMeta backItemMeta = backItem.getItemMeta();
		backItemMeta.setDisplayName(i18n("shop_back", false));
		backItem.setItemMeta(backItemMeta);

		for (String i : s) {
			ConfigurationSection category = Main.getConfigurator().shopconfig
					.getConfigurationSection("shop-items." + i);

			ItemStack categoryItem = new ItemStack(Material.valueOf(category.getString("item")), 1);
			ItemMeta categoryItemMeta = categoryItem.getItemMeta();
			categoryItemMeta.setLore(category.getStringList("lore"));
			categoryItemMeta.setDisplayName(category.getString("name"));
			categoryItem.setItemMeta(categoryItemMeta);
			Inventory categoryInventory = Bukkit.getServer().createInventory(null, 54,
					"[BW] Shop: " + category.getString("name"));
			Category categoryClass = new Category(categoryInventory);
			
			categoryClass.inv.setItem(0, backItem);

			List<Map<String, Object>> categoryItemsList = (List<Map<String, Object>>) category.getList("items");
			for (Map<String, Object> item : categoryItemsList) {
				ItemStack stack = (ItemStack) item.get("stack");
				ItemMeta stackMeta = stack.getItemMeta();
				List<String> lore = new ArrayList<String>();
				if (stackMeta.hasLore()) {
					lore = stackMeta.getLore();
				}
				int price = (int) item.get("price");
				ItemSpawnerType type = Game.readTypeFromString((String) item.get("price-type"));
				lore.add(i18n("price", false));
				lore.add(price + "" + type.color + i18n("resource_"+type.name().toLowerCase(), false));
				lore.add(i18n("amount", false));
				lore.add(Integer.toString(stack.getAmount()));
				stackMeta.setLore(lore);
				stack.setItemMeta(stackMeta);
				categoryClass.items.put(stack, item);
				categoryClass.inv.setItem(categoryClass.lastpos, stack);
				categoryClass.lastpos++;
			}

			mainInventory.setItem(lastpos, categoryItem);
			categoryInventories.put(lastpos, categoryClass);
			lastpos++;
		}

		Bukkit.getServer().getPluginManager().registerEvents(this, p);
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
			player.openInventory(category.inv);
			return;
		}
		
		for (Category cat : categoryInventories.values()) {
			if (e.getInventory().equals(cat.inv)) {
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
				Inventory inv = e.getInventory();
				if (clickedSlot < 0) {
					return;
				}
				ItemStack stack = inv.getItem(clickedSlot);
				if (stack == null) {
					return;
				}
				Map<String, Object> itemInfo = cat.items.get(stack);
				if (itemInfo == null) {
					if (stack.equals(backItem)) {
						player.openInventory(mainInventory);
					}
					return;
				}
				int price = (int) itemInfo.get("price");
				ItemSpawnerType type = Game.readTypeFromString((String) itemInfo.get("price-type"));
				ItemStack materialItem = new ItemStack(type.material, price);
				ItemMeta materialItemMeta = materialItem.getItemMeta();
				materialItemMeta.setDisplayName(type.color + i18n("resource_" + type.name().toLowerCase(), false));
				materialItem.setItemMeta(materialItemMeta);
				ItemStack newItem = (ItemStack) itemInfo.get("stack");
				if (player.getInventory().containsAtLeast(materialItem, price)) {
					player.getInventory().removeItem(materialItem);
					player.getInventory().addItem(newItem);
					player.sendMessage(i18n("buy_succes").replace("%item%",
							newItem.getItemMeta().hasDisplayName() ? newItem.getItemMeta().getDisplayName() : newItem.getType().name().toLowerCase()));
					player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);
				} else {
					player.sendMessage(i18n("buy_failed").replace("%item%",
							newItem.getItemMeta().hasDisplayName() ? newItem.getItemMeta().getDisplayName() : newItem.getType().name().toLowerCase()));
				}
				return;
			}
		}
	}
}