package misat11.bw.special;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.Team;

public class LuckyBlock extends SpecialItem implements misat11.bw.api.special.LuckyBlock {

	private List<Map<String, Object>> luckyBlockData;
	private Location placedLocation = null;
	private boolean isPlaced = false;

	public LuckyBlock(Game game, Player player, Team team, List<Map<String, Object>> luckyBlockData) {
		super(game, player, team);

		this.luckyBlockData = luckyBlockData;

		game.registerSpecialItem(this);
	}

	public void place(Location loc) {
		this.placedLocation = loc;
		this.isPlaced = true;
	}

	public void process(Player broker) {
		game.unregisterSpecialItem(this);

		Random rand = new Random();
		int element = rand.nextInt(luckyBlockData.size());

		Map<String, Object> map = luckyBlockData.get(element);

		String type = (String) map.getOrDefault("type", "nothing");
		switch (type) {
		case "item":
			ItemStack stack = ((ItemStack) map.get("stack")).clone();
			placedLocation.getWorld().dropItem(placedLocation, stack);
			break;
		case "potion":
			PotionEffect potionEffect = (PotionEffect) map.get("effect");
			broker.addPotionEffect(potionEffect);
			break;
		case "tnt":
			new BukkitRunnable() {
				
				@Override
				public void run() {
					TNTPrimed tnt = (TNTPrimed) placedLocation.getWorld().spawnEntity(placedLocation, EntityType.PRIMED_TNT);
					tnt.setFuseTicks(0);
				}
			}.runTaskLater(Main.getInstance(), 10L);
			break;
		case "teleport":
			broker.teleport(broker.getLocation().add(0, (int) map.get("height"), 0));
			break;
		}
		
		if (map.containsKey("message")) {
			broker.sendMessage((String) map.get("message"));
		}

	}

	@Override
	public boolean isPlaced() {
		return isPlaced;
	}

	@Override
	public Location getBlockLocation() {
		return placedLocation;
	}

}
