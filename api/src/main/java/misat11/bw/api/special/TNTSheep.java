package misat11.bw.api.special;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.TNTPrimed;

public interface TNTSheep extends SpecialItem {
	public LivingEntity getEntity();
	
	public boolean isUsed();
	
	public Location getInitialLocation();
	
	public TNTPrimed getTNT();
}
