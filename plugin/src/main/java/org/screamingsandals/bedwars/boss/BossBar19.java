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

package org.screamingsandals.bedwars.boss;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.List;

public class BossBar19 implements org.screamingsandals.bedwars.api.boss.BossBar19 {

    public BossBar boss;

    public BossBar19() {
        this.boss = Bukkit.createBossBar("", BarColor.PURPLE, BarStyle.SOLID);
    }

    @Override
    public String getMessage() {
        return boss.getTitle();
    }

    @Override
    public void setMessage(String s) {
        boss.setTitle(s);
    }

    @Override
    public void addPlayer(Player player) {
        boss.addPlayer(player);
    }

    @Override
    public void removePlayer(Player player) {
        boss.removePlayer(player);
    }

    @Override
    public void setProgress(double progress) {
        if (Double.isNaN(progress) || progress > 1) {
            progress = 1;
        } else if (progress < 0) {
            progress = 0;
        }
        boss.setProgress(progress);
    }

    @Override
    public List<Player> getViewers() {
        return boss.getPlayers();
    }

    @Override
    public double getProgress() {
        return boss.getProgress();
    }

    @Override
    public boolean isVisible() {
        return boss.isVisible();
    }

    @Override
    public void setVisible(boolean visibility) {
        boss.setVisible(visibility);
    }

    @Override
    public BarColor getColor() {
        return boss.getColor();
    }

    @Override
    public void setColor(BarColor color) {
        boss.setColor(color);
    }

    @Override
    public BarStyle getStyle() {
        return boss.getStyle();
    }

    @Override
    public void setStyle(BarStyle style) {
        boss.setStyle(style);
    }

}
