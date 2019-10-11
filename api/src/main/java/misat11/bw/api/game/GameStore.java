package misat11.bw.api.game;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;

/**
 * @author Bedwars Team
 */
public class GameStore {
    private final Location loc;
    private final String shop;
    private final String shopName;
    private final boolean enableCustomName;
    private final boolean useParent;
    private LivingEntity entity;
    private EntityType type;

    /**
     * @param loc
     * @param shop
     * @param useParent
     * @param shopName
     * @param enableCustomName
     */
    public GameStore(Location loc, String shop, boolean useParent, String shopName, boolean enableCustomName) {
        this(loc, shop, useParent, EntityType.VILLAGER, shopName, enableCustomName);
    }

    /**
     * @param loc
     * @param shop
     * @param useParent
     * @param type
     * @param shopName
     * @param enableCustomName
     */
    public GameStore(Location loc, String shop, boolean useParent, EntityType type, String shopName, boolean enableCustomName) {
        if (type == null || !type.isAlive()) {
            type = EntityType.VILLAGER;
        }
        this.loc = loc;
        this.shop = shop;
        this.useParent = useParent;
        this.type = type;
        this.shopName = shopName;
        this.enableCustomName = enableCustomName;
    }

    /**
     * @return
     */
    public LivingEntity spawn() {
        if (entity == null) {
            entity = (LivingEntity) loc.getWorld().spawnEntity(loc, type);
            if (enableCustomName) {
                entity.setCustomName(shopName);
                entity.setCustomNameVisible(true);
            }
            if (entity instanceof Villager) {
                ((Villager) entity).setProfession(Villager.Profession.FARMER);
            }
        }
        return entity;
    }

    /**
     * @return
     */
    public LivingEntity kill() {
        LivingEntity en = entity;
        if (entity != null) {
            Chunk chunk = entity.getLocation().getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            entity.remove();
            entity = null;
        }
        return en;
    }

    /**
     * @return
     */
    public LivingEntity getEntity() {
        return entity;
    }

    /**
     * @return
     */
    public EntityType getEntityType() {
        return type;
    }

    /**
     * @param type
     */
    public void setEntityType(EntityType type) {
        if (type != null && type.isAlive()) {
            this.type = type;
        }
    }

    /**
     * @return
     */
    public Location getStoreLocation() {
        return loc;
    }

    /**
     * @return
     */
    public String getShopFile() {
        return shop;
    }

    /**
     * @return
     */
    public String getShopCustomName() {
        return shopName;
    }

    /**
     * @return
     */
    public boolean getUseParent() {
        return useParent && shop != null;
    }

    /**
     * @return
     */
    public boolean isShopCustomName() {
        return enableCustomName;
    }

}
