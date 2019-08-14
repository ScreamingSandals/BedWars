package misat11.bw.api;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Bedwars Team
 *
 */
public class APIUtils {
	/**
	 * @param stack
	 * @param hash
	 */
	public static void hashIntoInvisibleString(ItemStack stack, String hash) {
		ItemMeta meta = stack.getItemMeta();

		List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

		lore.add(convertToInvisibleString(hash));

		meta.setLore(lore);

		stack.setItemMeta(meta);
	}

	/**
	 * @param stack
	 * @param startsWith
	 * @return
	 */
	public static String unhashFromInvisibleStringStartsWith(ItemStack stack, String startsWith) {
		ItemMeta meta = stack.getItemMeta();
		try {
			if (meta.hasLore()) {
				List<String> lore = meta.getLore();
				for (String s : lore) {
					String unhidden = returnFromInvisibleString(s);
					if (unhidden.startsWith(startsWith)) {
						return unhidden;
					}
				}
			}
		} catch (NullPointerException ignored) {
			return null;
		}
		return null;
	}

	/**
	 * @param stack
	 * @param hash
	 * @return
	 */
	public static boolean unhashFromInvisibleString(ItemStack stack, String hash) {
		ItemMeta meta = stack.getItemMeta();
		try {
			if (meta.hasLore()) {
				List<String> lore = meta.getLore();
				for (String s : lore) {
					String unhidden = returnFromInvisibleString(s);
					if (unhidden.equals(hash)) {
						return true;
					}
				}
			}
		} catch (NullPointerException ignored) {
			return false;
		}
		return false;
	}

	private static String convertToInvisibleString(String s) {
		String hidden = "";
		for (char c : s.toCharArray()) {
			hidden += ChatColor.COLOR_CHAR + "" + c;
		}
		return hidden;
	}

	private static String returnFromInvisibleString(String s) {
		return s.replaceAll(String.valueOf(ChatColor.COLOR_CHAR), "");
	}
}
