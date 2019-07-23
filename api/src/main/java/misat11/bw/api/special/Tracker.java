package misat11.bw.api.special;

import org.bukkit.entity.Player;

public interface Tracker extends SpecialItem {
	public void runTask();

	public Player findTarget();
}
