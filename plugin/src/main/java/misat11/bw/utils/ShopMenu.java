package misat11.bw.utils;

import static misat11.bw.utils.I18n.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import misat11.bw.Main;
import misat11.bw.api.ItemSpawnerType;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.lib.sgui.ItemData;
import misat11.lib.sgui.ItemInfo;
import misat11.lib.sgui.Property;
import misat11.lib.sgui.SimpleGuiFormat;
import misat11.lib.sgui.StaticGuiCreator;
import misat11.lib.sgui.StaticInventoryListener;
import misat11.lib.sgui.events.GenerateItemEvent;
import misat11.lib.sgui.events.PostActionEvent;
import misat11.lib.sgui.events.PreActionEvent;

public class ShopMenu implements Listener {
	private ItemStack backItem, pageBackItem, pageForwardItem, cosmeticItem;
	private SimpleGuiFormat format;
	private StaticGuiCreator creator;
	private StaticInventoryListener listener;

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

		format = new SimpleGuiFormat(data);
		format.generateData();

		creator = new StaticGuiCreator("[BW] Shop", format, backItem, pageBackItem, pageForwardItem, cosmeticItem);
		creator.generate();

		listener = new StaticInventoryListener(creator);
		Bukkit.getServer().getPluginManager().registerEvents(listener, Main.getInstance());
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.getInstance());
	}
	
	@EventHandler
	public void onGeneratingItem(GenerateItemEvent event) {
		ItemInfo item = event.getInfo();
		ItemData data = item.getData();
		Map<String, Object> originalItemData = data.getData();
		if (originalItemData.containsKey("price") && originalItemData.containsKey("price-type")) {
			ItemStack stack = event.getStack();
			ItemMeta stackMeta = stack.getItemMeta();
			List<String> lore = new ArrayList<String>();
			if (stackMeta.hasLore()) {
				lore = stackMeta.getLore();
			}
			int price = (int) originalItemData.get("price");
			ItemSpawnerType type = Main.getSpawnerType(((String) originalItemData.get("price-type")).toLowerCase());
			if (type == null) {
				return;
			}

			lore.add(i18n("price", false));
			lore.add(price + " " + type.getItemName());
			lore.add(i18n("amount", false));
			lore.add(Integer.toString(stack.getAmount()));
			stackMeta.setLore(lore);
			stack.setItemMeta(stackMeta);
			event.setStack(stack);
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
	}

	@EventHandler
	public void onPostAction(PostActionEvent event) {
		if (event.getFormat() != format || event.isCancelled()) {
			return;
		}

		Player player = event.getPlayer();
		
		ItemInfo item = event.getItem();
		ItemData data = item.getData();
		Map<String, Object> originalItemData = data.getData();
		if (originalItemData.containsKey("price") && originalItemData.containsKey("price-type")) {
			int price = (int) originalItemData.get("price");
			String priceType = ((String) originalItemData.get("price-type")).toLowerCase();
			ItemSpawnerType type = Main.getSpawnerType(priceType);
			ItemStack materialItem = type.getStack();
			materialItem.setAmount(price);
			ItemStack newItem = (ItemStack) originalItemData.get("stack");
			if (player.getInventory().containsAtLeast(materialItem, price)) {
				if (data.hasProperties()) {
					for (Property property : data.getProperties()) {
						if (property.hasName()) {
							BedwarsApplyPropertyToBoughtItem applyEvent = new BedwarsApplyPropertyToBoughtItem(
									Main.getPlayerGameProfile(player).getGame(), player, newItem,
									property.getPropertyData());
							Main.getInstance().getServer().getPluginManager().callEvent(applyEvent);

							newItem = applyEvent.getStack();
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
		}
	}

	public void show(Player p) {
		p.openInventory(creator.getInventories(null).get(0));
	}
}
