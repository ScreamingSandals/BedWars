package misat11.bw.api.special;

import org.bukkit.Location;

public interface Trap extends SpecialItem {
	public Location getLocation();
	
	public boolean isPlaced();
}
