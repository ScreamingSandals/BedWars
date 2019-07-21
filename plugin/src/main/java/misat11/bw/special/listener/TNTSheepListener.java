package misat11.bw.special.listener;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;

public class TNTSheepListener implements Listener {

	public static final String TNT_SHEEP_PREFIX = "Module:TNTSheep:";
	
	@EventHandler
	public void onTNTSheepRegistered(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("tntsheep")) {
			ItemStack stack = event.getStack();

			

			//APIUtils.hashIntoInvisibleString(stack, specialString);
		}
		
	}

}
