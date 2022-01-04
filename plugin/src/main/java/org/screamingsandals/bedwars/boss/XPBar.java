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

package org.screamingsandals.bedwars.boss;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;

import java.util.ArrayList;
import java.util.List;

public class XPBar implements org.screamingsandals.bedwars.api.boss.XPBar {

    private boolean visible = false;
    private float progress = 0F;
    private int seconds = 0;
    private List<Player> players = new ArrayList<>();

    @Override
    public void addPlayer(Player player) {
        if (!players.contains(player)) {
            players.add(player);
            if (visible) {
                PlayerUtils.fakeExp(player, progress, seconds);
            }
        }

    }

    @Override
    public void removePlayer(Player player) {
        if (players.contains(player)) {
            players.remove(player);
            PlayerUtils.fakeExp(player, player.getExp(), player.getLevel());
        }

    }

    @Override
    public void setProgress(double progress) {
        if (Double.isNaN(progress) || progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }
        this.progress = (float) progress;
        if (visible) {
            for (Player player : players) {
            	PlayerUtils.fakeExp(player, this.progress, seconds);
            }
        }
    }

    @Override
    public List<Player> getViewers() {
        return players;
    }

    @Override
    public double getProgress() {
        return progress;
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    @Override
    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            if (visible) {
                for (Player player : players) {
                	PlayerUtils.fakeExp(player, progress, seconds);
                }
            } else {
                for (Player player : players) {
                	PlayerUtils.fakeExp(player, player.getExp(), player.getLevel());
                }
            }
        }
        this.visible = visible;
    }

    @Override
    public void setSeconds(int seconds) {
        this.seconds = seconds;
        if (visible) {
            for (Player player : players) {
            	PlayerUtils.fakeExp(player, this.progress, seconds);
            }
        }
    }

    @Override
    public int getSeconds() {
        return this.seconds;
    }

}
