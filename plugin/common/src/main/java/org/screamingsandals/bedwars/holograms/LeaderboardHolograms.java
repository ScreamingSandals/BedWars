/*
 * Copyright (C) 2025 ScreamingSandals
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

package org.screamingsandals.bedwars.holograms;

import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.commands.RemoveHoloCommand;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.statistics.LeaderboardEntryImpl;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.bedwars.utils.SerializableLocation;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.event.EventExecutionOrder;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.hologram.HologramManager;
import org.screamingsandals.lib.hologram.event.HologramTouchEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.event.player.PlayerJoinEvent;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.ServiceDependencies;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;
import org.screamingsandals.lib.utils.annotations.parameters.ConfigFile;
import org.screamingsandals.lib.utils.visual.TextEntry;
import org.screamingsandals.lib.world.Location;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@ServiceDependencies(dependsOn = {
        HologramManager.class,
        MainConfig.class,
        PlayerStatisticManager.class
})
@RequiredArgsConstructor
public class LeaderboardHolograms {
    @ConfigFile(value = "database/holodb_leaderboard.yml", old = "holodb_leaderboard.yml")
    private final YamlConfigurationLoader loader;
    private final MainConfig mainConfig;

    private ArrayList<SerializableLocation> hologramLocations;
    private Map<Location, Hologram> holograms;
    private List<LeaderboardEntryImpl> entries;

    @ShouldRunControllable
    public static boolean isEnabled() {
        return PlayerStatisticManager.isEnabled() && MainConfig.getInstance().node("holograms", "enabled").getBoolean();
    }

    public static LeaderboardHolograms getInstance() {
        if (!isEnabled()) {
            throw new UnsupportedOperationException("LeaderboardHolograms are not enabled!");
        }
        return ServiceManager.get(LeaderboardHolograms.class);
    }

    public void addHologramLocation(Location eyeLocation) {
        this.hologramLocations.add(new SerializableLocation(eyeLocation.add(0, -3, 0)));
        this.updateHologramDatabase();

        if (entries == null) {
            updateEntries();
        } else {
            updateHolograms();
        }
    }

    public void updateEntries() {
        if (this.hologramLocations == null || this.hologramLocations.isEmpty()) {
            return;
        }

        this.entries = PlayerStatisticManager.getInstance().getLeaderboard(mainConfig.node("holograms", "leaderboard", "size").getInt(10));
        updateHolograms();
    }

    @OnPostEnable
    public void loadHolograms() {
        if (this.hologramLocations != null || this.holograms != null) {
            // first unload all holograms
            this.unloadHolograms();
        }

        this.holograms = new HashMap<>();
        this.hologramLocations = new ArrayList<>();

        try {
            var config = loader.load();
            var locations = config.node("locations").getList(SerializableLocation.class);
            this.hologramLocations.addAll(locations);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }

        if (this.hologramLocations.size() == 0) {
            return;
        }

        Tasker.runAsync(this::updateEntries);
    }

    private void updateHologramDatabase() {
        try {
            var node = loader.createNode();

            for (var location : hologramLocations) {
                node.node("locations").appendListNode().set(location);
            }
            loader.save(node);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @OnPreDisable
    public void unloadHolograms() {
        holograms.values().forEach(holo -> {
            holo.hide();
            HologramManager.removeHologram(holo);
        });
        holograms.clear();
    }

    @OnEvent(order = EventExecutionOrder.LAST)
    public void onJoin(PlayerJoinEvent event) {
        addViewer(event.player());
    }

    public void addViewer(Player player) {
        holograms.values().forEach(hologram -> {
            if (!hologram.viewers().contains(player)) {
                Tasker.runAsyncDelayed(() -> hologram.addViewer(player), 30, TaskerTime.TICKS);
            }
        });
    }

    private void updateHolograms() {
        hologramLocations.forEach(location ->
                location.asOptional(Location.class).ifPresent(locationHolder -> {
                    if (!holograms.containsKey(locationHolder)) {
                        var hologram = HologramManager
                                .hologram(locationHolder)
                                .firstLine(Message.of(LangKeys.LEADERBOARD_NO_SCORES))
                                .touchable(true);
                        HologramManager.addHologram(hologram);
                        hologram.show();
                        holograms.put(locationHolder, hologram);
                    }
                    updateHologram(holograms.get(locationHolder));
                })
        );
        Server.getConnectedPlayers().forEach(this::addViewer);
    }

    private void updateHologram(final Hologram holo) {
        if (entries != null && !entries.isEmpty()) {
            var lines = new ArrayList<String>();

            lines.add(MiscUtils.translateAlternateColorCodes('&', mainConfig.node("holograms", "leaderboard", "headline").getString("")));

            var line = MiscUtils.translateAlternateColorCodes('&', mainConfig.node("holograms", "leaderboard", "format").getString(""));

            var l = new AtomicInteger(1);
            entries.forEach(leaderboardEntry ->
                    lines.add(line
                            .replace("%name%", Optional.ofNullable(leaderboardEntry.getPlayer().getLastName()).orElse(leaderboardEntry.getLastKnownName() != null ? leaderboardEntry.getLastKnownName() : leaderboardEntry.getPlayer().getUuid().toString()))
                            .replace("%score%", Integer.toString(leaderboardEntry.getTotalScore()))
                            .replace("%order%", Integer.toString(l.getAndIncrement()))
                    )
            );

            for (int i = 0; i < lines.size(); i++) {
                holo.replaceLine(i, TextEntry.of(Component.fromLegacy(lines.get(i))));
            }
        }
    }

    @OnEvent
    public void handle(HologramTouchEvent event) {
        var player = event.player();
        var hologram = event.visual();

        if (hologram.location() == null || !holograms.containsKey(hologram.location())) {
            return;
        }

        if (!RemoveHoloCommand.PLAYERS_WITH_HOLOGRAM_REMOVER_IN_HAND.contains(player.getUuid()) || !player.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
            return;
        }

        var location = hologram.location();

        RemoveHoloCommand.PLAYERS_WITH_HOLOGRAM_REMOVER_IN_HAND.remove(player.getUuid());
        Tasker.runAsync(() -> {
                    hologram.hide();
                    HologramManager.removeHologram(hologram);
                    List.copyOf(hologramLocations).forEach(preparedLocation ->
                            preparedLocation.asOptional(Location.class).ifPresent(locationHolder -> {
                                if (locationHolder.getWorld().getUuid().equals(location.getWorld().getUuid())
                                        && locationHolder.getX() == location.getX()
                                        && locationHolder.getY() == location.getY()
                                        && locationHolder.getZ() == location.getZ()) {
                                    hologramLocations.remove(preparedLocation);
                                    holograms.remove(locationHolder);
                                    hologram.hide();
                                    updateHologramDatabase();
                                }
                            })
                    );
                    Message.of(LangKeys.ADMIN_HOLO_REMOVED).defaultPrefix().send(player);
                });
    }
}
