package org.screamingsandals.bedwars.utils;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.screamingsandals.bedwars.api.BedwarsAPI;

/**
 * @author Bedwars Team
 *
 * THIS CLASS WILL BE REPLACED WITH SCREAMING LIB EQUIVALENT
 */
// TODO: ScreamingLib equivalent
@Deprecated
@ApiStatus.ScheduledForRemoval
public class ItemUtils {
	public static final String BEDWARS_NAMESPACED_KEY = "screaming-bedwars-hidden-api";

	/**
	 * @param stack
	 * @param hash
	 */
	public static void hashIntoInvisibleString(ItemStack stack, String hash) {
		ItemMeta meta = stack.getItemMeta();
		try {
			NamespacedKey key = new NamespacedKey(BedwarsAPI.getInstance().as(Plugin.class), BEDWARS_NAMESPACED_KEY);
			PersistentDataContainer container = meta.getPersistentDataContainer();
			List<String> propertyLines = new ArrayList<>();
			if (container.has(key, PersistentDataType.STRING)) {
				String oldString = container.get(key, PersistentDataType.STRING);
				propertyLines.addAll((List<String>) new Gson().fromJson(oldString, List.class));
			}
			propertyLines.add(hash);
			container.set(key, PersistentDataType.STRING, new Gson().toJson(propertyLines));
		} catch (Throwable ignored) {
			ignored.printStackTrace();
			// Use the Lore API instead
			List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();

			lore.add(convertToInvisibleString(hash));
			meta.setLore(lore);
		}
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
			NamespacedKey key = new NamespacedKey(BedwarsAPI.getInstance().as(Plugin.class), BEDWARS_NAMESPACED_KEY);
			PersistentDataContainer container = meta.getPersistentDataContainer();
			if (container.has(key, PersistentDataType.STRING)) {
				String oldString = container.get(key, PersistentDataType.STRING);
				List<String> propertyLines = (List<String>) new Gson().fromJson(oldString, List.class);
				for (String unhidden : propertyLines) {
					if (unhidden.startsWith(startsWith)) {
						return unhidden;
					}
				}
			}
		} catch (Throwable ignored) {
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
			} catch (NullPointerException ignored2) {
			}
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
			NamespacedKey key = new NamespacedKey(BedwarsAPI.getInstance().as(Plugin.class), BEDWARS_NAMESPACED_KEY);
			PersistentDataContainer container = meta.getPersistentDataContainer();
			if (container.has(key, PersistentDataType.STRING)) {
				String oldString = container.get(key, PersistentDataType.STRING);
				List<String> propertyLines = (List<String>) new Gson().fromJson(oldString, List.class);
				for (String unhidden : propertyLines) {
					if (unhidden.equals(hash)) {
						return true;
					}
				}
			}
		} catch (Throwable ignored) {
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
			} catch (NullPointerException ignored2) {
				return false;
			}
		}
		return false;
	}

	private static String convertToInvisibleString(String s) {
		StringBuilder hidden = new StringBuilder();
		for (char c : s.toCharArray()) {
			hidden.append(ChatColor.COLOR_CHAR + "").append(c);
		}
		return hidden.toString();
	}

	private static String returnFromInvisibleString(String s) {
		return s.replaceAll(String.valueOf(ChatColor.COLOR_CHAR), "");
	}
}
