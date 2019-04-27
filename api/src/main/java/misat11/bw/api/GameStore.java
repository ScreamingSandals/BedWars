package misat11.bw.api;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;

public class GameStore {

	private final Location loc;
	private final String shop;
	private final boolean useParent;
	private LivingEntity entity;
	private EntityType type;

	public GameStore(Location loc, String shop, boolean useParent) {
		this(loc, shop, useParent, EntityType.VILLAGER);
	}

	public GameStore(Location loc, String shop, boolean useParent, EntityType type) {
		if (type == null || !type.isAlive()) {
			type = EntityType.VILLAGER;
		}
		this.loc = loc;
		this.shop = shop;
		this.useParent = useParent;
		this.type = type;
	}

	public LivingEntity spawn() {
		if (entity == null) {
			entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type);
			try {
				entity.setAI(false);
				if (entity instanceof Villager) {
					((Villager) entity).setProfession(Villager.Profession.FARMER);
				}
			} catch (Throwable t) {

			}
		}
		return entity;
	}

	public LivingEntity kill() {
		LivingEntity en = entity;
		if (entity != null) {
			entity.setHealth(0);
			entity = null;
		}
		return en;
	}

	public LivingEntity getEntity() {
		return entity;
	}

	public EntityType getEntityType() {
		return type;
	}
	
	public void setEntityType(EntityType type) {
		if (type != null && type.isAlive()) {
			this.type = type;
		}
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
