package misat11.bw.special.listener;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;

public class RescuePlatformListener implements Listener {

	public static final String RESCUE_PLATFORM_PREFIX = "Module:RescuePlatform:";
	
	@EventHandler
	public void onRescuePlatformRegistered(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("rescueplatform")) {
			ItemStack stack = event.getStack();

			

			//APIUtils.hashIntoInvisibleString(stack, specialString);
		}
		
	}

}
