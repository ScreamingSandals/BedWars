/*
 * Copyright (C) 2023 ScreamingSandals
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
import org.screamingsandals.bedwars.api.special.ArrowBlocker;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.game.TeamImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ArrowBlockerImpl extends SpecialItemImpl implements ArrowBlocker {
    private final int protectionTime;
    private int usedTime;
    private boolean isActivated;
    private final ItemStack item;

    public ArrowBlockerImpl(GameImpl game, BedWarsPlayer player, TeamImpl team, ItemStack item, int protectionTime) {
        super(game, player, team);
        this.item = item;
        this.protectionTime = protectionTime;
    }

    @Override
    public void runTask() {
        Tasker.runRepeatedly(DefaultThreads.GLOBAL_THREAD, task -> {
            usedTime++;
            if (usedTime == protectionTime) {
                isActivated = false;
                MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ARROW_BLOCKER_ENDED));

                game.unregisterSpecialItem(ArrowBlockerImpl.this);
                task.cancel();
            }
        }, 20, TaskerTime.TICKS);
    }

    public void activate() {
        if (protectionTime > 0) {
            game.registerSpecialItem(this);
            runTask();

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

            MiscUtils.sendActionBarMessage(player, Message.of(LangKeys.SPECIALS_ARROW_BLOCKER_STARTED).placeholder("time", protectionTime));
        }
    }
}
