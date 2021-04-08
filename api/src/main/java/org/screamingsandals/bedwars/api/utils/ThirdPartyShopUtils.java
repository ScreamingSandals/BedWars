package org.screamingsandals.bedwars.api.utils;

import org.screamingsandals.bedwars.api.BedwarsAPI;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToDisplayedItem;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Bedwars Team
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
        var api = BedwarsAPI.getInstance().getPlayerManager();
        if (!api.isPlayerInGame(player.getUniqueId())) {
            return stack;
        }

        Game game = api.getGameOfPlayer(player.getUniqueId()).orElseThrow();
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
            event = new BedwarsApplyPropertyToBoughtItem(game, player, stack, propertyName, map);
        } else {
            event = new BedwarsApplyPropertyToDisplayedItem(game, player, stack, propertyName, map);
        }
        Bukkit.getPluginManager().callEvent(event);

        return event.getStack();
    }
}
