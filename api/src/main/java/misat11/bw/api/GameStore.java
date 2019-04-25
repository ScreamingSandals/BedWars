package misat11.bw.api;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class GameStore {

	private final Location loc;
	private final String shop;
	private final boolean useParent;
	private Villager entity;

	public GameStore(Location loc, String shop, boolean useParent) {
		this.loc = loc;
		this.shop = shop;
		this.useParent = useParent;
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
	
	public String getShopFile() {
		return shop;
	}
	
	public boolean getUseParent() {
		return useParent && shop != null;
	}
	
	
}
