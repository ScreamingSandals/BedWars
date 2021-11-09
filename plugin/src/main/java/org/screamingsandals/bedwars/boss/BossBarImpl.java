package org.screamingsandals.bedwars.boss;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.adventure.wrapper.BossBarColorWrapper;
import org.screamingsandals.lib.utils.adventure.wrapper.BossBarOverlayWrapper;
import org.screamingsandals.lib.utils.adventure.wrapper.ComponentWrapper;

import java.util.LinkedList;
import java.util.List;

@Getter
public class BossBarImpl implements org.screamingsandals.bedwars.api.boss.BossBar<PlayerWrapper> {
    private final List<PlayerWrapper> viewers = new LinkedList<>();
    private final BossBar boss = BossBar.bossBar(
            Component.empty(),
            1,
            BossBar.Color.PURPLE,
            BossBar.Overlay.PROGRESS
    );
    private boolean visible;

    @Override
    public ComponentWrapper getMessage() {
        return ComponentWrapper.of(boss.name());
    }

    public void setMessage(@Nullable Object s) {
        if (s instanceof ComponentLike) {
            boss.name(((ComponentLike) s).asComponent());
        } else {
            boss.name(AdventureHelper.toComponentNullable(String.valueOf(s)));
        }
    }

    @Override
    public void addPlayer(PlayerWrapper player) {
        if (!viewers.contains(player)) {
            viewers.add(player);
            if (visible) {
                player.showBossBar(boss);
            }
        }
    }

    @Override
    public void removePlayer(PlayerWrapper player) {
        if (viewers.contains(player)) {
            viewers.remove(player);
            if (visible) {
                player.hideBossBar(boss);
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
            if (visible) {
                viewers.forEach(playerWrapper -> playerWrapper.showBossBar(boss));
            } else {
                viewers.forEach(playerWrapper -> playerWrapper.hideBossBar(boss));
            }
        }
        visible = visibility;
    }

    @Override
    public BossBarColorWrapper getColor() {
        return new BossBarColorWrapper(boss.color());
    }

    @Override
    public void setColor(@NotNull Object color) {
        if (color instanceof BossBar.Color) {
            boss.color((BossBar.Color) color);
        } else if (color instanceof BossBarColorWrapper) {
            boss.color(((BossBarColorWrapper) color).asBossBarColor());
        } else {
            boss.color(BossBar.Color.valueOf(color.toString().toUpperCase()));
        }
    }

    @Override
    public BossBarOverlayWrapper getStyle() {
        return new BossBarOverlayWrapper(boss.overlay());
    }

    @Override
    public void setStyle(@NotNull Object overlay) {
        if (overlay instanceof BossBar.Overlay) {
            boss.overlay((BossBar.Overlay) overlay);
        } else if (overlay instanceof BossBarOverlayWrapper) {
            boss.overlay(((BossBarOverlayWrapper) overlay).asBossBarOverlay());
        } else {
            var ov = overlay.toString().toUpperCase();
            if (ov.equals("SOLID")) {
                ov = "PROGRESS";
            } else if (ov.startsWith("SEGMENTED_")) {
                ov = "NOTCHED_" + ov.substring(10);
            }
            boss.overlay(BossBar.Overlay.valueOf(ov));
        }
    }
}
