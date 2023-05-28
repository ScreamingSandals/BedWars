/*
 * Copyright (C) 2023 ScreamingSandals
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
import org.screamingsandals.bedwars.lib.nms.accessors.ClientboundSetTitleTextPacketAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.ClientboundSetTitlesPacket$TypeAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.Component$SerializerAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

public class Title {
	public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		try {
			player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		} catch (Throwable t) {
			try {
				Object titleComponent = ClassStorage.getMethod(Component$SerializerAccessor.METHOD_FROM_JSON.get())
					.invokeStatic("{\"text\": \"" + title + "\"}");
				Object subtitleComponent = ClassStorage.getMethod(Component$SerializerAccessor.METHOD_FROM_JSON.get())
					.invokeStatic("{\"text\": \"" + subtitle + "\"}");
				
				Object titlePacket = ClientboundSetTitleTextPacketAccessor.CONSTRUCTOR_0.get()
					.newInstance(ClientboundSetTitlesPacket$TypeAccessor.FIELD_TITLE.get(), titleComponent);
				Object subtitlePacket = ClientboundSetTitleTextPacketAccessor.CONSTRUCTOR_0.get()
					.newInstance(ClientboundSetTitlesPacket$TypeAccessor.FIELD_SUBTITLE.get(), subtitleComponent);
				Object timesPacket = ClientboundSetTitleTextPacketAccessor.CONSTRUCTOR_1.get()
					.newInstance(ClientboundSetTitlesPacket$TypeAccessor.FIELD_TIMES.get(), null, fadeIn, stay, fadeOut);
				
				ClassStorage.sendPacket(player, titlePacket);
				ClassStorage.sendPacket(player, subtitlePacket);
				ClassStorage.sendPacket(player, timesPacket);
			} catch (Throwable ignored) {
			}
		}
	}
}
