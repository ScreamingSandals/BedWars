/*
 * Copyright (C) 2025 ScreamingSandals
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

import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.events.PlayerRespawnedEventImpl;
import org.screamingsandals.bedwars.events.UpgradeLevelChangedEventImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
public class EnchantmentUpgradeHandler {
    @OnEvent
    public void handle(@NotNull UpgradeLevelChangedEventImpl event) {
        if (!(event.getUpgradable() instanceof TeamImpl)) {
            return;
        }

        var team = (TeamImpl) event.getUpgradable();
        var builtin = event.getGame().getGameVariant().getUpgrade(event.getName());

        if (!(builtin instanceof EnchantmentUpgradeDefinition)) {
            return;
        }

        var level = event.getNewLevel();
        if (level <= 0) {
            return;
        }

        var enchantment = ((EnchantmentUpgradeDefinition) builtin).getType();
        var applyTo = ((EnchantmentUpgradeDefinition) builtin).getApplyTo().toArray();

        team.getPlayers().forEach(player -> {
            var inventory = player.getPlayerInventory();
            var contents = inventory.getContents();

            for (int i = 0; i < contents.length; i++) {
                var item = contents[i];

                if (!item.is(applyTo)) {
                    continue;
                }

                inventory.setItem(i, item.withEnchantment(enchantment.asEnchantment((int) level)));
            }
        });
    }

    @OnEvent
    public void handle(@NotNull PlayerRespawnedEventImpl event) {
        var team = event.getGame().getPlayerTeam(event.getPlayer());
        if (team == null) {
            return;
        }

        var variant = event.getGame().getGameVariant();
        if (variant.getUpgrades().isEmpty()) {
            return;
        }

        Tasker.runDelayed(event.getPlayer(), () -> {
            for (var entry : team.getUpgrades().entrySet()) {
                var builtin = variant.getUpgrade(entry.getKey());
                if (!(builtin instanceof EnchantmentUpgradeDefinition)) {
                    continue;
                }

                var level = entry.getValue().getLevel();
                if (level <= 0) {
                    continue;
                }

                var enchantment = ((EnchantmentUpgradeDefinition) builtin).getType();
                var applyTo = ((EnchantmentUpgradeDefinition) builtin).getApplyTo().toArray();

                var inventory = event.getPlayer().getPlayerInventory();
                var contents = inventory.getContents();

                for (int i = 0; i < contents.length; i++) {
                    var item = contents[i];

                    if (!item.is(applyTo)) {
                        continue;
                    }

                    inventory.setItem(i, item.withEnchantment(enchantment.asEnchantment((int) level)));
                }
            }
        }, 1L, TaskerTime.TICKS);
    }
}
