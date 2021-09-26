package org.screamingsandals.bedwars.utils;

import lombok.experimental.UtilityClass;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.lib.entity.EntityLiving;
import org.screamingsandals.lib.item.builder.ItemFactory;

@UtilityClass
public class ArmorStandUtils {
    public void equip(EntityLiving entity, TeamImpl team) {
        var helmet = ItemFactory.build("leather_helmet", builder -> builder.color(team.getColor().getLeatherColor())).orElseThrow();
        var chestplate = ItemFactory.build("leather_chestplate", builder -> builder.color(team.getColor().getLeatherColor())).orElseThrow();
        var leggings = ItemFactory.build("leather_leggings", builder -> builder.color(team.getColor().getLeatherColor())).orElseThrow();
        var boots = ItemFactory.build("leather_boots", builder -> builder.color(team.getColor().getLeatherColor())).orElseThrow();

        entity.setHelmet(helmet);
        entity.setChestplate(chestplate);
        entity.setLeggings(leggings);
        entity.setBoots(boots);
    }
}
