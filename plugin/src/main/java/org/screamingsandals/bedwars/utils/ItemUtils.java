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
import java.util.List;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.lib.item.Item;
import org.screamingsandals.lib.utils.GsonUtils;

@UtilityClass
public class ItemUtils {
	public final String BEDWARS_NAMESPACED_KEY = "screaming-bedwars-hidden-api";

	@SuppressWarnings("unchecked")
	public Item saveData(Item item, String data) {
		var itemData = item.getData();
		var old = itemData.get(BEDWARS_NAMESPACED_KEY, String.class);
		var propertyLines = new ArrayList<String>();
		if (old != null && !old.isEmpty()) {
			propertyLines.addAll((List<String>) GsonUtils.gson().fromJson(old, List.class));
		}
		propertyLines.add(data);

		itemData.set(BEDWARS_NAMESPACED_KEY, GsonUtils.gson().toJson(propertyLines), String.class);
		return item.withData(itemData);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public String getIfStartsWith(Item item, String startsWith) {
		var data = item.getData().get(BEDWARS_NAMESPACED_KEY, String.class);
		if (data != null && !data.isEmpty()) {
			var list = (List<String>) GsonUtils.gson().fromJson(data, List.class);
			for (var l : list) {
				if (l.startsWith(startsWith)) {
					return l;
				}
			}
		}
		return null;
	}
}
