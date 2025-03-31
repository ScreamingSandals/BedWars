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
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.utils.SerializableLocation;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.event.EventExecutionOrder;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.PlayerJoinEvent;
import org.screamingsandals.lib.event.player.PlayerLeaveEvent;
import org.screamingsandals.lib.event.player.PlayerWorldChangeEvent;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.hologram.HologramManager;
import org.screamingsandals.lib.hologram.event.HologramTouchEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.Player;
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

@Service
@ServiceDependencies(dependsOn = {
        HologramManager.class,
        MainConfig.class,
        PlayerStatisticManager.class
})
@RequiredArgsConstructor
public class StatisticsHolograms {
    @ConfigFile(value = "database/holodb.yml", old = "holodb.yml")
    private final YamlConfigurationLoader loader;
    private final MainConfig mainConfig;

    private ArrayList<SerializableLocation> hologramLocations = null;
    private Map<UUID, List<Hologram>> holograms = null;

    public void addHologramLocation(Location eyeLocation) {
        this.hologramLocations.add(new SerializableLocation(eyeLocation.add(0, -3, 0)));
        this.updateHologramDatabase();
    }

    @ShouldRunControllable
    public static boolean isEnabled() {
        return PlayerStatisticManager.isEnabled() && MainConfig.getInstance().node("holograms", "enabled").getBoolean();
    }

    public static StatisticsHolograms getInstance() {
        if (!isEnabled()) {
            throw new UnsupportedOperationException("StatisticsHolograms are not enabled!");
        }
        return ServiceManager.get(StatisticsHolograms.class);
    }

    @OnPostEnable
    public void loadHolograms() {
        if (this.holograms != null && this.hologramLocations != null) {
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

        this.updateHolograms();
    }

    @OnPreDisable
    public void unloadHolograms() {
        holograms.values().forEach(Holograms -> Holograms.forEach(holo -> {
            holo.hide();
            HologramManager.removeHologram(holo);
        }));
    }

    @OnEvent(order = EventExecutionOrder.LAST)
    public void onJoin(PlayerJoinEvent event) {
        updateHolograms(event.player(), 10L);
    }

    @OnEvent
    public void onWorldChange(PlayerWorldChangeEvent event) {
        updateHolograms(event.player(), 10L);
    }

    @OnEvent
    public void onLeave(PlayerLeaveEvent event) {
        final var playerId = event.player().getUuid();
        if (holograms.containsKey(playerId)) {
            Tasker.runAsync(() -> {
                        holograms.get(playerId).forEach(holo -> {
                            holo.hide();
                            HologramManager.removeHologram(holo);
                        });
                        holograms.remove(playerId);
                    });
        }
    }

    public void updateHolograms(Player player) {
        Tasker.runAsync(() ->
                this.hologramLocations.forEach(holoLocation ->
                        holoLocation.asOptional(Location.class).ifPresent(location ->
                                this.updatePlayerHologram(player, location)
                        )
                ));
    }

    public void updateHolograms(Player player, long delay) {
        Tasker.runAsyncDelayed(() -> this.updateHolograms(player), delay, TaskerTime.TICKS);
    }

    public void updateHolograms() {
        Server.getConnectedPlayers().forEach(player ->
                Tasker.runAsync(() ->
                        this.hologramLocations.forEach(holoLocation ->
                                holoLocation.asOptional(Location.class).ifPresent(location ->
                                        this.updatePlayerHologram(player, location)
                                )
                        ))
        );
    }

    private Optional<Hologram> getHologramByLocation(List<Hologram> holograms, Location holoLocation) {
        return holograms.stream()
                .filter(hologram -> {
                    var loc = hologram.location();
                    return loc != null && loc.getWorld().getUuid().equals(holoLocation.getWorld().getUuid())
                            && loc.getX() == holoLocation.getX()
                            && loc.getY() == holoLocation.getY()
                            && loc.getZ() == holoLocation.getZ();
                })
                .findFirst();
    }

    public void updatePlayerHologram(Player player, Location holoLocation) {
        if (!this.holograms.containsKey(player.getUuid())) {
            this.holograms.put(player.getUuid(), new ArrayList<>());
        }

        var holograms = this.holograms.get(player.getUuid());
        var holo = this.getHologramByLocation(holograms, holoLocation);
        if (holo.isEmpty() && player.getLocation().getWorld().getUuid().equals(holoLocation.getWorld().getUuid())) {
            holograms.add(this.createPlayerStatisticHologram(player, holoLocation));
        } else if (holo.isPresent()) {
            if (player.getLocation().getWorld().getUuid().equals(holo.get().location().getWorld().getUuid())) {
                this.updatePlayerStatisticHologram(player, holo.get());
            } else {
                holograms.remove(holo.get());
                holo.get().hide();
                HologramManager.removeHologram(holo.get());
            }
        }
    }

    private Hologram createPlayerStatisticHologram(Player player, Location holoLocation) {
        final var holo = HologramManager
                .hologram(holoLocation)
                .firstLine(TextEntry.of(Component.fromLegacy(mainConfig.node("holograms", "headline").getString("Your §eBEDWARS§f stats"))))
                .touchable(true)
                .addViewer(player);
        HologramManager.addHologram(holo);
        holo.show();

        this.updatePlayerStatisticHologram(player, holo);
        return holo;
    }

    private void updateHologramDatabase() {
        try {
            // update hologram-database file
            var node = loader.createNode();

            for (var location : hologramLocations) {
                node.node("locations").appendListNode().set(location);
            }
            loader.save(node);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private SerializableLocation getHologramLocationByLocation(Location holoLocation) {
        return hologramLocations.stream()
                .filter(loc -> loc.getWorld().equals(holoLocation.getWorld().getName())
                        && loc.getX() == holoLocation.getX()
                        && loc.getY() == holoLocation.getY()
                        && loc.getZ() == holoLocation.getZ()
                )
                .findFirst()
                .orElse(null);
    }

    @OnEvent
    public void handle(HologramTouchEvent event) {
        var player = event.player();
        var holo = event.visual();
        if (!holograms.containsKey(player.getUuid()) || !holograms.get(player.getUuid()).contains(holo) || holo.location() == null) {
            return;
        }

        if (!RemoveHoloCommand.PLAYERS_WITH_HOLOGRAM_REMOVER_IN_HAND.contains(player.getUuid()) || !player.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
            return;
        }

        var location = holo.location();

        RemoveHoloCommand.PLAYERS_WITH_HOLOGRAM_REMOVER_IN_HAND.remove(player.getUuid());
        Tasker.runAsync(() -> {
                    // remove all player holograms on this location
                    for (var entry : holograms.entrySet()) {
                        var iterator = entry.getValue().iterator();
                        while (iterator.hasNext()) {
                            var hologram = iterator.next();

                            if (location.getWorld().getUuid().equals(location.getWorld().getUuid())
                                    && location.getX() == location.getX()
                                    && location.getY() == location.getY()
                                    && location.getZ() == location.getZ()) {
                                hologram.hide();
                                HologramManager.removeHologram(hologram);
                                iterator.remove();
                            }
                        }
                    }

                    var holoLocation = getHologramLocationByLocation(location);
                    if (holoLocation != null) {
                        hologramLocations.remove(holoLocation);
                        updateHologramDatabase();
                    }
                    Message.of(LangKeys.ADMIN_HOLO_REMOVED).defaultPrefix().send(player);
                });
    }

    private void updatePlayerStatisticHologram(Player player, final Hologram holo) {
        var statistic = PlayerStatisticManager.getInstance().getStatistic(player);

        var lines = Message
                .of(LangKeys.STATISTICS_KILLS)
                .join(LangKeys.STATISTICS_DEATHS)
                .join(LangKeys.STATISTICS_KD)
                .join(LangKeys.STATISTICS_WINS)
                .join(LangKeys.STATISTICS_LOSES)
                .join(LangKeys.STATISTICS_GAMES)
                .join(LangKeys.STATISTICS_BEDS)
                .join(LangKeys.STATISTICS_SCORE)
                .placeholder("player", statistic.getName())
                .placeholder("kills", statistic.getKills())
                .placeholder("deaths", statistic.getDeaths())
                .placeholder("kd", statistic.getKD())
                .placeholder("wins", statistic.getWins())
                .placeholder("loses", statistic.getLoses())
                .placeholder("games", statistic.getGames())
                .placeholder("beds", statistic.getDestroyedBeds())
                .placeholder("score", statistic.getScore())
                .getFor(player);

        var size = holo.lines().size();
        var increment = size == 1 || size > lines.size() ? 1 : 0;

        for (int i = 0; i < lines.size(); i++) {
            holo.replaceLine(i + increment, lines.get(i));
        }

        if (!holo.viewers().contains(player)) {
            holo.addViewer(player);
        }
    }

}
