/*
 * Copyright (C) 2024 ScreamingSandals
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
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.item.ItemType;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.lang.Translation;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.Pair;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class ItemSpawnerTypeImpl implements ItemSpawnerType {
    private final String configKey;
    private final String name;
    private final String translatableKey;
    private final double spread;
    private final ItemType itemType;
    private final Color color;
    private final Pair<Long, TaskerTime> interval;

    @Override
    public long getIntervalTicks() {
        return this.interval.second().getBukkitTime(this.interval.first());
    }

    public Component getTranslatableKey() {
        if (translatableKey != null && !translatableKey.isEmpty()) {
            return Message.of(Translation.of(Arrays.asList(translatableKey.split("_")), Component.fromLegacy(name))).asComponent();
        }
        return Component.text(name);
    }

    public Component getItemName() {
        return getTranslatableKey().withColor(color);
    }

    public Component getItemBoldName() {
        return getTranslatableKey().withColor(color).withBold(true);
    }

    public ItemStack getItem() {
        return getItem(1);
    }

    public ItemStack getItem(int amount) {
        return Objects.requireNonNull(ItemStackFactory.build(itemType, builder -> builder.name(getItemName()).amount(amount)));
    }

    @Override
    public ItemSpawnerTypeHolderImpl toHolder() {
        return new ItemSpawnerTypeHolderImpl(configKey);
    }

    public static ItemSpawnerTypeImpl deserialize(String spawnerKey, ConfigurationNode node) {
        spawnerKey = spawnerKey.toLowerCase(Locale.ROOT);

        var name = node.node("name").getString();
        var translate = node.node("translate").getString();
        Pair<Long, TaskerTime> interval;
        try {
            var integer = node.node("interval").get(Integer.class, 1);
            interval = Pair.of((long) integer, TaskerTime.SECONDS);
        } catch (ConfigurateException exception) {
            stringIntervalResolution: {
                var string = node.node("interval").getString();
                if (string != null) {
                    var split = string.split(" ", 2);
                    if (split.length == 2) {
                        try {
                            var longValue = Long.parseLong(split[0]);
                            var unitName = split[1].toUpperCase(Locale.ROOT).trim();
                            if (!unitName.endsWith("S")) {
                                unitName += "S";
                            }
                            var unit = TaskerTime.valueOf(unitName);
                            interval = Pair.of(longValue, unit);

                            break stringIntervalResolution;
                        } catch (IllegalArgumentException exception1) {
                        }
                    }
                }

                // default
                interval = Pair.of(1L, TaskerTime.SECONDS);
            }
        }
//        var interval = node.node("interval").getInt(1);
        var spread = node.node("spread").getDouble();
        var damage = node.node("damage").getInt();
        var materialName = node.node("material").getString();
        var colorName = node.node("color").getString();

        if (damage != 0) {
            materialName += ":" + damage;
        }

        var result = ItemType.ofNullable(materialName);
        if (result == null || result.isAir()) {
            return null; // no air
        }

        return new ItemSpawnerTypeImpl(
                spawnerKey,
                name,
                translate,
                spread,
                result,
                MiscUtils.getColor(colorName),
                interval
        );
    }
}
