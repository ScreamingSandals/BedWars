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

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.legacy.bossbar.BossBar;
import com.viaversion.viaversion.api.legacy.bossbar.BossColor;
import com.viaversion.viaversion.api.legacy.bossbar.BossStyle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.lib.nms.entity.BossBarDragon;
import org.screamingsandals.bedwars.lib.nms.entity.BossBarWither;
import org.screamingsandals.bedwars.lib.nms.entity.FakeEntityNMS;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BossBar18 implements org.screamingsandals.bedwars.api.boss.BossBar18 {
    private double progress = 0;

    public final FakeEntityNMS<?> bossbarEntity;
    public final boolean viaActive;
    public BossBar viaBossBar; // can't be final
    public boolean viaAfterJsonUpdate;

    public BossBar18(Location location) {
        String backend = Main.getConfigurator().config.getString("bossbar.backend-entity");
        if ("dragon".equalsIgnoreCase(backend) || "ender_dragon".equalsIgnoreCase(backend) || "ender".equalsIgnoreCase(backend)) {
            bossbarEntity = new BossBarDragon(location);
        } else {
            bossbarEntity = new BossBarWither(location);
        }
        boolean viaActive = false;
        if (Main.getConfigurator().config.getBoolean("bossbar.allow-via-hooks") && Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) {
            try {
                String version = Bukkit.getPluginManager().getPlugin("ViaVersion").getDescription().getVersion();
                // due to Via now accepting both, many strings including an empty string fails
                viaAfterJsonUpdate = Integer.parseInt(version.split("\\.", 2)[0]) >= 5 && !version.equals("5.0.0") && !version.equals("5.0.1");
                viaBossBar = Via.getAPI().legacyAPI().createLegacyBossBar(viaAfterJsonUpdate ? "{text: \"\"}" : "", 1, BossColor.PURPLE, BossStyle.SOLID);
                viaActive = true;
            } catch (Throwable ignored) {
                // Too old ViaVersion is installed
            }
        }
        this.viaActive = viaActive;
    }

    @Override
    public String getMessage() {
        return bossbarEntity.getCustomName();
    }

    @Override
    public void setMessage(String message) {
        bossbarEntity.setCustomName(message);
        if (viaActive) {
            viaBossBar.setTitle(viaAfterJsonUpdate ? "{text: \"" + message.replace("\"", "\\\"") + "\"}" : message);
        }
    }

    public void setViaColor(String color) {
        if (viaActive) {
            try {
                viaBossBar.setColor(BossColor.valueOf(color.toUpperCase()));
            } catch (Throwable ignored) {
            }
        }
    }

    public void setViaStyle(String style) {
        if (viaActive) {
            try {
                viaBossBar.setStyle(BossStyle.valueOf(style.toUpperCase()));
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    public void addPlayer(Player player) {
        if (viaActive) {
            if (Via.getAPI().getPlayerVersion(player.getUniqueId()) >= 107) {
                viaBossBar.addPlayer(player.getUniqueId());
                return;
            }
        }
        bossbarEntity.addViewer(player);
    }

    @Override
    public void removePlayer(Player player) {
        if (viaActive) {
            if (viaBossBar.getPlayers().contains(player.getUniqueId())) {
                viaBossBar.removePlayer(player.getUniqueId());
                return;
            }
        }
        bossbarEntity.removeViewer(player);
    }

    public boolean isViaPlayer(Player player) {
        if (viaActive) {
            return viaBossBar.getPlayers().contains(player.getUniqueId());
        }
        return false;
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
        if (viaActive) {
            viaBossBar.setHealth((float) progress);
        }
    }

    @Override
    public List<Player> getViewers() {
        if (viaActive) {
            Set<UUID> viaViewers = viaBossBar.getPlayers();
            if (!viaViewers.isEmpty()) {
                List<Player> allViewers = new ArrayList<>(bossbarEntity.getViewers());
                for (UUID viewer : viaViewers) {
                    Player player = Bukkit.getPlayer(viewer);
                    if (player != null && !allViewers.contains(player)) {
                        allViewers.add(player);
                    }
                }
                return allViewers;
            }
        }
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
        if (viaActive) {
            if (visible && !viaBossBar.isVisible()) {
                viaBossBar.show();
            } else if (!visible && viaBossBar.isVisible()) {
                viaBossBar.hide();
            }
        }
    }
}
