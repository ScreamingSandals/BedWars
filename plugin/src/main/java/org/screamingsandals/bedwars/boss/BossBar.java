package org.screamingsandals.bedwars.boss;

import net.kyori.adventure.text.Component;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.Wrapper;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BossBar implements org.screamingsandals.bedwars.api.boss.BossBar {

    public static final Map<BarStyle, net.kyori.adventure.bossbar.BossBar.Overlay> STYLE_OVERLAY_MAP = Map.of(
            BarStyle.SOLID, net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS,
            BarStyle.SEGMENTED_6, net.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_6,
            BarStyle.SEGMENTED_10, net.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_10,
            BarStyle.SEGMENTED_12, net.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_12,
            BarStyle.SEGMENTED_20, net.kyori.adventure.bossbar.BossBar.Overlay.NOTCHED_20
    );

    private final net.kyori.adventure.bossbar.BossBar boss = net.kyori.adventure.bossbar.BossBar.
            bossBar(
                    Component.empty(),
                    1,
                    net.kyori.adventure.bossbar.BossBar.Color.PURPLE,
                    net.kyori.adventure.bossbar.BossBar.Overlay.PROGRESS
            );
    private final List<PlayerWrapper> viewers = new LinkedList<>();
    private boolean visible;

    @Override
    public String getMessage() {
        return AdventureHelper.toLegacy(boss.name());
    }

    @Override
    public void setMessage(String s) {
        boss.name(AdventureHelper.toComponent(s));
    }

    public Component getComponent() {
        return boss.name();
    }

    public void setComponent(Component s) {
        boss.name(s);
    }

    @Override
    public void addPlayer(Wrapper player) {
        player.asOptional(PlayerWrapper.class).ifPresent(playerWrapper -> {
            if (!viewers.contains(playerWrapper)) {
                viewers.add(playerWrapper);
                if (visible) {
                    playerWrapper.showBossBar(boss);
                }
            }
        });
    }

    @Override
    public void removePlayer(Wrapper player) {
        player.asOptional(PlayerWrapper.class).ifPresent(playerWrapper -> {
            if (viewers.contains(playerWrapper)) {
                viewers.remove(playerWrapper);
                if (visible) {
                    playerWrapper.showBossBar(boss);
                }
            }
        });
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

    @SuppressWarnings("unchecked")
    @Override
    public List<Wrapper> getViewers() {
        return (List<Wrapper>) (List<?>) viewers;
    }

    @Override
    public float getProgress() {
        return boss.progress();
    }

    @Override
    public boolean isVisible() {
        return visible;
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

    public net.kyori.adventure.bossbar.BossBar.Color getBossBarColor() {
        return boss.color();
    }

    public void setBossBarColor(net.kyori.adventure.bossbar.BossBar.Color color) {
        boss.color(color);
    }

    public net.kyori.adventure.bossbar.BossBar.Overlay getOverlay() {
        return boss.overlay();
    }

    public void setOverlay(net.kyori.adventure.bossbar.BossBar.Overlay overlay) {
        boss.overlay(overlay);
    }

    @Override
    public BarColor getColor() {
        return BarColor.valueOf(boss.color().name());
    }

    @Override
    public void setColor(BarColor color) {
        boss.color(net.kyori.adventure.bossbar.BossBar.Color.valueOf(color.name()));
    }

    @Override
    public BarStyle getStyle() {
        return STYLE_OVERLAY_MAP.entrySet().stream()
                .filter(b -> b.getValue() == boss.overlay())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();
    }

    @Override
    public void setStyle(BarStyle style) {
        boss.overlay(STYLE_OVERLAY_MAP.get(style));
    }

}
