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
	
	public Object getProperty(String key) {
		return this.properties.get(key);
	}
	
	public String getStringProperty(String key) {
		return (String) this.properties.get(key);
	}
	
	public byte getByteProperty(String key) {
		return (byte) this.properties.get(key);
	}
	
	public short getShortProperty(String key) {
		return (short) this.properties.get(key);
	}
	
	public int getIntProperty(String key) {
		return (int) this.properties.get(key);
	}
	
	public long getLongProperty(String key) {
		return (long) this.properties.get(key);
	}
	
	public float getFloatProperty(String key) {
		return (float) this.properties.get(key);
	}
	
	public double getDoubleProperty(String key) {
		return (double) this.properties.get(key);
	}
	
	public boolean getBooleanProperty(String key) {
		return (boolean) this.properties.get(key);
	}
	
	public char getCharProperty(String key) {
		return (char) this.properties.get(key);
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsApplyPropertyToBoughtItem.handlers;
	}
}
