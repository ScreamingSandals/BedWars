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

package org.screamingsandals.bedwars.lib.nms.holograms;

import static org.screamingsandals.bedwars.lib.nms.utils.ClassStorage.getField;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.lib.nms.accessors.PacketPlayInUseEntityAccessor;
import org.screamingsandals.bedwars.lib.nms.network.inbound.AutoPacketInboundListener;

public class HologramManager implements Listener {

	public static final int VISIBILITY_DISTANCE_SQUARED = 4096;
	
	private final List<Hologram> HOLOGRAMS = new ArrayList<>();
	private Plugin pl;
	
	public HologramManager(Plugin pl) {
		this.pl = pl;
		this.pl.getServer().getPluginManager().registerEvents(this, this.pl);
		new AutoPacketInboundListener(pl) {
			
			@Override
			protected Object handle(Player sender, Object packet) throws Throwable {
				if (PacketPlayInUseEntityAccessor.getType().isInstance(packet)) {
					int a = (int) getField(packet, PacketPlayInUseEntityAccessor.getFieldField_149567_a());
					for (Hologram h : HOLOGRAMS) {
						if (h.handleTouch(sender, a)) {
							break;
						}
					}
				}
				return packet;
			}
		};
	}

	public Hologram spawnHologram(Location loc, String... lines) {
		return new Hologram(this, Collections.emptyList(), loc, lines);
	}

	public Hologram spawnHologramTouchable(Location loc, String... lines) {
		return new Hologram(this, Collections.emptyList(), loc, lines, true);
	}
	
	public Hologram spawnHologram(Player player, Location loc, String... lines) {
		return spawnHologram(Arrays.asList(player), loc, lines);
	}

	public Hologram spawnHologramTouchable(Player player, Location loc, String... lines) {
		return spawnHologramTouchable(Arrays.asList(player), loc, lines);
	}

	public Hologram spawnHologram(List<Player> players, Location loc, String... lines) {
		return new Hologram(this, players, loc, lines);
	}

	public Hologram spawnHologramTouchable(List<Player> players, Location loc, String... lines) {
		return new Hologram(this, players, loc, lines, true);
	}
	
	public List<Hologram> getHolograms() {
		return HOLOGRAMS;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (HOLOGRAMS.isEmpty()) {
			return;
		}
		
		List<Hologram> copy = new ArrayList<>(HOLOGRAMS);
		for (Hologram hologram : copy) {
			if (hologram.isEmpty() || !hologram.hasViewers()) {
				HOLOGRAMS.remove(hologram);
				continue;
			}
			try {
				Player player = event.getPlayer();
				List<Player> viewers = hologram.getViewers();
				Location loc = hologram.getLocation();
				if (viewers.contains(player) && player.getWorld().equals(loc.getWorld())) {
					if (event.getTo().distanceSquared(loc) < VISIBILITY_DISTANCE_SQUARED
						&& event.getFrom().distanceSquared(loc) >= VISIBILITY_DISTANCE_SQUARED) {
						hologram.update(player, hologram.getAllSpawnPackets(), false);
					} else if (event.getTo().distanceSquared(loc) >= VISIBILITY_DISTANCE_SQUARED
						&& event.getFrom().distanceSquared(loc) < VISIBILITY_DISTANCE_SQUARED) {
						hologram.update(player, hologram.getAllDestroyPackets(), false);
					}
				}
			} catch (Throwable t) {
			}
		}
	}
	
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		if (HOLOGRAMS.isEmpty()) {
			return;
		}
		
		List<Hologram> copy = new ArrayList<>(HOLOGRAMS);
		for (Hologram hologram : copy) {
			if (hologram.isEmpty() || !hologram.hasViewers()) {
				HOLOGRAMS.remove(hologram);
				continue;
			}
			try {
				Player player = event.getPlayer();
				List<Player> viewers = hologram.getViewers();
				Location loc = hologram.getLocation();
				if (viewers.contains(player) && player.getWorld().equals(loc.getWorld())
					&& !event.getFrom().equals(loc.getWorld())) {
					if (player.getLocation().distanceSquared(loc) < VISIBILITY_DISTANCE_SQUARED) {
						new BukkitRunnable() {
							public void run() {
								try {
									hologram.update(player, hologram.getAllSpawnPackets(), false);
								} catch (Throwable t) {
								}
							}
						}.runTaskLater(pl, 20L);
					}
				}
			} catch (Throwable t) {
			}
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (HOLOGRAMS.isEmpty()) {
			return;
		}
		
		List<Hologram> copy = new ArrayList<>(HOLOGRAMS);
		for (Hologram hologram : copy) {
			if (hologram.isEmpty() || !hologram.hasViewers()) {
				HOLOGRAMS.remove(hologram);
				continue;
			}
			try {
				Player player = event.getPlayer();
				List<Player> viewers = hologram.getViewers();
				Location loc = hologram.getLocation();
				if (viewers.contains(player) && event.getRespawnLocation().getWorld().equals(loc.getWorld())) {
					if (player.getLocation().distanceSquared(loc) < VISIBILITY_DISTANCE_SQUARED) {
						new BukkitRunnable() {
							public void run() {
								try {
									hologram.update(player, hologram.getAllSpawnPackets(), false);
								} catch (Throwable t) {
								}
							}
						}.runTaskLater(pl, 20L);
					}
				}
			} catch (Throwable t) {
			}
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (HOLOGRAMS.isEmpty() || !event.getFrom().getWorld().equals(event.getTo().getWorld()) /* World change is handled in another event*/) {
			return;
		}
		
		List<Hologram> copy = new ArrayList<>(HOLOGRAMS);
		for (Hologram hologram : copy) {
			if (hologram.isEmpty() || !hologram.hasViewers()) {
				HOLOGRAMS.remove(hologram);
				continue;
			}
			try {
				Player player = event.getPlayer();
				List<Player> viewers = hologram.getViewers();
				Location loc = hologram.getLocation();
				if (viewers.contains(player) && player.getWorld().equals(loc.getWorld())) {
					if (event.getTo().distanceSquared(loc) < VISIBILITY_DISTANCE_SQUARED
						&& event.getFrom().distanceSquared(loc) >= VISIBILITY_DISTANCE_SQUARED) {
						new BukkitRunnable() {
							public void run() {
								try {
									hologram.update(player, hologram.getAllSpawnPackets(), false);
								} catch (Throwable t) {
								}
							}
						}.runTaskLater(pl, 10L);
					} else if (event.getTo().distanceSquared(loc) >= VISIBILITY_DISTANCE_SQUARED
						&& event.getFrom().distanceSquared(loc) < VISIBILITY_DISTANCE_SQUARED) {
						new BukkitRunnable() {
							public void run() {
								try {
									hologram.update(player, hologram.getAllDestroyPackets(), false);
								} catch (Throwable t) {
								}
							}
						}.runTaskLater(pl, 10L);
					}
				}
			} catch (Throwable t) {
			}
		}
	}

	@EventHandler
	public void onPlayersQuit(PlayerQuitEvent event) {
		if (HOLOGRAMS.isEmpty()) {
			return;
		}

		List<Hologram> copy = new ArrayList<>(HOLOGRAMS);
		for (Hologram hologram : copy) {
			if (hologram.getViewers().contains(event.getPlayer())) {
				hologram.removeViewer(event.getPlayer());
			}
			if (hologram.isEmpty() || !hologram.hasViewers()) {
				HOLOGRAMS.remove(hologram);
			}
		}
	}
}
