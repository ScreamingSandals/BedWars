package misat11.bw.api.events;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import misat11.bw.api.Game;

public class BedwarsApplyPropertyToBoughtItem extends Event  {

	private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private Player player = null;
	private ItemStack stack = null;
	private Map<String, Object> properties = null;

	public BedwarsApplyPropertyToBoughtItem(Game game, Player player, ItemStack stack, Map<String, Object> properties) {
		this.game = game;
		this.player = player;
		this.stack = stack;
		this.properties = properties;
	}

	public static HandlerList getHandlerList() {
		return BedwarsApplyPropertyToBoughtItem.handlers;
	}

	public Game getGame() {
		return this.game;
	}
	
	public Player getPlayer() {
		return this.player;
	}
	
	public boolean setStack(ItemStack stack) {
		if (stack == null){
			return false;
		}
		this.stack = stack;
		return true;
	}
	
	public ItemStack getStack() {
		return this.stack;
	}
	
	public Map<String, Object> getProperties(){
		return this.properties;
	}
	
	public String getPropertyName() {
		return (String) this.properties.get("name");
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsApplyPropertyToBoughtItem.handlers;
	}
}
