package misat11.bw.special.listener;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;

public class MagnetShoesListener implements Listener {

	public static final String MAGNET_SHOES_PREFIX = "Module:MagnetShoes:";
	
	@EventHandler
	public void onMagnetShoesRegistered(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("magnetshoes")) {
			ItemStack stack = event.getStack();

			

			//APIUtils.hashIntoInvisibleString(stack, specialString);
		}
		
	}

}
