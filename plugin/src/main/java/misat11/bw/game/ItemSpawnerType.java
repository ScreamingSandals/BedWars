package misat11.bw.game;

import static misat11.lib.lang.I18n.i18n;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemSpawnerType implements misat11.bw.api.ItemSpawnerType {
	private String configKey;
	private String name;
	private String translatableKey;
	private double spread;
	private Material material;
	private ChatColor color;
	private int interval;
	private int damage;

	public ItemSpawnerType(String configKey, String name, String translatableKey, double spread, Material material,
			ChatColor color, int interval, int damage) {
		this.configKey = configKey;
		this.name = name;
		this.translatableKey = translatableKey;
		this.spread = spread;
		this.material = material;
		this.color = color;
		this.interval = interval;
		this.damage = damage;
	}

	public String getConfigKey() {
		return configKey;
	}

	public ChatColor getColor() {
		return color;
	}

	public int getInterval() {
		return interval;
	}

	public double getSpread() {
		return spread;
	}

	public String getName() {
		return name;
	}

	public Material getMaterial() {
		return material;
	}

	public String getTranslatableKey() {
		if (translatableKey != null && !translatableKey.equals("")) {
			return i18n(translatableKey, name, false);
		}
		return name;
	}

	public String getItemName() {
		return color + getTranslatableKey();
	}

	public int getDamage() {
		return damage;
	}

	public ItemStack getStack() {
		ItemStack stack = new ItemStack(material, 1, (short) damage);
		ItemMeta stackMeta = stack.getItemMeta();
		stackMeta.setDisplayName(getItemName());
		stack.setItemMeta(stackMeta);
		return stack;
	}
}
