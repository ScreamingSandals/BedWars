package misat11.bw.special.listener;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;

public class ArrowBlockerListener implements Listener {

	public static final String ARROW_BLOCKER_PREFIX = "Module:ArrowBlocker:";
	
	@EventHandler
	public void onArrowBlockerRegistered(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("arrowblocker")) {
			ItemStack stack = event.getStack();

			

			//APIUtils.hashIntoInvisibleString(stack, specialString);
		}
		
	}

}
