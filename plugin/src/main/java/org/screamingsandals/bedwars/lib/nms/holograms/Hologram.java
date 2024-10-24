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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.lib.nms.accessors.*;
import org.screamingsandals.bedwars.lib.nms.entity.ArmorStandNMS;
import org.screamingsandals.bedwars.lib.nms.entity.EntityNMS;
import org.screamingsandals.bedwars.lib.nms.entity.EntityTextDisplayNMS;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;
import org.screamingsandals.bedwars.lib.nms.utils.Version;

public class Hologram {

	public static final int VISIBILITY_DISTANCE_SQUARED = 4096;
	public static final boolean DISPLAY_ENTITIES_AVAILABLE = Display$TextDisplayAccessor.TYPE.get() != null;

	private List<Player> viewers = new ArrayList<>();
	private List<String> lines = new ArrayList<>();
	private Location loc;
	private List<EntityNMS> entities = new ArrayList<>();
	private boolean touchable = false;
	private List<TouchHandler> handlers = new ArrayList<>();
	private HologramManager manager;

	Hologram(HologramManager manager, List<Player> players, Location loc, String[] lines) {
		this(manager, players, loc, lines, false);
	}

	Hologram(HologramManager manager, List<Player> players, Location loc, String[] lines, boolean touchable) {
		this.manager = manager;
		this.lines.addAll(Arrays.asList(lines));
		this.loc = loc;
		updateEntities();
		addViewers(players);
		this.touchable = touchable;
	}

	public Location getLocation() {
		return loc;
	}

	public int length() {
		return lines.size();
	}

	public boolean hasViewers() {
		return !viewers.isEmpty();
	}
	
	public List<Player> getViewers() {
		return viewers;
	}

	public boolean isEmpty() {
		return lines.isEmpty();
	}

	public Hologram addHandler(TouchHandler handler) {
		if (handler != null) {
			handlers.add(handler);
		}
		return this;
	}

	public Hologram setHandler(TouchHandler handler) {
		handlers.clear();
		if (handler != null) {
			handlers.add(handler);
		}
		return this;
	}

	public Hologram addViewer(Player player) {
		return addViewers(Collections.singletonList(player));
	}

	public Hologram addViewers(List<Player> players) {
		for (Player player : players) {
			if (!viewers.contains(player)) {
				viewers.add(player);
				try {
					update(player, getAllSpawnPackets(), true);
				} catch (Throwable ignored) {
				}
			}
		}
		return this;
	}

	public Hologram removeViewer(Player player) {
		return removeViewers(Arrays.asList(player));
	}

	public Hologram removeViewers(List<Player> players) {
		for (Player player : players) {
			if (viewers.contains(player)) {
				viewers.remove(player);
				try {
					update(player, getAllDestroyPackets(), true);
				} catch (Throwable ignored) {
				}
			}
		}
		return this;
	}

	public Hologram setLine(int index, String message) {
		if (this.lines.size() <= index) {
			return addLine(message);
		}
		this.lines.set(index, message);
		updateEntities(index, true);
		return this;

	}

	public Hologram addLine(String message) {
		this.lines.add(message);
		// updateEntities(this.lines.size() - 1, true);
		updateEntities(); // TODO just move upper lines
		return this;
	}

	public Hologram removeLine() {
		return removeLine(this.lines.size() - 1);
	}

	public Hologram removeLine(int index) {
		this.lines.remove(index);
		updateEntities(index, false);
		return this;
	}

	public Hologram destroy() {
		this.lines.clear();
		updateEntities();
		return this;
	}

	public boolean handleTouch(Player player, int entityId) throws Throwable {
		if (!touchable)
			return false;
		if (!isItMyId(entityId))
			return false;

		for (TouchHandler handler : handlers) {
			handler.handle(player, this);
		}

		return true;
	}

	public boolean isItMyId(int id) throws Throwable {
		for (EntityNMS entity : entities) {
			if (entity.getId() == id) {
				return true;
			}
		}
		return false;
	}

	private void updateEntities() {
		updateEntities(0, false);
	}

	private void updateEntities(int startIndex, boolean justThisIndex) {
		try {
			List<Object> packets = new ArrayList<>();
			boolean positionChanged = !justThisIndex && this.lines.size() != this.entities.size();
			for (int i = startIndex; (i < this.lines.size()) && (!justThisIndex || i == startIndex); i++) {
				String line = this.lines.get(i);
				if (i < this.entities.size() && this.entities.get(i) != null) {
					EntityNMS entity = this.entities.get(i);
					if (entity instanceof ArmorStandNMS) {
						ArmorStandNMS stand = (ArmorStandNMS) entity;
						stand.setCustomName(line);
						if (ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_1.get() != null) {
							Object watcherInList = ClassStorage.getMethod(stand.getDataWatcher(), SynchedEntityDataAccessor.METHOD_PACK_DIRTY.get()).invoke();
							if (watcherInList != null) {
								Object metadataPacket = ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_1.get()
										.newInstance(stand.getId(), watcherInList);
								packets.add(metadataPacket);
								ClassStorage.getMethod(stand.getDataWatcher(), SynchedEntityDataAccessor.METHOD_CLEAR_DIRTY.get()).invoke();
							}
						} else {
							Object metadataPacket = ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_0.get()
									.newInstance(stand.getId(),
											stand.getDataWatcher(), false);
							packets.add(metadataPacket);
						}
					} else if (entity instanceof EntityTextDisplayNMS) {
						EntityTextDisplayNMS display = (EntityTextDisplayNMS) entity;
						display.setText(line);

						Object watcherInList = ClassStorage.getMethod(display.getDataWatcher(), SynchedEntityDataAccessor.METHOD_PACK_DIRTY.get()).invoke();
						if (watcherInList != null) {
							Object metadataPacket = ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_1.get()
									.newInstance(display.getId(), watcherInList);
							packets.add(metadataPacket);
						}
					}
					if (positionChanged) {
						Location localLoc = loc.clone().add(0, (this.lines.size() - i) * .30, 0);
						entity.setLocation(localLoc);
						Object teleportPacket;
						if (ClientboundTeleportEntityPacketAccessor.CONSTRUCTOR_1.get() != null) {
							Object move = ClassStorage.getMethod(PositionMoveRotationAccessor.METHOD_OF.get()).invokeStatic(entity.getHandler());

							teleportPacket = ClientboundTeleportEntityPacketAccessor.CONSTRUCTOR_1.get()
									.newInstance(
											entity.getId(),
											move,
											Collections.emptySet(),
											ClassStorage.getMethod(entity.getHandler(), EntityAccessor.METHOD_ON_GROUND.get()).invoke()
									);
						} else {
							teleportPacket = ClientboundTeleportEntityPacketAccessor.CONSTRUCTOR_0.get()
									.newInstance(entity.getHandler());
						}
						packets.add(teleportPacket);
					}
				} else {
					Location localLoc = loc.clone().add(0, (this.lines.size() - i) * .30, 0);
					if (!touchable && DISPLAY_ENTITIES_AVAILABLE && Main.getConfigurator().config.getBoolean("prefer-1-19-4-display-entities")) { // TODO: support touchable
						// 1.19.4+
						EntityTextDisplayNMS display = new EntityTextDisplayNMS(localLoc);
						display.setText(line);

						if (ClientboundAddEntityPacketAccessor.CONSTRUCTOR_1.get() != null) {
							packets.add(ClientboundAddEntityPacketAccessor.CONSTRUCTOR_1.get().newInstance(display.getHandler()));
						} else {
							packets.add(ClientboundAddEntityPacketAccessor.CONSTRUCTOR_2.get().newInstance(
									display.getId(),
									display.getUUID(),
									display.getX(),
									display.getY(),
									display.getZ(),
									display.getXRot(),
									display.getYRot(),
									display.getType(),
									0,
									display.getDelta(),
									display.getYHeadRot()
							));
						}

						Object watcherInList = ClassStorage.getMethod(display.getDataWatcher(), SynchedEntityDataAccessor.METHOD_PACK_DIRTY.get()).invoke();
						if (watcherInList != null) {
							Object metadataPacket = ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_1.get()
									.newInstance(display.getId(), watcherInList);
							packets.add(metadataPacket);
						}
						if (this.entities.size() <= i) {
							this.entities.add(display);
						} else {
							this.entities.set(i, display);
						}
					} else {
						ArmorStandNMS stand = new ArmorStandNMS(localLoc);
						stand.setCustomName(line);
						stand.setCustomNameVisible(true);
						stand.setInvisible(true);
						stand.setSmall(!touchable);
						stand.setArms(false);
						stand.setBasePlate(false);
						stand.setGravity(false);
						stand.setMarker(!touchable);
						Object spawnLivingPacket;
						if (ClientboundAddMobPacketAccessor.TYPE.get() != null) {
							spawnLivingPacket = ClientboundAddMobPacketAccessor.CONSTRUCTOR_0.get()
									.newInstance(stand.getHandler());
						} else if (ClientboundAddEntityPacketAccessor.CONSTRUCTOR_0.get() != null) {
							spawnLivingPacket = ClientboundAddEntityPacketAccessor.CONSTRUCTOR_0.get()
									.newInstance(stand.getHandler());
						} else if (ClientboundAddEntityPacketAccessor.CONSTRUCTOR_1.get() != null) {
							spawnLivingPacket = ClientboundAddEntityPacketAccessor.CONSTRUCTOR_1.get()
									.newInstance(stand.getHandler());
						} else {
							spawnLivingPacket = ClientboundAddEntityPacketAccessor.CONSTRUCTOR_2.get()
									.newInstance(
											stand.getId(),
											stand.getUUID(),
											stand.getX(),
											stand.getY(),
											stand.getZ(),
											stand.getXRot(),
											stand.getYRot(),
											stand.getType(),
											0,
											stand.getDelta(),
											stand.getYHeadRot()
									);
						}
						packets.add(spawnLivingPacket);
						if (Version.isVersion(1, 15)) {
							if (ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_1.get() != null) {
								Object watcherInList = ClassStorage.getMethod(stand.getDataWatcher(), SynchedEntityDataAccessor.METHOD_PACK_DIRTY.get()).invoke();
								if (watcherInList != null) {
									Object metadataPacket = ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_1.get()
											.newInstance(stand.getId(), watcherInList);
									packets.add(metadataPacket);
									ClassStorage.getMethod(stand.getDataWatcher(), SynchedEntityDataAccessor.METHOD_CLEAR_DIRTY.get()).invoke();
								}
							} else {
								Object metadataPacket = ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_0.get()
										.newInstance(stand.getId(),
												stand.getDataWatcher(), false);
								packets.add(metadataPacket);
							}
						}
						if (this.entities.size() <= i) {
							this.entities.add(stand);
						} else {
							this.entities.set(i, stand);
						}
					}
				}
			}

			List<Integer> forRemoval = new ArrayList<>();
			if (this.entities.size() > this.lines.size()) {
				for (int i = this.lines.size(); i < this.entities.size(); this.entities.remove(i)) {
					forRemoval.add(this.entities.get(i).getId());
				}
			}

			if (ClientboundRemoveEntitiesPacketAccessor.CONSTRUCTOR_0.get() != null) { // weird 1.17 version
				Constructor<?> constructor = ClientboundRemoveEntitiesPacketAccessor.CONSTRUCTOR_0.get();
				for (Integer integer : forRemoval) {
					packets.add(constructor.newInstance(integer));
				}
			} else {
				Object destroyPacket = ClientboundRemoveEntitiesPacketAccessor.CONSTRUCTOR_1.get()
						.newInstance(forRemoval.stream().mapToInt(i -> i).toArray());

				packets.add(destroyPacket);
			}

			viewers.forEach(player -> update(player, packets, true));

		} catch (Throwable ignored) {
			ignored.printStackTrace(); // Remove after testing
		}
	}

	void update(Player player, List<Object> packets, boolean check_distance) {
		try {
			if (!manager.getHolograms().contains(this)) {
				manager.getHolograms().add(this);
			}

			if (player.getLocation().getWorld().equals(loc.getWorld())) {
				if (check_distance && player.getLocation().distanceSquared(loc) >= VISIBILITY_DISTANCE_SQUARED) {
					return;
				}
			} else if (check_distance) {
				return;
			}

			for (Object packet : packets) {
				ClassStorage.sendPacket(player, packet);
			}
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
	}

	public List<Object> getAllDestroyPackets() throws InstantiationException, IllegalAccessException,
		IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<Object> packets = new ArrayList<>();
		
		if (ClientboundRemoveEntitiesPacketAccessor.CONSTRUCTOR_0.get() != null) {
			Constructor<?> constructor =  ClientboundRemoveEntitiesPacketAccessor.CONSTRUCTOR_0.get(); // weird 1.17 version
			for (EntityNMS integer : entities) {
				packets.add(constructor.newInstance(integer.getId()));
			}
		} else {
			int[] removal = new int[entities.size()];
			for (int i = 0; i < entities.size(); i++) {
				removal[i] = (int) entities.get(i).getId();
			}

			packets.add(ClientboundRemoveEntitiesPacketAccessor.CONSTRUCTOR_1.get().newInstance(removal));
		}
		return packets;
	}

	public List<Object> getAllSpawnPackets() throws InstantiationException, IllegalAccessException,
		IllegalArgumentException, InvocationTargetException, SecurityException {
		List<Object> packets = new ArrayList<>();
		for (EntityNMS entity : entities) {
			if (ClientboundAddMobPacketAccessor.TYPE.get() != null) {
				packets.add(ClientboundAddMobPacketAccessor.CONSTRUCTOR_0.get().newInstance(entity.getHandler()));
			} else if (ClientboundAddEntityPacketAccessor.CONSTRUCTOR_0.get() != null) {
				packets.add(ClientboundAddEntityPacketAccessor.CONSTRUCTOR_0.get().newInstance(entity.getHandler()));
			} else if (ClientboundAddEntityPacketAccessor.CONSTRUCTOR_1.get() != null) {
				packets.add(ClientboundAddEntityPacketAccessor.CONSTRUCTOR_1.get().newInstance(entity.getHandler()));
			} else {
				packets.add(ClientboundAddEntityPacketAccessor.CONSTRUCTOR_2.get().newInstance(
						entity.getId(),
						entity.getUUID(),
						entity.getX(),
						entity.getY(),
						entity.getZ(),
						entity.getXRot(),
						entity.getYRot(),
						entity.getType(),
						0,
						entity.getDelta(),
						entity.getYHeadRot()
				));
			}
			if (Version.isVersion(1, 15)) {
				if (ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_1.get() != null) {
					Object watcherInList;
					if (SynchedEntityDataAccessor.METHOD_GET_ALL.get() != null) {
						watcherInList = ClassStorage.getMethod(entity.getDataWatcher(), SynchedEntityDataAccessor.METHOD_GET_ALL.get()).invoke();
					} else {
						watcherInList = ClassStorage.getMethod(entity.getDataWatcher(), SynchedEntityDataAccessor.METHOD_GET_NON_DEFAULT_VALUES.get()).invoke();
					}
					if (watcherInList != null) {
						Object metadataPacket = ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_1.get()
								.newInstance(entity.getId(), watcherInList);
						packets.add(metadataPacket);
					}
				} else {
					Object metadataPacket = ClientboundSetEntityDataPacketAccessor.CONSTRUCTOR_0.get()
							.newInstance(entity.getId(), entity.getDataWatcher(), true);
					packets.add(metadataPacket);
				}
			}
		}
		return packets;
	}
}
