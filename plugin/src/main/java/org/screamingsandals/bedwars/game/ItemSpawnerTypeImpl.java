package org.screamingsandals.bedwars.game;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.lib.item.ItemTypeHolder;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.lang.Translation;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.adventure.wrapper.ComponentWrapper;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public class ItemSpawnerTypeImpl implements ItemSpawnerType<ItemTypeHolder, Item, ComponentWrapper> {
    private final String configKey;
    private final String name;
    private final String translatableKey;
    private final double spread;
    private final ItemTypeHolder itemType;
    private final TextColor color;
    private final int interval;
    private final int damage;

    public ComponentWrapper getTranslatableKey() {
        if (translatableKey != null && !translatableKey.equals("")) {
            return new ComponentWrapper(Message.of(Translation.of(Arrays.asList(translatableKey.split("_")), AdventureHelper.toComponent(name))).asComponent());
        }
        return new ComponentWrapper(Component.text(name));
    }

    public ComponentWrapper getItemName() {
        return new ComponentWrapper(getTranslatableKey().asComponent().color(color));
    }

    public ComponentWrapper getItemBoldName() {
        return new ComponentWrapper(getTranslatableKey().asComponent().color(color).decorate(TextDecoration.BOLD));
    }

    public Item getItem() {
        return getItem(1);
    }

    public Item getItem(int amount) {
        return ItemFactory.build(itemType, builder -> builder.name(getItemName().asComponent()).amount(amount)).orElseThrow();
    }
}
