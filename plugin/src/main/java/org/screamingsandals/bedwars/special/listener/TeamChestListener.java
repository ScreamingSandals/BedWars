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

package org.screamingsandals.bedwars.special.listener;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.events.BedwarsApplyPropertyToBoughtItem;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerBuildBlock;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import static org.screamingsandals.bedwars.lib.lang.I.i18nc;
import static org.screamingsandals.bedwars.lib.lang.I18n.i18n;

public class TeamChestListener implements Listener {

    public static final String TEAM_CHEST_PREFIX = "Module:TeamChest:";

    @EventHandler
    public void onMagnetShoesRegistered(BedwarsApplyPropertyToBoughtItem event) {
        if (event.getPropertyName().equalsIgnoreCase("teamchest")) {
            ItemStack stack = event.getStack();

            APIUtils.hashIntoInvisibleString(stack, TEAM_CHEST_PREFIX);
        }
    }

    @EventHandler
    public void onTeamChestBuilt(BedwarsPlayerBuildBlock event) {
        if (event.isCancelled()) {
            return;
        }

        Block block = event.getBlock();
        RunningTeam team = event.getTeam();

        if (block.getType() != Material.ENDER_CHEST) {
            return;
        }

        String unhidden = APIUtils.unhashFromInvisibleStringStartsWith(event.getItemInHand(), TEAM_CHEST_PREFIX);

        if (unhidden != null || Main.getConfigurator().config.getBoolean("specials.teamchest.turn-all-enderchests-to-teamchests")) {
            team.addTeamChest(block);
            String message = i18nc("team_chest_placed", event.getGame().getCustomPrefix());
            for (Player pl : team.getConnectedPlayers()) {
                pl.sendMessage(message);
            }
        }
    }

}
