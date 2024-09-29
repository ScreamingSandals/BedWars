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

import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.events.UpgradeLevelChangedEventImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.lib.event.OnEvent;
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

        team.getPlayers().forEach(player -> {
            var inventory = player.getPlayerInventory();
            var contents = inventory.getContents();

            for (int i = 0; i < contents.length; i++) {
                var item = contents[i];

                // TODO: remap items with logic from https://github.com/boiscljo/SBA/blob/release/plugin/src/main/java/io/github/pronze/sba/utils/ShopUtil.java#L206
                // inventory.setItem(i, item);
            }
        });
    }
}
