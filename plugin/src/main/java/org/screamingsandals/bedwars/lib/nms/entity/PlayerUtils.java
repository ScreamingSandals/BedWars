package org.screamingsandals.bedwars.lib.nms.entity;

import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.nms.accessors.ServerGamePacketListenerImplAccessor;
import org.screamingsandals.bedwars.nms.accessors.ServerboundClientCommandPacketAccessor;
import org.screamingsandals.bedwars.nms.accessors.ServerboundClientCommandPacket_i_ActionAccessor;
import org.screamingsandals.lib.bukkit.utils.nms.ClassStorage;
import org.screamingsandals.lib.packet.SClientboundSetExperiencePacket;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.reflect.Reflect;

public class PlayerUtils {
	public static void respawn(PlayerWrapper playerWrapper, long delay) {
		var player = playerWrapper.as(Player.class);
		Tasker.build(() -> {
			try {
				player.spigot().respawn();
			} catch (Throwable t) {
				try {
					var selectedObj = ServerboundClientCommandPacket_i_ActionAccessor.getFieldPERFORM_RESPAWN();
					var packet = Reflect.construct(ServerboundClientCommandPacketAccessor.getConstructor0(), selectedObj);
					var connection = ClassStorage.getPlayerConnection(player);
					Reflect.fastInvoke(connection, ServerGamePacketListenerImplAccessor.getMethodHandleClientCommand1(), packet);
				} catch (Throwable ignored) {
					t.printStackTrace();
				}
			}
		}).delay(delay, TaskerTime.TICKS).start();
	}

	public static void fakeExp(PlayerWrapper player, float percentage, int levels) {
		var experience = new SClientboundSetExperiencePacket();
		experience.percentage(percentage);
		experience.totalExperience(player.as(Player.class).getTotalExperience()); // TODO
		experience.level(levels);
		experience.sendPacket(player);
	}
}
