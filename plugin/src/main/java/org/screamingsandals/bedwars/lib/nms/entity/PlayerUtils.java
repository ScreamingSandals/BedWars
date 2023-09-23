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

package org.screamingsandals.bedwars.lib.nms.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.lib.nms.accessors.*;

import static org.screamingsandals.bedwars.lib.nms.utils.ClassStorage.*;

public class PlayerUtils {
	public static void respawn(Plugin instance, Player player, long delay) {
		new BukkitRunnable() {

			@Override
			public void run() {
				try {
					player.spigot().respawn();
				} catch (Throwable t) {
					try {
						Object selectedObj = ServerboundClientCommandPacket$ActionAccessor.FIELD_PERFORM_RESPAWN.get();
						Object packet = ServerboundClientCommandPacketAccessor.CONSTRUCTOR_0.get()
							.newInstance(selectedObj);
						Object connection = getPlayerConnection(player);
						if (ServerCommonPacketListenerImplAccessor.METHOD_SEND.get() != null) {
							getMethod(connection, ServerCommonPacketListenerImplAccessor.METHOD_SEND.get()).invoke(packet);
						} else {
							getMethod(connection, ServerGamePacketListenerImplAccessor.METHOD_SEND.get()).invoke(packet);
						}
					} catch (Throwable ignored) {
						t.printStackTrace();
					}
				}
			}
		}.runTaskLater(instance, delay);
	}

	public static void fakeExp(Player player, float percentage, int levels) {
		try {
			Object packet = ClientboundSetExperiencePacketAccessor.CONSTRUCTOR_0.get()
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
