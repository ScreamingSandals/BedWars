package org.screamingsandals.bedwars.holograms;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.api.statistics.LeaderboardEntry;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.utils.PreparedLocation;
import org.screamingsandals.lib.event.EventPriority;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.hologram.HologramManager;
import org.screamingsandals.lib.hologram.event.HologramTouchEvent;
import org.screamingsandals.lib.player.OfflinePlayerWrapper;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.player.event.SPlayerJoinEvent;
import org.screamingsandals.lib.plugin.PluginDescription;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;
import org.screamingsandals.lib.utils.annotations.parameters.ConfigFile;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.LocationMapper;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;
import static org.screamingsandals.bedwars.lib.lang.I.i18nonly;

@Service(dependsOn = {
        PlayerMapper.class,
        LocationMapper.class,
        HologramManager.class,
        Tasker.class,
        MainConfig.class,
        PlayerStatisticManager.class
})
@RequiredArgsConstructor
public class LeaderboardHolograms {
    private final PluginDescription pluginDescription;
    @ConfigFile(value = "database/holodb_leaderboard.yml", old = "holodb_leaderboard.yml")
    private final YamlConfigurationLoader loader;
    private final MainConfig mainConfig;

    private ArrayList<PreparedLocation> hologramLocations;
    private Map<LocationHolder, Hologram> holograms;
    private List<LeaderboardEntry<OfflinePlayerWrapper>> entries;

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

    public void addHologramLocation(LocationHolder eyeLocation) {
        this.hologramLocations.add(new PreparedLocation(eyeLocation.add(0, -3, 0)));
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

        this.entries = PlayerStatisticManager.getInstance().getLeaderboard(mainConfig.node("holograms", "leaderboard", "size").getInt());
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
            var locations = config.node("locations").getList(PreparedLocation.class);
            this.hologramLocations.addAll(locations);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }

        if (this.hologramLocations.size() == 0) {
            return;
        }

        Tasker.build(this::updateEntries)
                .async()
                .start();
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

    @OnEvent(priority = EventPriority.HIGHEST)
    public void onJoin(SPlayerJoinEvent event) {
        addViewer(event.getPlayer());
    }

    public void addViewer(PlayerWrapper player) {
        holograms.values().forEach(hologram -> {
            if (!hologram.getViewers().contains(player)) {
                Tasker.build(() -> hologram.addViewer(player))
                        .async()
                        .delay(10, TaskerTime.TICKS)
                        .start();
            }
        });
    }

    private void updateHolograms() {
        hologramLocations.forEach(location ->
                location.asOptional(LocationHolder.class).ifPresent(locationHolder -> {
                    if (!holograms.containsKey(locationHolder)) {
                        var hologram = HologramManager
                                .hologram(locationHolder)
                                .setTouchable(true);
                        HologramManager.addHologram(hologram);
                        hologram.show();
                        holograms.put(locationHolder, hologram);
                    }
                    updateHologram(holograms.get(locationHolder));
                })
        );
        PlayerMapper.getPlayers().forEach(this::addViewer);
    }

    private void updateHologram(final Hologram holo) {
        var lines = new ArrayList<String>();

        lines.add(ChatColor.translateAlternateColorCodes('&', mainConfig.node("holograms", "leaderboard", "headline").getString("")));

        var line = ChatColor.translateAlternateColorCodes('&', mainConfig.node("holograms", "leaderboard", "format").getString(""));

        if (entries == null || entries.isEmpty()) {
            lines.add(i18nonly("leaderboard_no_scores"));
        } else {
            var l = new AtomicInteger(1);
            entries.forEach(leaderboardEntry ->
                    lines.add(line
                            .replace("%name%", leaderboardEntry.getPlayer().getLastName().orElse(leaderboardEntry.getPlayer().getUuid().toString()))
                            .replace("%score%", Integer.toString(leaderboardEntry.getTotalScore()))
                            .replace("%order%", Integer.toString(l.getAndIncrement()))
                    )
            );
        }

        for (int i = 0; i < lines.size(); i++) {
            holo.setLine(i, AdventureHelper.toComponent(lines.get(i)));
        }
    }

    @OnEvent
    public void handle(HologramTouchEvent event) {
        var player = event.getPlayer();
        var hologram = event.getHologram();

        if (hologram.getLocation().isEmpty() || !holograms.containsKey(hologram.getLocation().get())) {
            return;
        }

        if (!player.as(Player.class).hasMetadata("bw-remove-holo") || !PlayerMapper.wrapPlayer(player).hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
            return;
        }

        var location = hologram.getLocation().get();

        player.as(Player.class).removeMetadata("bw-remove-holo", pluginDescription.as(JavaPlugin.class));
        Tasker
                .build(() -> {
                    hologram.hide();
                    HologramManager.removeHologram(hologram);
                    List.copyOf(hologramLocations).forEach(preparedLocation ->
                            preparedLocation.asOptional(LocationHolder.class).ifPresent(locationHolder -> {
                                if (locationHolder.getWorld().getUuid().equals(location.getWorld().getUuid())
                                        && locationHolder.getX() == location.getX()
                                        && locationHolder.getY() == location.getY()
                                        && locationHolder.getZ() == location.getZ()) {
                                    hologramLocations.remove(preparedLocation);
                                    holograms.remove(locationHolder);
                                    updateHologramDatabase();
                                }
                            })
                    );
                    player.sendMessage(i18n("holo_removed"));
                })
                .async()
                .start();
    }
}
