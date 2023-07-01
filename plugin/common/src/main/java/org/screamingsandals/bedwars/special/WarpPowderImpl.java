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

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.screamingsandals.bedwars.api.special.WarpPowder;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.SpawnEffects;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.Task;

@Getter
@EqualsAndHashCode(callSuper = true)
public class WarpPowderImpl extends SpecialItemImpl implements WarpPowder {
    private final ItemStack item;
    private Task teleportingTask;
    private int teleportingTime;

    public WarpPowderImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, ItemStack item, int teleportingTime) {
        super(game, player, team);
        this.item = item;
        this.teleportingTime = teleportingTime;
    }

    @Override
    public void cancelTeleport(boolean showCancelMessage) {
        try {
            teleportingTask.cancel();
        } catch (Exception ignored) {
        }

        game.unregisterSpecialItem(this);

        if (showCancelMessage) {
            Message.of(LangKeys.SPECIALS_WARP_POWDER_CANCELED)
                    .prefixOrDefault(game.getCustomPrefixComponent())
                    .send(player);
        }
    }

    @Override
    public void runTask() {
        game.registerSpecialItem(this);

        Message.of(LangKeys.SPECIALS_WARP_POWDER_STARTED)
                .prefixOrDefault(game.getCustomPrefixComponent())
                .placeholder("time", teleportingTime)
                .send(player);

        teleportingTask = Tasker.runRepeatedly(DefaultThreads.GLOBAL_THREAD, () -> {
            if (teleportingTime == 0) {
                cancelTeleport(false);
                var stack = item.withAmount(1);
                try {
                    if (player.getPlayerInventory().getItemInOffHand().equals(stack)) {
                        player.getPlayerInventory().setItemInOffHand(ItemStackFactory.getAir());
                    } else {
                        player.getPlayerInventory().removeItem(stack);
                    }
                } catch (Throwable e) {
                    player.getPlayerInventory().removeItem(stack);
                }
                player.forceUpdateInventory();
                player.teleport(team.getRandomSpawn());
            } else {
                SpawnEffects.spawnEffect(game, player, "game-effects.warppowdertick");
                teleportingTime--;
            }
        }, 20, TaskerTime.TICKS);
    }
}
