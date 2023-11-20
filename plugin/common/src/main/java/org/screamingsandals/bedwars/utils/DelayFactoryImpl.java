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

package org.screamingsandals.bedwars.utils;

import lombok.Data;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.api.utils.DelayFactory;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.Task;

@Data
public class DelayFactoryImpl implements DelayFactory {
    private int remainDelay;
    private final SpecialItem specialItem;
    private final BedWarsPlayer player;
    private final GameImpl game;
    private boolean delayActive;
    private Task task;

    public DelayFactoryImpl(int remainDelay, SpecialItem specialItem, BedWarsPlayer player, GameImpl game) {
        this.remainDelay = remainDelay;
        this.specialItem = specialItem;
        this.player = player;
        this.game = game;

        runDelay();
    }

    private void runDelay() {
        task = Tasker.runRepeatedly(DefaultThreads.GLOBAL_THREAD, () -> {
            if (remainDelay > 0) {
                delayActive = true;
                remainDelay--;
                if (remainDelay == 0) {
                    delayActive = false;

                    task.cancel();
                    game.unregisterDelay(this);
                }
            }
        }, 20, TaskerTime.TICKS);
    }
}
