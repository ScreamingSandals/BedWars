package misat11.bw.game;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;

public class GameStore {

	public final Location loc;
	private Villager entity;
	private List<Villager> oldVillagers = new ArrayList<Villager>();

	public GameStore(Location loc) {
		this.loc = loc;
	}

	public void spawn() {
		if (entity == null) {
			entity = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
			entity.setAI(false);
		}
	}

	public void forceKill() {
		if (entity != null) {
			entity.setHealth(0);
			entity = null;
		}
		for (Villager vill : oldVillagers) {
			vill.setHealth(0);
		}
		oldVillagers.clear();
	}
}
