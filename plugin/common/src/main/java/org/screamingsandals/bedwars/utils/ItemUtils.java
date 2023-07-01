/*
 * Copyright (C) 2022 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.utils;

import java.util.ArrayList;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.lib.item.ItemStack;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;

@UtilityClass
public class ItemUtils {
	public final String BEDWARS_NAMESPACED_KEY = "screaming-bedwars-hidden-api";

	public ItemStack saveData(ItemStack item, String data) {
		var itemData = item.getData();
		try {
			var old = itemData.get(BEDWARS_NAMESPACED_KEY, String.class);
			var propertyLines = new ArrayList<String>();
			if (old != null && !old.isEmpty()) {
				propertyLines.addAll(GsonConfigurationLoader.builder().buildAndLoadString(old).getList(String.class));
			}
			propertyLines.add(data);

			itemData.set(BEDWARS_NAMESPACED_KEY, GsonConfigurationLoader.builder().buildAndSaveString(BasicConfigurationNode.root().setList(String.class, propertyLines)), String.class);
		} catch (ConfigurateException | NullPointerException e) {
			e.printStackTrace();
		}
		return item.withData(itemData);
	}

	@Nullable
	public String getIfStartsWith(ItemStack item, String startsWith) {
		var data = item.getData().get(BEDWARS_NAMESPACED_KEY, String.class);
		if (data != null && !data.isEmpty()) {
			try {
				var list = GsonConfigurationLoader.builder().buildAndLoadString(data).getList(String.class);
				for (var l : list) {
					if (l.startsWith(startsWith)) {
						return l;
					}
				}
			} catch (ConfigurateException | NullPointerException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
