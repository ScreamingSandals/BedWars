/*
 * Copyright (C) 2023 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

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
