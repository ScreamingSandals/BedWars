/*
 * Copyright (C) 2022 ScreamingSandals
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
