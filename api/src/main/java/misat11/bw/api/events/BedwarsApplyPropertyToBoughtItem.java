package misat11.bw.api.events;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import misat11.bw.api.Game;

/**
 * @author Bedwars Team
 *
 */
public class BedwarsApplyPropertyToBoughtItem extends BedwarsApplyPropertyToItem {

	/**
	 * @param game
	 * @param player
	 * @param stack
	 * @param properties
	 */
	public BedwarsApplyPropertyToBoughtItem(Game game, Player player, ItemStack stack, Map<String, Object> properties) {
		super(game, player, stack, properties);
	}
}
