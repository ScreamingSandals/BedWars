package org.screamingsandals.bedwars.boss;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.ArrayList;
import java.util.List;

@Getter
public class XPBar implements org.screamingsandals.bedwars.api.boss.XPBar {

    private boolean visible = false;
    private float progress = 0F;
    private int seconds = 0;
    private final List<PlayerWrapper> viewers = new ArrayList<>();

    @Override
    public void addPlayer(Wrapper wrapper) {
        wrapper.asOptional(PlayerWrapper.class).ifPresent(player -> {
            if (!viewers.contains(player)) {
                viewers.add(player);
                if (visible) {
                    PlayerUtils.fakeExp(player.as(Player.class), progress, seconds);
                }
            }
        });
    }

    @Override
    public void removePlayer(Wrapper wrapper) {
        wrapper.asOptional(PlayerWrapper.class).ifPresent(player -> {
            if (viewers.contains(player)) {
                viewers.remove(player);
                PlayerUtils.fakeExp(player.as(Player.class), player.as(Player.class).getExp(), player.as(Player.class).getLevel());
            }
        });
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
            	PlayerUtils.fakeExp(player.as(Player.class), this.progress, seconds);
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            if (visible) {
                for (var player : viewers) {
                	PlayerUtils.fakeExp(player.as(Player.class), progress, seconds);
                }
            } else {
                for (var player : viewers) {
                	PlayerUtils.fakeExp(player.as(Player.class), player.as(Player.class).getExp(), player.as(Player.class).getLevel());
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
            	PlayerUtils.fakeExp(player.as(Player.class), this.progress, seconds);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Wrapper> getViewers() {
        return (List<Wrapper>) (List<?>) viewers;
    }
}
