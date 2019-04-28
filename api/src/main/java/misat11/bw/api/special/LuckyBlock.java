package misat11.bw.api.special;

import org.bukkit.Location;

public interface LuckyBlock extends SpecialItem {
	public boolean isPlaced();
	
	public Location getBlockLocation();
}
