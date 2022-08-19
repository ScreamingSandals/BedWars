/*
 * Copyright (C) 2022 ScreamingSandals
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

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

import java.io.File;
import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.statistics.PlayerStatistic;
import org.screamingsandals.bedwars.commands.BaseCommand;
import org.screamingsandals.bedwars.lib.nms.holograms.Hologram;
import org.screamingsandals.bedwars.lib.nms.holograms.TouchHandler;

public class StatisticsHolograms implements TouchHandler {

    private ArrayList<Location> hologramLocations = null;
    private Map<UUID, List<Hologram>> holograms = null;

	public void addHologramLocation(Location eyeLocation) {
        this.hologramLocations.add(eyeLocation.subtract(0, 3, 0));
        this.updateHologramDatabase();
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
            assert locations != null;
            this.hologramLocations.addAll(locations);
        }

        if (this.hologramLocations.size() == 0) {
            return;
        }

        this.updateHolograms();
    }

	public void unloadHolograms() {
        if (Main.isHologramsEnabled()) {
        	for (List<Hologram> holos : holograms.values()) {
        		for (Hologram holo : holos) {
        			holo.destroy();
        		}
        	}
        }
	}

	public void updateHolograms(Player player) {
        Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
            for (Location holoLocation : StatisticsHolograms.this.hologramLocations) {
            	StatisticsHolograms.this.updatePlayerHologram(player, holoLocation);
            }
        });
	}

	public void updateHolograms(Player player, long delay) {
        Main.getInstance().getServer().getScheduler().runTaskLater(Main.getInstance(), () -> StatisticsHolograms.this.updateHolograms(player), delay);
	}

    public void cleanupPlayerLeave(OfflinePlayer player) {
        final List<Hologram> holos = holograms.get(player.getUniqueId());
        if (holos != null) {
            holos.forEach(holo -> holo.destroy());
            holos.clear();
            holograms.remove(player.getUniqueId());
        }
    }

	public void updateHolograms() {
        for (final Player player : Bukkit.getServer().getOnlinePlayers()) {
            Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
                for (Location holoLocation : StatisticsHolograms.this.hologramLocations) {
                	StatisticsHolograms.this.updatePlayerHologram(player, holoLocation);
                }
            });
        }
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
	
	public void updatePlayerHologram(Player player, Location holoLocation) {
        List<Hologram> holograms;
        if (!this.holograms.containsKey(player.getUniqueId())) {
            this.holograms.put(player.getUniqueId(), new ArrayList<>());
        }

        holograms = this.holograms.get(player.getUniqueId());
        Hologram holo = this.getHologramByLocation(holograms, holoLocation);
        if (holo == null && player.getWorld() == holoLocation.getWorld()) {
            holograms.add(this.createPlayerStatisticHologram(player, holoLocation));
        } else if (holo != null) {
            if (holo.getLocation().getWorld() == player.getWorld()) {
                this.updatePlayerStatisticHologram(player, holo);
            } else {
                holograms.remove(holo);
                holo.destroy();
            }
        }
	}

    private Hologram createPlayerStatisticHologram(Player player, Location holoLocation) {
        final Hologram holo = Main.getHologramManager().spawnHologramTouchable(player, holoLocation);
        holo.addHandler(this);

        String headline =
                ChatColor.translateAlternateColorCodes('&',
                        Main.getConfigurator().config.getString("holograms.headline", "Your &eBEDWARS&f stats")
                );
        if (!headline.trim().isEmpty()) {
            holo.addLine(headline);
        }

        this.updatePlayerStatisticHologram(player, holo);
        return holo;
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

    private Location getHologramLocationByLocation(Location holoLocation) {
        for (Location loc : this.hologramLocations) {
            if (loc.getX() == holoLocation.getX() && loc.getY() == holoLocation.getY()
                    && loc.getZ() == holoLocation.getZ()) {
                return loc;
            }
        }

        return null;
    }

	@Override
	public void handle(Player player, Hologram holo) {
        if (!player.hasMetadata("bw-remove-holo") || (!player.isOp() && !BaseCommand.hasPermission(player, BaseCommand.ADMIN_PERMISSION, false))) {
            return;
        }

        player.removeMetadata("bw-remove-holo", Main.getInstance());
        Main.getInstance().getServer().getScheduler().runTask(Main.getInstance(), () -> {
            // remove all player holograms on this location
            for (Entry<UUID, List<Hologram>> entry : holograms.entrySet()) {
                Iterator<Hologram> iterator = entry.getValue().iterator();
                while (iterator.hasNext()) {
                    Hologram hologram = iterator.next();
                    if (hologram.getLocation().getX() == holo.getLocation().getX() && hologram.getLocation().getY() == holo.getLocation().getY()
                            && hologram.getLocation().getZ() == holo.getLocation().getZ()) {
                        hologram.destroy();
                        iterator.remove();
                    }
                }
            }

            Location holoLocation = getHologramLocationByLocation(holo.getLocation());
            if (holoLocation != null) {
                hologramLocations.remove(holoLocation);
                updateHologramDatabase();
            }
            player.sendMessage(i18n("holo_removed"));
        });
	}

    private void updatePlayerStatisticHologram(Player player, final Hologram holo) {
        PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(player);
        
        List<String> lines = new ArrayList<>();

        lines.add(i18n("statistics_kills", false).replace("%kills%",
                Integer.toString(statistic.getKills())));
        lines.add(i18n("statistics_deaths", false).replace("%deaths%",
                Integer.toString(statistic.getDeaths())));
        lines.add(i18n("statistics_kd", false).replace("%kd%",
                Double.toString(statistic.getKD())));
        lines.add(i18n("statistics_wins", false).replace("%wins%",
                Integer.toString(statistic.getWins())));
        lines.add(i18n("statistics_loses", false).replace("%loses%",
                Integer.toString(statistic.getLoses())));
        lines.add(i18n("statistics_games", false).replace("%games%",
                Integer.toString(statistic.getGames())));
        lines.add(i18n("statistics_beds", false).replace("%beds%",
                Integer.toString(statistic.getDestroyedBeds())));
        lines.add(i18n("statistics_score", false).replace("%score%",
                Integer.toString(statistic.getScore())));
        
        int size = holo.length();
        int increment = 0;
        if (size == 1 || size > lines.size()) {
        	increment = 1;
        }

        for (int i = 0; i < lines.size(); i++) {
        	holo.setLine(i + increment, lines.get(i));
        }

        if (!holo.getViewers().contains(player)) {
            holo.addViewer(player);
        }
    }

}
