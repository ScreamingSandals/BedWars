package misat11.bw.utils;

import misat11.bw.game.Team;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArmorStandUtils {
    public static void equipArmorStand(ArmorStand stand, Team team) {
        if (stand == null || team == null) {
            return;
        }

        // helmet
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET, 1);
        LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();
        meta.setColor(team.color.leatherColor);
        helmet.setItemMeta(meta);

        // chestplate
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        meta = (LeatherArmorMeta) chestplate.getItemMeta();
        meta.setColor(team.color.leatherColor);
        chestplate.setItemMeta(meta);

        // leggings
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS, 1);
        meta = (LeatherArmorMeta) leggings.getItemMeta();
        meta.setColor(team.color.leatherColor);
        leggings.setItemMeta(meta);

        // boots
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
        meta = (LeatherArmorMeta) boots.getItemMeta();
        meta.setColor(team.color.leatherColor);
        boots.setItemMeta(meta);

        stand.setHelmet(helmet);
        stand.setChestplate(chestplate);
        stand.setLeggings(leggings);
        stand.setBoots(boots);
    }
}
