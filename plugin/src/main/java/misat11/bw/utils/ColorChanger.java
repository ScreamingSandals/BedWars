package misat11.bw.utils;

import misat11.bw.game.TeamColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

public class ColorChanger {
    private static List<Material> materialList = new ArrayList<>();

    public static ItemStack changeLegacyStackColor(ItemStack itemStack, TeamColor teamColor) {
        Material material = itemStack.getType();
        String materialName = material.name();

        if (materialList.contains(materialName)) {
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

    public static Material changeStackColor(ItemStack itemStack, TeamColor teamColor) {
        Material material = itemStack.getType();
        String materialName = material.toString();

        try {
            materialName = material.toString().substring(material.toString().indexOf("_") + 1);
        } catch (StringIndexOutOfBoundsException e) {
        }

        String teamMaterialColor = teamColor.material1_13;

        if (materialList.contains(materialName)) {
            return Material.getMaterial(teamMaterialColor + "_" + materialName);
        } else if (material.toString().contains("GLASS")) {
            return Material.getMaterial(teamMaterialColor + "_STAINED_GLASS");
        } else if (material.toString().contains("GLASS_PANE")) {
            return Material.getMaterial(teamMaterialColor + "_STAINED_GLASS_PANE");
        }
        return material;

    }

    public static ItemStack changeLeatherArmorColor(ItemStack itemStack, TeamColor color) {
        Material material = itemStack.getType();

        if (material.toString().contains("LEATHER_") && !material.toString().contains("LEATHER_HORSE_")) {
            LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();

            meta.setColor(color.leatherColor);
            itemStack.setItemMeta(meta);

            return itemStack;
        }
        return itemStack;
    }
}
