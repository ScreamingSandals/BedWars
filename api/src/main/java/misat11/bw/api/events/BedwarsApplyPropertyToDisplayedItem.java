package misat11.bw.api.events;

import misat11.bw.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * @author Bedwars Team
 */
public class BedwarsApplyPropertyToDisplayedItem extends BedwarsApplyPropertyToItem {

    /**
     * @param game
     * @param player
     * @param stack
     * @param properties
     */
    public BedwarsApplyPropertyToDisplayedItem(Game game, Player player, ItemStack stack, Map<String, Object> properties) {
        super(game, player, stack, properties);
    }
}
