package misat11.bw.special.listener;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;

public class ProtectionWallListener implements Listener {

	public static final String PROTECTION_WALL_PREFIX = "Module:ProtectionWall:";
	
	@EventHandler
	public void onProtectionWallRegistered(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("protectionwall")) {
			ItemStack stack = event.getStack();

			

			//APIUtils.hashIntoInvisibleString(stack, specialString);
		}
		
	}

}
