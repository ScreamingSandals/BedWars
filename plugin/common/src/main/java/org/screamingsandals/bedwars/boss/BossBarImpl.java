/*
 * Copyright (C) 2024 ScreamingSandals
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

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.api.types.ComponentHolder;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.ComponentLike;
import org.screamingsandals.lib.spectator.bossbar.BossBar;
import org.screamingsandals.lib.spectator.bossbar.BossBarColor;
import org.screamingsandals.lib.spectator.bossbar.BossBarDivision;

import java.util.LinkedList;
import java.util.List;

@Getter
public class BossBarImpl implements org.screamingsandals.bedwars.api.boss.BossBar {
    private final List<BedWarsPlayer> viewers = new LinkedList<>();
    private final BossBar boss = BossBar.builder()
            .title(Component.empty())
            .progress(1)
            .color(BossBarColor.PURPLE)
            .division(BossBarDivision.NO_DIVISION)
            .build();
    private boolean visible;

    @Override
    public Component getMessage() {
        return boss.title();
    }

    public void setMessage(@Nullable ComponentHolder s) {
        boss.title(s == null ? Component.empty() : s.as(Component.class));
    }

    @Override
    public void addPlayer(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }

        var bwPlayer = (BedWarsPlayer) player;

        if (!viewers.contains(bwPlayer)) {
            viewers.add(bwPlayer);
            if (visible) {
                bwPlayer.showBossBar(boss);
            }
        }
    }

    @Override
    public void removePlayer(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }

        var bwPlayer = (BedWarsPlayer) player;

        if (viewers.contains(bwPlayer)) {
            viewers.remove(bwPlayer);
            if (visible) {
                bwPlayer.hideBossBar(boss);
            }
        }
    }

    @Override
    public void setProgress(float progress) {
        if (Float.isNaN(progress) || progress > 1) {
            progress = 1;
        } else if (progress < 0) {
            progress = 0;
        }
        boss.progress(progress);
    }

    @Override
    public float getProgress() {
        return boss.progress();
    }

    @Override
    public void setVisible(boolean visibility) {
        if (visible != visibility) {
            if (visibility) {
                viewers.forEach(playerWrapper -> playerWrapper.showBossBar(boss));
            } else {
                viewers.forEach(playerWrapper -> playerWrapper.hideBossBar(boss));
            }
        }
        visible = visibility;
    }

    @Override
    public BossBarColor getColor() {
        return boss.color();
    }

    @Override
    public void setColor(@NotNull Object color) {
        if (color instanceof BossBarColor) {
            boss.color((BossBarColor) color);
        } else {
            boss.color(BossBarColor.valueOf(color.toString().toUpperCase()));
        }
    }

    @Override
    public BossBarDivision getStyle() {
        return boss.division();
    }

    @Override
    public void setStyle(@NotNull Object division) {
        if (division instanceof BossBarDivision) {
            boss.division((BossBarDivision) division);
        } else {
            var ov = division.toString().toUpperCase();
            if (ov.equals("SOLID") || ov.equals("PROGRESS")) {
                ov = "NO_DIVISION";
            } else if (ov.startsWith("SEGMENTED_")) {
                ov = "NOTCHED_" + ov.substring(10);
            }
            boss.division(BossBarDivision.valueOf(ov));
        }
    }
}
