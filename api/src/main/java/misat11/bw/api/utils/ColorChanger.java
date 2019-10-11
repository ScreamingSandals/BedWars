package misat11.bw.api.utils;

import misat11.bw.api.TeamColor;
import org.bukkit.inventory.ItemStack;


/**
 * @author Bedwars Team
 */
public interface ColorChanger {

    /**
     * Apply color of team to ItemStack
     *
     * @param color Color of team
     * @param stack ItemStack that should be colored
     * @return colored ItemStack or normal ItemStack if ItemStack can't be colored
     */
    ItemStack applyColor(TeamColor color, ItemStack stack);
}
