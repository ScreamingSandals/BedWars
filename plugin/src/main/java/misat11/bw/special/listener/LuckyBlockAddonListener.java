package misat11.bw.special.listener;

import java.util.List;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import misat11.bw.api.APIUtils;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.api.events.BedwarsPlayerBreakBlock;
import misat11.bw.api.events.BedwarsPlayerBuildBlock;
import misat11.bw.api.special.SpecialItem;
import misat11.bw.special.LuckyBlock;

public class LuckyBlockAddonListener implements Listener {

	public static final String LUCKY_BLOCK_PREFIX = "Module:LuckyBlock:";

	@EventHandler
	public void onLuckyBlockRegistered(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("luckyblock")) {
			ItemStack stack = event.getStack();

			LuckyBlock lucky = new LuckyBlock(event.getGame(), event.getPlayer(),
					event.getGame().getTeamOfPlayer(event.getPlayer()),
					(List<Map<String, Object>>) event.getProperty("data"));

			int id = System.identityHashCode(lucky);

			String luckyBlockString = LUCKY_BLOCK_PREFIX + id;

			APIUtils.hashIntoInvisibleString(stack, luckyBlockString);
		}
	}
	
	@EventHandler
	public void onLuckyBlockBuild(BedwarsPlayerBuildBlock event) {
		if (event.isCancelled()) {
			return;
		}
		
		ItemStack luckyItem = event.getItemInHand();
		String invisible = APIUtils.unhashFromInvisibleStringStartsWith(luckyItem, LUCKY_BLOCK_PREFIX);
		if (invisible != null) {
			String[] splitted = invisible.split(":");
			int classID = Integer.parseInt(splitted[2]);
			
			for (SpecialItem special : event.getGame().getActivedSpecialItems(LuckyBlock.class)) {
				LuckyBlock luckyBlock = (LuckyBlock) special;
				if (System.identityHashCode(luckyBlock) == classID) {
					luckyBlock.place(event.getBlock().getLocation());
					return;
				}
			}
		}
		
	}
	
	@EventHandler
	public void onLuckyBlockBreak(BedwarsPlayerBreakBlock event) {
		if (event.isCancelled()) {
			return;
		}
		for (SpecialItem special : event.getGame().getActivedSpecialItems(LuckyBlock.class)) {
			LuckyBlock luckyBlock = (LuckyBlock) special;
			if (luckyBlock.isPlaced()) {
				if (event.getBlock().getLocation().equals(luckyBlock.getBlockLocation())) {
					event.setDrops(false);
					luckyBlock.process(event.getPlayer());
					return;
				}
			}
		}
	}

}
