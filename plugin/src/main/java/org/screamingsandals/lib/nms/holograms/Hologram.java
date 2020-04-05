package org.screamingsandals.lib.nms.holograms;

import static org.screamingsandals.lib.nms.utils.ClassStorage.NMS.*;

import static org.screamingsandals.lib.nms.utils.ClassStorage.*;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.lib.nms.entity.ArmorStandNMS;
import org.screamingsandals.lib.nms.utils.InstanceMethod;
import org.screamingsandals.lib.nms.utils.Version;

public class Hologram {

	public static final int VISIBILITY_DISTANCE_SQUARED = 4096;

	private List<Player> viewers = new ArrayList<>();
	private List<String> lines = new ArrayList<>();
	private Location loc;
	private List<ArmorStandNMS> entities = new ArrayList<>();
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
		return addViewers(Arrays.asList(player));
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
					update(player, Arrays.asList(getFullDestroyPacket()), true);
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
		for (ArmorStandNMS entity : entities) {
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
					ArmorStandNMS stand = this.entities.get(i);
					stand.setCustomName(line);
					Object metadataPacket = PacketPlayOutEntityMetadata
						.getConstructor(int.class, DataWatcher, boolean.class).newInstance(stand.getId(),
							stand.getDataWatcher(), false);
					packets.add(metadataPacket);
					if (positionChanged) {
						Location localLoc = loc.clone().add(0, (this.lines.size() - i) * .30, 0);
						stand.setLocation(localLoc);;
						Object teleportPacket = PacketPlayOutEntityTeleport
							.getConstructor(Entity).newInstance(stand.getHandler());
						packets.add(teleportPacket);
					}
				} else {
					Location localLoc = loc.clone().add(0, (this.lines.size() - i) * .30, 0);
					ArmorStandNMS stand = new ArmorStandNMS(localLoc);
					stand.setCustomName(line);
					stand.setCustomNameVisible(true);
					stand.setInvisible(true);
					stand.setSmall(!touchable);
					stand.setArms(false);
					stand.setBasePlate(false);
					stand.setGravity(false);
					stand.setMarker(!touchable);
					Object spawnLivingPacket = PacketPlayOutSpawnEntityLiving.getConstructor(EntityLiving)
						.newInstance(stand.getHandler());
					packets.add(spawnLivingPacket);
					if (Version.isVersion(1, 15)) {
						Object metadataPacket = PacketPlayOutEntityMetadata
							.getConstructor(int.class, DataWatcher, boolean.class).newInstance(stand.getId(),
								stand.getDataWatcher(), false);
						packets.add(metadataPacket);
					}
					if (this.entities.size() <= i) {
						this.entities.add(stand);
					} else {
						this.entities.set(i, stand);
					}
				}
			}

			List<Integer> forRemoval = new ArrayList<>();
			if (this.entities.size() > this.lines.size()) {
				for (int i = this.lines.size(); i < this.entities.size(); this.entities.remove(i)) {
					forRemoval.add(this.entities.get(i).getId());
				}
			}

			Object destroyPacket = PacketPlayOutEntityDestroy.getConstructor(int[].class)
				.newInstance(forRemoval.stream().mapToInt(i -> i).toArray());

			packets.add(destroyPacket);

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

			Object handler = getMethod(player, "getHandle").invoke();
			Object connection = getField(handler, "playerConnection,field_71135_a");
			InstanceMethod sendPacket = getMethod(connection, "sendPacket,func_147359_a", Packet);
			for (Object packet : packets) {
				sendPacket.invoke(packet);
			}
		} catch (Throwable ignored) {
			ignored.printStackTrace();
		}
	}

	public Object getFullDestroyPacket() throws InstantiationException, IllegalAccessException,
		IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		int[] removal = new int[entities.size()];
		for (int i = 0; i < entities.size(); i++) {
			removal[i] = (int) entities.get(i).getId();
		}
		return PacketPlayOutEntityDestroy.getConstructor(int[].class).newInstance(removal);
	}

	public List<Object> getAllSpawnPackets() throws InstantiationException, IllegalAccessException,
		IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		List<Object> packets = new ArrayList<>();
		for (ArmorStandNMS entity : entities) {
			packets.add(PacketPlayOutSpawnEntityLiving.getConstructor(EntityLiving).newInstance(entity.getHandler()));
			if (Version.isVersion(1, 15)) {
				Object metadataPacket = PacketPlayOutEntityMetadata
					.getConstructor(int.class, DataWatcher, boolean.class).newInstance(
						entity.getId(), entity.getDataWatcher(), true);
				packets.add(metadataPacket);
			}
		}
		return packets;
	}
}
