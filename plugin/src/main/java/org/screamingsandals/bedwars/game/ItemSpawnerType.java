package org.screamingsandals.bedwars.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.lang.Translation;
import org.screamingsandals.lib.utils.AdventureHelper;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public class ItemSpawnerType implements org.screamingsandals.bedwars.api.game.ItemSpawnerType {
    private final String configKey;
    private final String name;
    private final String translatableKey;
    private final double spread;
    private final Material material;
    private final ChatColor color;
    private final int interval;
    private final int damage;

    public String getTranslatableKey() {
        if (translatableKey != null && !translatableKey.equals("")) {
            return AdventureHelper.toLegacy(Message.of(Translation.of(Arrays.asList(translatableKey.split("_")), AdventureHelper.toComponent(name))).asComponent());
        }
        return name;
    }

    public String getItemName() {
        return color + getTranslatableKey();
    }

    public String getItemBoldName() {
        return color.toString() + ChatColor.BOLD.toString() + getTranslatableKey();
    }

    public ItemStack getStack() {
        return getStack(1);
    }

    public ItemStack getStack(int amount) {
        ItemStack stack = new ItemStack(material, amount, (short) damage);
        ItemMeta stackMeta = stack.getItemMeta();
        stackMeta.setDisplayName(getItemName());
        stack.setItemMeta(stackMeta);
        return stack;
    }
}
