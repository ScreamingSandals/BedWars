package org.screamingsandals.bedwars.lib.nms.title;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.lib.nms.accessors.IChatBaseComponent_i_ChatSerializerAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.PacketPlayOutTitleAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.PacketPlayOutTitle_i_EnumTitleActionAccessor;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

public class Title {
	public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		try {
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
