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

package org.screamingsandals.bedwars.game.upgrade.builtin;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.game.upgrade.Upgradable;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.lib.item.meta.EnchantmentType;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.Objects;

@RequiredArgsConstructor
@Getter
public class EnchantmentUpgradeDefinition implements BuiltInUpgradeDefinition {
    private static final @NotNull String ENCHANTMENT_KEY = "enchantment";
    private static final @NotNull String MAX_LEVEL_KEY = "max-level";

    private final @NotNull EnchantmentType type;
    private final int maxLevel;

    @Override
    public double getInitialLevel() {
        return 0;
    }

    @Override
    public @Nullable Double getMaximalLevel() {
        return (double) maxLevel;
    }

    @Override
    public boolean isApplicable(@NotNull Upgradable upgradable) {
        return upgradable instanceof TeamImpl;
    }

    public static class Loader implements BuiltInUpgradeDefinition.Loader<EnchantmentUpgradeDefinition> {
        public static final @NotNull Loader INSTANCE = new Loader();

        @Override
        public @NotNull EnchantmentUpgradeDefinition load(@NotNull ConfigurationNode node) throws ConfigurateException {
            return new EnchantmentUpgradeDefinition(
                EnchantmentType.of(Objects.requireNonNull(node.node(ENCHANTMENT_KEY).get(String.class))),
                node.node(MAX_LEVEL_KEY).getInt(1)
            );
        }
    }
}
