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

package org.screamingsandals.bedwars.lib.nms.title;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.lib.nms.accessors.IChatBaseComponent_i_ChatSerializerAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.PacketPlayOutTitleAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.PacketPlayOutTitle_i_EnumTitleActionAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

public class Title {
	public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		try {
			// I was thinking about fixing this in CraftBukkit, but then I remembered of BuildTools, so here's a workaround
			if (title.isEmpty()) {
				title = " ";
			}
			if (subtitle.isEmpty()) {
				subtitle = " ";
			}
			player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		} catch (Throwable t) {
			try {
				Object titleComponent = ClassStorage.getMethod(IChatBaseComponent_i_ChatSerializerAccessor.getMethodFunc_150699_a1())
					.invokeStatic("{\"text\": \"" + title + "\"}");
				Object subtitleComponent = ClassStorage.getMethod(IChatBaseComponent_i_ChatSerializerAccessor.getMethodFunc_150699_a1())
					.invokeStatic("{\"text\": \"" + subtitle + "\"}");
				
				Object titlePacket = PacketPlayOutTitleAccessor.getConstructor0()
					.newInstance(PacketPlayOutTitle_i_EnumTitleActionAccessor.getFieldTITLE(), titleComponent);
				Object subtitlePacket = PacketPlayOutTitleAccessor.getConstructor0()
					.newInstance(PacketPlayOutTitle_i_EnumTitleActionAccessor.getFieldSUBTITLE(), subtitleComponent);
				Object timesPacket = PacketPlayOutTitleAccessor.getConstructor1()
					.newInstance(PacketPlayOutTitle_i_EnumTitleActionAccessor.getFieldTIMES(), null, fadeIn, stay, fadeOut);
				
				ClassStorage.sendPacket(player, titlePacket);
				ClassStorage.sendPacket(player, subtitlePacket);
				ClassStorage.sendPacket(player, timesPacket);
			} catch (Throwable ignored) {
			}
		}
	}
}
