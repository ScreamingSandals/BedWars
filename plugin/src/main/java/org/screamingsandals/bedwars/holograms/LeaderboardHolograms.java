package org.screamingsandals.bedwars.holograms;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.statistics.LeaderboardEntry;
import org.screamingsandals.bedwars.commands.old.BaseCommand;
import org.screamingsandals.bedwars.lib.nms.holograms.Hologram;
import org.screamingsandals.bedwars.lib.nms.holograms.TouchHandler;
import org.screamingsandals.bedwars.utils.PreparedLocation;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;
import static org.screamingsandals.bedwars.lib.lang.I.i18nonly;

public class LeaderboardHolograms implements TouchHandler {
    private ArrayList<PreparedLocation> hologramLocations;
    private Map<Location, Hologram> holograms;
    private List<LeaderboardEntry> entries;

    public void addHologramLocation(Location eyeLocation) {
        this.hologramLocations.add(new PreparedLocation(eyeLocation.subtract(0, 3, 0)));
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

        this.entries = Main.getPlayerStatisticsManager().getLeaderboard(Main.getConfigurator().node("holograms", "leaderboard", "size").getInt());
        updateHolograms();
    }

    public void loadHolograms() {
        if (!Main.isHologramsEnabled()) {
            return;
        }

        if (this.hologramLocations != null || this.holograms != null) {
            // first unload all holograms
            this.unloadHolograms();
        }

        this.holograms = new HashMap<>();
        this.hologramLocations = new ArrayList<>();

        File file = new File(Main.getInstance().getDataFolder(), "database/holodb_leaderboard.yml");
        if (file.exists()) {
            var loader = YamlConfigurationLoader.builder()
                    .file(file)
                    .build();
            try {
                var config = loader.load();
                var locations = config.node("locations").getList(PreparedLocation.class);
                this.hologramLocations.addAll(locations);
            } catch (ConfigurateException e) {
                e.printStackTrace();
            }
        }

        if (this.hologramLocations.size() == 0) {
            return;
        }

        Bukkit.getScheduler().runTask(Main.getInstance(), this::updateEntries);
    }

    private void updateHologramDatabase() {
        try {
            File file = new File(Main.getInstance().getDataFolder(), "database/holodb_leaderboard.yml");
            var loader = YamlConfigurationLoader.builder()
                    .file(file)
                    .build();

            var node = loader.createNode();

            for (var location : hologramLocations) {
                node.node("locations").appendListNode().set(location);
            }
            loader.save(node);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void unloadHolograms() {
        if (Main.isHologramsEnabled()) {
            for (Hologram holo : holograms.values()) {
                holo.destroy();
            }
            holograms.clear();
        }
    }

    public void addViewer(Player player) {
        holograms.values().forEach(hologram -> {
            if (!hologram.getViewers().contains(player)) {
                Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> hologram.addViewer(player), 10L);
            }
        });
    }

    private void updateHolograms() {
        hologramLocations.forEach(location ->
            location.asOptional(Location.class).ifPresent(bukkitLocation -> {
                if (!holograms.containsKey(bukkitLocation)) {
                    holograms.put(bukkitLocation, Main.getHologramManager().spawnHologramTouchable(bukkitLocation));
                    holograms.get(bukkitLocation).addHandler(this);
                }
                updateHologram(holograms.get(bukkitLocation));
            })
        );
        Bukkit.getOnlinePlayers().forEach(this::addViewer);
    }

    private void updateHologram(final Hologram holo) {
        List<String> lines = new ArrayList<>();

        lines.add(ChatColor.translateAlternateColorCodes('&', Main.getConfigurator().node("holograms", "leaderboard", "headline").getString("")));

        String line = ChatColor.translateAlternateColorCodes('&', Main.getConfigurator().node("holograms", "leaderboard", "format").getString(""));

        if (entries == null || entries.isEmpty()) {
            lines.add(i18nonly("leaderboard_no_scores"));
        } else {
            AtomicInteger l = new AtomicInteger(1);
            entries.forEach(leaderboardEntry ->
                lines.add(line
                        .replace("%name%", leaderboardEntry.getPlayer().getName() != null ? leaderboardEntry.getPlayer().getName() : leaderboardEntry.getPlayer().getUniqueId().toString())
                        .replace("%score%", Integer.toString(leaderboardEntry.getTotalScore()))
                        .replace("%order%", Integer.toString(l.getAndIncrement()))
                )
            );
        }

        for (int i = 0; i < lines.size(); i++) {
            holo.setLine(i, lines.get(i));
        }
    }

    @Override
    public void handle(Player player, Hologram hologram) {
        if (!player.hasMetadata("bw-remove-holo") || (!player.isOp() && !BaseCommand.hasPermission(player, BaseCommand.ADMIN_PERMISSION, false))) {
            return;
        }

        player.removeMetadata("bw-remove-holo", Main.getInstance());
        Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
            hologram.destroy();
            List.copyOf(hologramLocations).forEach(location ->
                location.asOptional(Location.class).ifPresent(bukkitLocation -> {
                    if (hologram.getLocation().getX() == bukkitLocation.getX() && hologram.getLocation().getY() == bukkitLocation.getY()
                            && hologram.getLocation().getZ() == bukkitLocation.getZ()) {
                        hologramLocations.remove(location);
                        holograms.remove(bukkitLocation);
                        updateHologramDatabase();
                    }
                })
            );
            player.sendMessage(i18n("holo_removed"));
        });
    }
}
