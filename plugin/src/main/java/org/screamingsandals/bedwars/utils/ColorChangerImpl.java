package org.screamingsandals.bedwars.utils;

import org.screamingsandals.bedwars.api.TeamColor;
import org.screamingsandals.bedwars.api.utils.ColorChanger;
import org.screamingsandals.bedwars.game.TeamColorImpl;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class ColorChangerImpl implements ColorChanger<Item> {
    @Override
    public Item applyColor(TeamColor apiColor, Object item) {
        var color = (TeamColorImpl) apiColor;
        var newItem = item instanceof Item ? ((Item) item).clone() : ItemFactory.build(item).orElse(ItemFactory.getAir());
        if (newItem.getMaterial().is("LEATHER_BOOTS", "LEATHER_CHESTPLATE", "LEATHER_HELMET", "LEATHER_LEGGINGS")) {
            newItem = newItem.builder().color(color.getLeatherColor()).build().orElseThrow();
        } else {
            newItem = newItem.withType(newItem.getMaterial().colorize(color.material1_13));
        }
        return newItem;
    }
}
