package org.screamingsandals.bedwars.holograms;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import static misat11.lib.lang.I18n.i18n;

public class HolographicDisplaysInteraction implements IHologramInteraction {

    private ArrayList<Location> hologramLocations = null;
    private Map<Player, List<Hologram>> holograms = null;

    @Override
    public void addHologramLocation(Location eyeLocation) {
        this.hologramLocations.add(eyeLocation);
        this.updateHologramDatabase();
    }

    private Hologram createPlayerStatisticHologram(Player player, Location holoLocation) {
        final Hologram holo = HologramsAPI.createHologram(Main.getInstance(), holoLocation);
        holo.getVisibilityManager().setVisibleByDefault(false);
        holo.getVisibilityManager().showTo(player);

        this.updatePlayerStatisticHologram(player, holo);
        return holo;
    }

    private Hologram getHologramByLocation(List<Hologram> holograms, Location holoLocation) {
        for (Hologram holo : holograms) {
            if (holo.getLocation().getX() == holoLocation.getX() && holo.getLocation().getY() == holoLocation.getY()
                    && holo.getLocation().getZ() == holoLocation.getZ()) {
                return holo;
            }
        }

        return null;
    }

    private Location getHologramLocationByLocation(Location holoLocation) {
        for (Location loc : this.hologramLocations) {
            if (loc.getX() == holoLocation.getX() && loc.getY() == holoLocation.getY()
                    && loc.getZ() == holoLocation.getZ()) {
                return loc;
            }
        }

        return null;
    }

    public List<Hologram> getHolograms(Player player) {
        return this.holograms.get(player);
    }

    public Map<Player, List<Hologram>> getHolograms() {
        return this.holograms;
    }

    @Override
    public String getType() {
        return "HolographicDisplays";
    }

    @SuppressWarnings("unchecked")
    public void loadHolograms() {
        if (!Main.isHologramsEnabled()) {
            return;
        }

        if (this.holograms != null && this.hologramLocations != null) {
            // first unload all holograms
            this.unloadHolograms();
        }

        this.holograms = new HashMap<>();
        this.hologramLocations = new ArrayList<>();

        File file = new File(Main.getInstance().getDataFolder(), "holodb.yml");
        if (file.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
            List<Location> locations = (List<Location>) config.get("locations");
            for (Location location : locations) {
                this.hologramLocations.add(location);
            }
        }

        if (this.hologramLocations.size() == 0) {
            return;
        }

        this.updateHolograms();
    }

    public void onHologramTouch(final Player player, final Hologram holo) {
        if (!player.hasMetadata("bw-remove-holo") || (!player.isOp() && !player.hasPermission("bw.setup"))) {
            return;
        }

        player.removeMetadata("bw-remove-holo", Main.getInstance());
        Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
            // remove all player holograms on this location
            for (Entry<Player, List<Hologram>> entry : HolographicDisplaysInteraction.this.getHolograms().entrySet()) {
                Iterator<Hologram> iterator = entry.getValue().iterator();
                while (iterator.hasNext()) {
                    Hologram hologram = iterator.next();
                    if (hologram.getX() == holo.getX() && hologram.getY() == holo.getY()
                            && hologram.getZ() == holo.getZ()) {
                        hologram.delete();
                        iterator.remove();
                    }
                }
            }

            Location holoLocation = HolographicDisplaysInteraction.this
                    .getHologramLocationByLocation(holo.getLocation());
            if (holoLocation != null) {
                HolographicDisplaysInteraction.this.hologramLocations.remove(holoLocation);
                HolographicDisplaysInteraction.this.updateHologramDatabase();
            }
            player.sendMessage(i18n("holo_removed"));
        });
    }

    @Override
    public void onHologramTouch(Player player, Location holoLocation) {
        // NOT NEEDED HERE
    }

    public void removeHologramPlayer(Player player) {
        this.holograms.remove(player);
    }

    public void unloadAllHolograms(Player player) {
        if (!this.holograms.containsKey(player)) {
            return;
        }

        for (Hologram holo : this.holograms.get(player)) {
            holo.delete();
        }

        this.holograms.remove(player);
    }

    public void unloadHolograms() {
        if (Main.isHologramsEnabled()) {
            Iterator<Hologram> iterator = HologramsAPI.getHolograms(Main.getInstance()).iterator();
            while (iterator.hasNext()) {
                iterator.next().delete();
            }
        }
    }

    private void updateHologramDatabase() {
        try {
            // update hologram-database file
            File file = new File(Main.getInstance().getDataFolder(), "holodb.yml");
            YamlConfiguration config = new YamlConfiguration();

            if (!file.exists()) {
                file.createNewFile();
            }

            config.set("locations", hologramLocations);
            config.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateHolograms() {
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
                for (Location holoLocation : HolographicDisplaysInteraction.this.hologramLocations) {
                    HolographicDisplaysInteraction.this.updatePlayerHologram(player, holoLocation);
                }
            });
        }
    }

    public void updateHolograms(final Player player) {
        Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
            for (Location holoLocation : HolographicDisplaysInteraction.this.hologramLocations) {
                HolographicDisplaysInteraction.this.updatePlayerHologram(player, holoLocation);
            }
        });
    }

    public void updateHolograms(final Player player, long delay) {
        Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), () -> {
            HolographicDisplaysInteraction.this.updateHolograms(player);
        }, delay);
    }

    private void updatePlayerHologram(Player player, Location holoLocation) {
        List<Hologram> holograms;
        if (!this.holograms.containsKey(player)) {
            this.holograms.put(player, new ArrayList<>());
        }

        holograms = this.holograms.get(player);
        Hologram holo = this.getHologramByLocation(holograms, holoLocation);
        if (holo == null && player.getWorld() == holoLocation.getWorld()) {
            holograms.add(this.createPlayerStatisticHologram(player, holoLocation));
        } else if (holo != null) {
            if (holo.getLocation().getWorld() == player.getWorld()) {
                this.updatePlayerStatisticHologram(player, holo);
            } else {
                holograms.remove(holo);
                holo.delete();
            }
        }
    }

    private void updatePlayerStatisticHologram(Player player, final Hologram holo) {
        PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(player);
        holo.clearLines();

        List<String> lines = new ArrayList<>();

        String headline = Main.getConfigurator().config.getString("holograms.headline", "Your §eBEDWARS§f stats");
        if (!headline.trim().isEmpty()) {
            lines.add(headline);
        }

        lines.add(i18n("statistics_kills", false).replace("%kills%",
                Integer.toString(statistic.getKills() + statistic.getCurrentKills())));
        lines.add(i18n("statistics_deaths", false).replace("%deaths%",
                Integer.toString(statistic.getDeaths() + statistic.getCurrentDeaths())));
        lines.add(i18n("statistics_kd", false).replace("%kd%",
                Double.toString(statistic.getKD() + statistic.getCurrentKD())));
        lines.add(i18n("statistics_wins", false).replace("%wins%",
                Integer.toString(statistic.getWins() + statistic.getCurrentWins())));
        lines.add(i18n("statistics_loses", false).replace("%loses%",
                Integer.toString(statistic.getLoses() + statistic.getCurrentLoses())));
        lines.add(i18n("statistics_games", false).replace("%games%",
                Integer.toString(statistic.getGames() + statistic.getCurrentGames())));
        lines.add(i18n("statistics_beds", false).replace("%beds%",
                Integer.toString(statistic.getDestroyedBeds() + statistic.getCurrentDestroyedBeds())));
        lines.add(i18n("statistics_score", false).replace("%score%",
                Integer.toString(statistic.getScore() + statistic.getCurrentScore())));

        for (String line : lines) {
            TextLine textLine = holo.appendTextLine(line);
            textLine.setTouchHandler(play -> HolographicDisplaysInteraction.this.onHologramTouch(play, holo));
        }
    }

    @Override
    public ArrayList<Location> getHologramLocations() {
        return hologramLocations;
    }

}
