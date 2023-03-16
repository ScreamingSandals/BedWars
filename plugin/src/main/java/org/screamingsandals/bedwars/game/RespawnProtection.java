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

package org.screamingsandals.bedwars.game;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static org.screamingsandals.bedwars.lib.lang.I18n.i18nonly;

public class RespawnProtection extends BukkitRunnable {
    private Game game;
    private Player player;
    private int length;
    private boolean running = true;

    public RespawnProtection(Game game, Player player, int seconds) {
        this.game = game;
        this.player = player;
        this.length = seconds;
    }

    @Override
    public void run() {
    	if (!running) return;
        if (length > 0) {
            if (Main.getConfigurator().config.getBoolean("respawn.show-messages")) {
                MiscUtils.sendActionBarMessage(player, i18nonly("respawn_protection_remaining").replace("%time%", Integer.toString(length)));
            }
        }
        if (length <= 0) {
            if (Main.getConfigurator().config.getBoolean("respawn.show-messages")) {
                MiscUtils.sendActionBarMessage(player, i18nonly("respawn_protection_end"));
            }
            game.removeProtectedPlayer(player);
            running = false;
        }
        length--;
    }

    public void runProtection() {
        runTaskTimerAsynchronously(Main.getInstance(), 5L, 20L);
    }


}

