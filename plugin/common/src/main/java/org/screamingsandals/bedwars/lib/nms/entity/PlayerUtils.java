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

package org.screamingsandals.bedwars.lib.nms.entity;

import lombok.experimental.UtilityClass;
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

@UtilityClass
public class PlayerUtils {
	public void respawn(PlayerWrapper playerWrapper, long delay) {
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

	public void fakeExp(PlayerWrapper player, float percentage, int levels) {
		new SClientboundSetExperiencePacket()
				.percentage(percentage)
				.totalExperience(player.as(Player.class).getTotalExperience()) // TODO
				.level(levels)
				.sendPacket(player);
	}
}
