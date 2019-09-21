package misat11.bw.api.events;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import misat11.bw.api.Game;

public class BedwarsApplyPropertyToItem extends Event {

    private static final HandlerList handlers = new HandlerList();
	private Game game = null;
	private Player player = null;
	private ItemStack stack = null;
	private Map<String, Object> properties = null;

	/**
	 * @param game
	 * @param player
	 * @param stack
	 * @param properties
	 */
	public BedwarsApplyPropertyToItem(Game game, Player player, ItemStack stack, Map<String, Object> properties) {
		this.game = game;
		this.player = player;
		this.stack = stack;
		this.properties = properties;
	}

	/**
	 * @return
	 */
	public Game getGame() {
		return this.game;
	}
	
	/**
	 * @return
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * @param stack
	 * @return
	 */
	public boolean setStack(ItemStack stack) {
		if (stack == null){
			return false;
		}
		this.stack = stack;
		return true;
	}
	
	/**
	 * @return
	 */
	public ItemStack getStack() {
		return this.stack;
	}
	
	/**
	 * @return
	 */
	public Map<String, Object> getProperties(){
		return this.properties;
	}
	
	/**
	 * @return
	 */
	public String getPropertyName() {
		return (String) this.properties.get("name");
	}
	
	/**
	 * @param key
	 * @return
	 */
	public Object getProperty(String key) {
		return this.properties.get(key);
	}
	
	/**
	 * @param key
	 * @return
	 */
	public String getStringProperty(String key) {
		return (String) this.properties.get(key);
	}
	
	/**
	 * @param key
	 * @return
	 */
	public byte getByteProperty(String key) {
		return (byte) this.properties.get(key);
	}
	
	/**
	 * @param key
	 * @return
	 */
	public short getShortProperty(String key) {
		return (short) this.properties.get(key);
	}
	
	/**
	 * @param key
	 * @return
	 */
	public int getIntProperty(String key) {
		return (int) this.properties.get(key);
	}
	
	/**
	 * @param key
	 * @return
	 */
	public long getLongProperty(String key) {
		return (long) this.properties.get(key);
	}
	
	/**
	 * @param key
	 * @return
	 */
	public float getFloatProperty(String key) {
		return (float) this.properties.get(key);
	}
	
	/**
	 * @param key
	 * @return
	 */
	public double getDoubleProperty(String key) {
		return (double) this.properties.get(key);
	}
	
	/**
	 * @param key
	 * @return
	 */
	public boolean getBooleanProperty(String key) {
		return (boolean) this.properties.get(key);
	}
	
	/**
	 * @param key
	 * @return
	 */
	public char getCharProperty(String key) {
		return (char) this.properties.get(key);
	}

	@Override
	public HandlerList getHandlers() {
		return BedwarsApplyPropertyToItem.handlers;
	}

	public static HandlerList getHandlerList() {
		return BedwarsApplyPropertyToItem.handlers;
	}
}
