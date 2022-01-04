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

package org.screamingsandals.bedwars.special;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.utils.SpawnEffects;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;

import static org.screamingsandals.bedwars.lib.lang.I.i18nc;

public class WarpPowder extends SpecialItem implements org.screamingsandals.bedwars.api.special.WarpPowder {
    private BukkitTask teleportingTask = null;
    private int teleportingTime;

    private ItemStack item;

    public WarpPowder(Game game, Player player, Team team, ItemStack item, int teleportingTime) {
        super(game, player, team);
        this.item = item;
        this.teleportingTime = teleportingTime;
    }

    @Override
    public ItemStack getStack() {
        return item;
    }

    @Override
    public void cancelTeleport(boolean showCancelledMessage) {
        try {
            teleportingTask.cancel();
        } catch (Exception ignored) {

        }

        game.unregisterSpecialItem(this);

        if (showCancelledMessage) {
            player.sendMessage(i18nc("specials_warp_powder_canceled", game.getCustomPrefix()));
        }
    }

    @Override
    public void runTask() {
        game.registerSpecialItem(this);

        player.sendMessage(i18nc("specials_warp_powder_started", game.getCustomPrefix()).replace("%time%", Double.toString(teleportingTime)));

        teleportingTask = new BukkitRunnable() {

            @Override
            public void run() {
                if (teleportingTime == 0) {
                    cancelTeleport(false);
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        try {
                            if (player.getInventory().getItemInOffHand().equals(item)) {
                                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
                            } else {
                                player.getInventory().remove(item);
                            }
                        } catch (Throwable e) {
                            player.getInventory().remove(item);
                        }
                    }
                    player.updateInventory();
                    PlayerUtils.teleportPlayer(player, team.getTeamSpawn());
                } else {
                    SpawnEffects.spawnEffect(game, player, "game-effects.warppowdertick");
                    teleportingTime--;
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }
}
