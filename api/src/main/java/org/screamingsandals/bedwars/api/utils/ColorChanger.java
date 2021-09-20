package org.screamingsandals.bedwars.api.utils;

import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.TeamColor;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.lib.utils.Wrapper;


@ApiStatus.NonExtendable
public interface ColorChanger<I extends Wrapper> {

    @Deprecated
    ItemStack applyColor(TeamColor color, ItemStack stack);


    /**
     * Apply color of team to ItemStack
     *
     * @param color Color of team
     * @param stack ItemStack that should be colored
     * @return colored ItemStack or normal ItemStack if ItemStack can't be colored
     */
    I applyColor(TeamColor color, Object stack);
}
