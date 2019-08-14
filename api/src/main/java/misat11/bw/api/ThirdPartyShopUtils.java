package misat11.bw.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.api.events.BedwarsApplyPropertyToDisplayedItem;
import misat11.bw.api.events.BedwarsApplyPropertyToItem;

/**
 * @author Bedwars Team
 *
 */
public class ThirdPartyShopUtils {
	/**
	 * @param player
	 * @param stack
	 * @param propertyName
	 * @param onBuy
	 * @param entries
	 * @return
	 */
	public static ItemStack applyPropertyToItem(Player player, ItemStack stack, String propertyName, boolean onBuy,
			Object... entries) {
		BedwarsAPI api = BedwarsAPI.getInstance();
		if (!api.isPlayerPlayingAnyGame(player)) {
			return stack;
		}

		Game game = api.getGameOfPlayer(player);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("name", propertyName);

		String lastEntry = null;
		for (Object obj : entries) {
			if (lastEntry == null) {
				if (obj instanceof String) {
					lastEntry = (String) obj;
				}
			} else {
				map.put(lastEntry, obj);
				lastEntry = null;
			}
		}

		BedwarsApplyPropertyToItem event;
		if (onBuy) {
			event = new BedwarsApplyPropertyToBoughtItem(game, player, stack, map);
		} else {
			event = new BedwarsApplyPropertyToDisplayedItem(game, player, stack, map);
		}
		Bukkit.getPluginManager().callEvent(event);

		return event.getStack();
	}
}
