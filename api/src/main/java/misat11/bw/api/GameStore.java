package misat11.bw.api;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class GameStore {

	private final Location loc;
	private Villager entity;

	public GameStore(Location loc) {
		this.loc = loc;
	}

	public Villager spawn() {
		if (entity == null) {
			entity = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
			try  {
				entity.setAI(false);
			} catch (Throwable t) {
				
			}
		}
		return entity;
	}

	public Villager kill() {
		Villager en = entity;
		if (entity != null) {
			entity.setHealth(0);
			entity = null;
		}
		return en;
	}
	
	public Villager getEntity() {
		return entity;
	}
	
	public Location getStoreLocation() {
		return loc;
	}
	
	
}
