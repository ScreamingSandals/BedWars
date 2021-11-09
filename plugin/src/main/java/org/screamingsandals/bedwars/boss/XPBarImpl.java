package org.screamingsandals.bedwars.boss;

import lombok.Getter;
import org.screamingsandals.bedwars.api.boss.XPBar;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;
import org.screamingsandals.lib.player.PlayerWrapper;

import java.util.ArrayList;
import java.util.List;

@Getter
public class XPBarImpl implements XPBar<PlayerWrapper> {
    private final List<PlayerWrapper> viewers = new ArrayList<>();
    private boolean visible = false;
    private float progress = 0F;
    private int seconds = 0;

    @Override
    public void addPlayer(PlayerWrapper player) {
        if (!viewers.contains(player)) {
            viewers.add(player);
            if (visible) {
                PlayerUtils.fakeExp(player, progress, seconds);
            }
        }
    }

    @Override
    public void removePlayer(PlayerWrapper player) {
        if (viewers.contains(player)) {
            viewers.remove(player);
            PlayerUtils.fakeExp(player, player.getExp(), player.getLevel());
        }
    }

    @Override
    public void setProgress(float progress) {
        if (Double.isNaN(progress) || progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }
        this.progress = progress;
        if (visible) {
            for (var player : viewers) {
            	PlayerUtils.fakeExp(player, this.progress, seconds);
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            if (visible) {
                for (var player : viewers) {
                	PlayerUtils.fakeExp(player, progress, seconds);
                }
            } else {
                for (var player : viewers) {
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
            for (var player : viewers) {
            	PlayerUtils.fakeExp(player, this.progress, seconds);
            }
        }
    }
}
