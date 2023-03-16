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

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DelayFactory implements org.screamingsandals.bedwars.api.utils.DelayFactory {
    private int delay;
    private SpecialItem specialItem;
    private Player player;
    private Game game;
    private boolean delayActive;

    public DelayFactory(int delay, SpecialItem specialItem, Player player, Game game) {
        this.delay = delay;
        this.specialItem = specialItem;
        this.player = player;
        this.game = game;

        runDelay();
    }

    @Override
    public boolean getDelayActive() {
        return delayActive;
    }

    @Override
    public SpecialItem getSpecialItem() {
        return specialItem;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public int getRemainDelay() {
        return delay;
    }

    private void runDelay() {
        new BukkitRunnable() {
            public void run() {
                if (delay > 0) {
                    delayActive = true;
                    delay--;
                    if (delay == 0) {
                        delayActive = false;

                        this.cancel();
                        game.unregisterDelay(DelayFactory.this);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 20L);
    }
}
