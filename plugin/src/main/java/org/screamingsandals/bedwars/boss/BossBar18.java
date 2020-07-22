package org.screamingsandals.bedwars.boss;

import me.confuser.barapi.BarAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BossBar18 implements org.screamingsandals.bedwars.api.boss.BossBar18 {

    private boolean visible = false;
    private List<Player> players = new ArrayList<>();
    private String message = "";
    private double progress = 0;

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        if (!isPluginForLegacyBossBarEnabled()) return;
        this.message = message;
        if (visible) {
            for (Player p : players) {
                show(p);
            }
        }
    }

    @Override
    public void addPlayer(Player player) {
        if (!isPluginForLegacyBossBarEnabled()) return;
        if (!players.contains(player)) {
            players.add(player);

            if (visible) {
                show(player);
            }
        }
    }

    @Override
    public void removePlayer(Player player) {
        if (!isPluginForLegacyBossBarEnabled()) return;
        if (players.contains(player)) {
            players.remove(player);

            if (BarAPI.hasBar(player)) {
                hide(player);
            }
        }
    }

    @Override
    public void setProgress(double progress) {
        if (!isPluginForLegacyBossBarEnabled()) return;
        this.progress = progress;
        if (Double.isNaN(progress) || progress < 0) {
            progress = 0;
        } else if (progress > 1) {
            progress = 1;
        }
        if (visible) {
            for (Player p : players) {
                show(p);
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
        if (!isPluginForLegacyBossBarEnabled()) return;
        if (this.visible != visible) {
            if (visible) {
                for (Player player : players) {
                    show(player);
                }
            } else {
                for (Player player : players) {
                    hide(player);
                }
            }
            this.visible = visible;
        }
    }

    private void show(Player player) {
        if (!isPluginForLegacyBossBarEnabled()) return;
        BarAPI.setMessage(player, message, (float) progress * 100);
    }

    private void hide(Player player) {
        if (!isPluginForLegacyBossBarEnabled()) return;
        BarAPI.removeBar(player);
    }

    public static boolean isPluginForLegacyBossBarEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("BarAPI");
    }

}
