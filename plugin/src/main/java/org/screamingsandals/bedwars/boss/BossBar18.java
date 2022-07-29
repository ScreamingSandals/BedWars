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

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.lib.nms.entity.BossBarWither;

import java.util.List;

public class BossBar18 implements org.screamingsandals.bedwars.api.boss.BossBar18 {
    private double progress = 0;

    public BossBarWither bossbarEntity;

    public BossBar18(Location location) {
        bossbarEntity = new BossBarWither(location);
    }

    @Override
    public String getMessage() {
        return bossbarEntity.getCustomName();
    }

    @Override
    public void setMessage(String message) {
        bossbarEntity.setCustomName(message);
    }

    @Override
    public void addPlayer(Player player) {
        if (bossbarEntity == null) {
            bossbarEntity = new BossBarWither(player.getLocation());
        }

        bossbarEntity.addViewer(player);
    }

    @Override
    public void removePlayer(Player player) {
        bossbarEntity.removeViewer(player);
    }

    @Override
    public void setProgress(double progress) {
        this.progress = progress;
        if (Double.isNaN(progress) || progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }

        bossbarEntity.setHealth(progress);
    }

    @Override
    public List<Player> getViewers() {
        return bossbarEntity.getViewers();
    }

    @Override
    public double getProgress() {
        return progress;
    }

    @Override
    public boolean isVisible() {
        return bossbarEntity.isVisible();
    }

    @Override
    public void setVisible(boolean visible) {
        bossbarEntity.setVisible(visible);
    }
}
