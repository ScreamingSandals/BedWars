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

import static org.screamingsandals.bedwars.lib.nms.utils.ClassStorage.*;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.lib.nms.accessors.PacketPlayInClientCommandAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.PacketPlayInClientCommand_i_EnumClientCommandAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.PacketPlayOutExperienceAccessor;
import org.screamingsandals.bedwars.lib.nms.accessors.PlayerConnectionAccessor;

public class PlayerUtils {
	public static void respawn(Plugin instance, Player player, long delay) {
		new BukkitRunnable() {

			@Override
			public void run() {
				try {
					player.spigot().respawn();
				} catch (Throwable t) {
					try {
						Object selectedObj = PacketPlayInClientCommand_i_EnumClientCommandAccessor.getFieldPERFORM_RESPAWN();
						Object packet = PacketPlayInClientCommandAccessor.getConstructor0()
							.newInstance(selectedObj);
						Object connection = getPlayerConnection(player);
						getMethod(connection, PlayerConnectionAccessor.getMethodFunc_147342_a1()).invoke(packet);
					} catch (Throwable ignored) {
						t.printStackTrace();
					}
				}
			}
		}.runTaskLater(instance, delay);
	}

	public static void fakeExp(Player player, float percentage, int levels) {
		try {
			Object packet = PacketPlayOutExperienceAccessor.getConstructor0()
				.newInstance(percentage, player.getTotalExperience(), levels);
			sendPacket(player, packet);
		} catch (Throwable t) {
		}
	}

	public static boolean teleportPlayer(Player player, Location location) {
		try {
			return player.teleportAsync(location).isDone();
		} catch (Throwable t) {
			player.teleport(location);
			return true;
		}
	}

	public static boolean teleportPlayer(Player player, Location location, Runnable runnable) {
		try {
			return player.teleportAsync(location).thenRun(runnable).isDone();
		} catch (Throwable t) {
			player.teleport(location);
			if (!Main.getInstance().isEnabled()) {
				runnable.run();
			} else {
				Bukkit.getScheduler().runTaskLater(Main.getInstance(), runnable, 2); // player.teleport is synchronized, we don't have to wait
			}
			return true;
		}
	}
}
