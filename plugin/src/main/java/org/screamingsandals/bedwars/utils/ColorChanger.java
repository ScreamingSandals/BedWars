package org.screamingsandals.bedwars.utils;

import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.game.TeamColorImpl;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.item.builder.ItemFactory;

public class ColorChanger implements org.screamingsandals.bedwars.api.utils.ColorChanger<Item> {
    @Deprecated
    public static ItemStack changeLegacyStackColor(ItemStack itemStack, TeamColorImpl teamColor) {
        Material material = itemStack.getType();
        String materialName = material.name();

        if (BedWarsPlugin.autoColoredMaterials.contains(materialName)) {
            itemStack.setDurability((short) teamColor.woolData);
        } else if (material.toString().contains("GLASS")) {
            itemStack.setType(Material.getMaterial("STAINED_GLASS"));
            itemStack.setDurability((short) teamColor.woolData);
        } else if (material.toString().contains("GLASS_PANE")) {
            itemStack.setType(Material.getMaterial("STAINED_GLASS_PANE"));
            itemStack.setDurability((short) teamColor.woolData);
        }
        return itemStack;
    }

    @Deprecated
    public static Material changeMaterialColor(Material material, TeamColorImpl teamColor) {
        String materialName = material.name();

        try {
            materialName = material.toString().substring(material.toString().indexOf("_") + 1);
        } catch (StringIndexOutOfBoundsException ignored) {
        }

        String teamMaterialColor = teamColor.material1_13;

        if (BedWarsPlugin.autoColoredMaterials.contains(materialName)) {
            return Material.getMaterial(teamMaterialColor + "_" + materialName);
        } else if (material.toString().contains("GLASS")) {
            return Material.getMaterial(teamMaterialColor + "_STAINED_GLASS");
        } else if (material.toString().contains("GLASS_PANE")) {
            return Material.getMaterial(teamMaterialColor + "_STAINED_GLASS_PANE");
        }
        return material;

    }

    @Deprecated
    public static ItemStack changeLeatherArmorColor(ItemStack itemStack, TeamColorImpl color) {
        Material material = itemStack.getType();

        if (material.toString().contains("LEATHER_") && !material.toString().contains("LEATHER_HORSE_")) {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();

            meta.setColor(color.leatherColor);
            itemStack.setItemMeta(meta);

            return itemStack;
        }
        return itemStack;
    }

    @Deprecated
    @Override
    public ItemStack applyColor(org.screamingsandals.bedwars.api.TeamColor apiColor, ItemStack stack) {
        try {
            TeamColorImpl color = (TeamColorImpl) apiColor;
            Material material = stack.getType();
            if (BedWarsPlugin.isLegacy()) {
                stack = changeLegacyStackColor(stack, color);
            } else {
                stack.setType(changeMaterialColor(material, color));
            }
            stack = changeLeatherArmorColor(stack, color);
            return stack;
        } catch (NullPointerException e) {
            Debug.warn("DEFINED ITEM DOES NOT EXISTS. CHECK YOUR CONFIG.");
            e.printStackTrace();
            return new ItemStack(Material.BLACK_WOOL);
        }
    }

    @Override
    public Item applyColor(org.screamingsandals.bedwars.api.TeamColor apiColor, Object item) {
        var color = (TeamColorImpl) apiColor;
        var newItem = item instanceof Item ? ((Item) item).clone() : ItemFactory.build(item).orElse(ItemFactory.getAir());
        if (newItem.getMaterial().is("LEATHER_BOOTS", "LEATHER_CHESTPLATE", "LEATHER_HELMET", "LEATHER_LEGGINGS")) {
            newItem.setColor(color.getLeatherColor());
        } else {
            newItem.setMaterial(newItem.getMaterial().colorize(color.material1_13));
        }
        return newItem;
    }
}
