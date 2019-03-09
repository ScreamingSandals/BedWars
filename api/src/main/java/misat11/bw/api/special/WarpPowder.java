package misat11.bw.api.special;

import org.bukkit.inventory.ItemStack;

public interface WarpPowder extends SpecialItem {
	public void cancelTeleport(boolean removeSpecial, boolean showMessage, boolean decrementStack);
	
	public ItemStack getStack();
	
	public void runTask();
	
	public void setStackAmount(int amount);
}
