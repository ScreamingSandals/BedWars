package org.screamingsandals.bedwars.api.events;

import org.screamingsandals.bedwars.api.game.Game;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * @author Bedwars Team
 */
public class BedwarsApplyPropertyToBoughtItem extends BedwarsApplyPropertyToItem {

    /**
     * @param game
     * @param player
     * @param stack
     * @param properties
     */
    public BedwarsApplyPropertyToBoughtItem(Game game, Player player, ItemStack stack, String name, Map<String, Object> properties) {
        super(game, player, stack, name, properties);
    }
}
