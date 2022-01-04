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

package org.screamingsandals.bedwars.listener;

import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.game.CurrentTeam;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.Team;
import org.screamingsandals.bedwars.utils.Sounds;

public class Player116ListenerUtils {
    public static boolean processAnchorDeath(Game game, CurrentTeam team, boolean isBed) {
        RespawnAnchor anchor = (RespawnAnchor) team.teamInfo.bed.getBlock().getBlockData();
        int charges = anchor.getCharges();
        if (charges <= 0) {
            isBed = false;
        } else {
            anchor.setCharges(charges - 1);
            team.teamInfo.bed.getBlock().setBlockData(anchor);
            if (anchor.getCharges() == 0) {
                Sounds.playSound(team.teamInfo.bed, Main.getConfigurator().config.getString("target-block.respawn-anchor.sound.deplete"), Sounds.BLOCK_RESPAWN_ANCHOR_DEPLETE, 1, 1);
                game.updateScoreboard();
            } else {
                Sounds.playSound(team.teamInfo.bed, Main.getConfigurator().config.getString("target-block.respawn-anchor.sound.used"), Sounds.BLOCK_GLASS_BREAK, 1, 1);
            }
        }
        return isBed;
    }

    public static boolean anchorCharge(PlayerInteractEvent event, Game game, ItemStack stack) {
        boolean anchorFilled = false;
        RespawnAnchor anchor = (RespawnAnchor) event.getClickedBlock().getBlockData();
        int charges = anchor.getCharges();
        charges++;
        if (charges <= anchor.getMaximumCharges()) {
            anchorFilled = true;
            anchor.setCharges(charges);
            event.getClickedBlock().setBlockData(anchor);
            stack.setAmount(stack.getAmount() - 1);
            Sounds.playSound(event.getClickedBlock().getLocation(), Main.getConfigurator().config.getString("target-block.respawn-anchor.sound.charge"), Sounds.BLOCK_RESPAWN_ANCHOR_CHARGE, 1, 1);
            game.updateScoreboard();
        }
        return anchorFilled;
    }

    public static boolean isAnchorEmpty(Block anchor) {
        return ((RespawnAnchor) anchor.getBlockData()).getCharges() == 0;
    }
}
