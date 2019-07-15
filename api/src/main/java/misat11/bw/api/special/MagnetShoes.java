package misat11.bw.api.special;

import org.bukkit.entity.Player;

public interface MagnetShoes extends SpecialItem {
	public Player getWearer();
	
	public boolean isAnybodyWearing();
}
