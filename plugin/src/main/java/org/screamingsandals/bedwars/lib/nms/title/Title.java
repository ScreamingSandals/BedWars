package org.screamingsandals.bedwars.lib.nms.title;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.lib.nms.utils.ClassStorage;

public class Title {
	public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		try {
			player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
		} catch (Throwable t) {
			try {
				Object titleComponent = ClassStorage.getMethod(ClassStorage.NMS.ChatSerializer, "a,field_150700_a", String.class)
					.invokeStatic("{\"text\": \"" + title + "\"}");
				Object subtitleComponent = ClassStorage.getMethod(ClassStorage.NMS.ChatSerializer, "a,field_150700_a", String.class)
					.invokeStatic("{\"text\": \"" + subtitle + "\"}");
				
				Object titlePacket = ClassStorage.NMS.PacketPlayOutTitle.getConstructor(ClassStorage.NMS.EnumTitleAction, ClassStorage.NMS.IChatBaseComponent)
					.newInstance(ClassStorage.findEnumConstant(ClassStorage.NMS.EnumTitleAction, "TITLE"), titleComponent);
				Object subtitlePacket = ClassStorage.NMS.PacketPlayOutTitle.getConstructor(ClassStorage.NMS.EnumTitleAction, ClassStorage.NMS.IChatBaseComponent)
					.newInstance(ClassStorage.findEnumConstant(ClassStorage.NMS.EnumTitleAction, "SUBTITLE"), subtitleComponent);
				Object timesPacket = ClassStorage.NMS.PacketPlayOutTitle
					.getConstructor(ClassStorage.NMS.EnumTitleAction, ClassStorage.NMS.IChatBaseComponent, int.class, int.class, int.class)
					.newInstance(ClassStorage.findEnumConstant(ClassStorage.NMS.EnumTitleAction, "TIMES"), null, fadeIn, stay, fadeOut);
				
				ClassStorage.sendPacket(player, titlePacket);
				ClassStorage.sendPacket(player, subtitlePacket);
				ClassStorage.sendPacket(player, timesPacket);
			} catch (Throwable ignored) {
			}
		}
	}
}
