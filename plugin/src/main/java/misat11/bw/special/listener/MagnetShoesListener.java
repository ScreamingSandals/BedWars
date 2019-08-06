package misat11.bw.special.listener;

import misat11.bw.Main;
import misat11.bw.api.APIUtils;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class MagnetShoesListener implements Listener {
	
	// Class for special item is not needed in this case (so this special item is not registered in game)

	public static final String MAGNET_SHOES_PREFIX = "Module:MagnetShoes:";
	
	@EventHandler
	public void onMagnetShoesRegistered(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("magnetshoes")) {
			ItemStack stack = event.getStack();
			
			int probability = event.getIntProperty("probability");

			APIUtils.hashIntoInvisibleString(stack, MAGNET_SHOES_PREFIX + probability);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
			return;
		}
		
		Player player = (Player) event.getEntity();
		if (Main.isPlayerInGame(player)) {
			ItemStack boots = player.getInventory().getBoots();
			if (boots != null) {
				String magnetShoes = APIUtils.unhashFromInvisibleStringStartsWith(boots, MAGNET_SHOES_PREFIX);
				if (magnetShoes != null) {
					int probability = Integer.parseInt(magnetShoes.split(":")[2]);
					Random r = new Random();
					int roll = r.nextInt(101);
					if (roll <= probability) {
					      event.setCancelled(true);
					      player.damage(event.getDamage());
					}
				}
			}
		}
	}

}
