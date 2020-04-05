package org.screamingsandals.lib.nms.entity;

import static org.screamingsandals.lib.nms.utils.ClassStorage.*;
import static org.screamingsandals.lib.nms.utils.ClassStorage.NMS.*;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerUtils {
	public static void respawn(Plugin instance, Player player, long delay) {
		new BukkitRunnable() {

			@Override
			public void run() {
				try {
					player.spigot().respawn();
				} catch (Throwable t) {
					try {
						Object selectedObj = findEnumConstant(EnumClientCommand, "PERFORM_RESPAWN");
						Object packet = PacketPlayInClientCommand.getDeclaredConstructor(EnumClientCommand)
							.newInstance(selectedObj);
						Object connection = getPlayerConnection(player);
						getMethod(connection, "a,func_147342_a", PacketPlayInClientCommand).invoke(packet);
					} catch (Throwable ignored) {
						t.printStackTrace();
					}
				}
			}
		}.runTaskLater(instance, delay);
	}

	public static void fakeExp(Player player, float percentage, int levels) {
		try {
			Object packet = PacketPlayOutExperience.getConstructor(float.class, int.class, int.class)
				.newInstance(percentage, player.getTotalExperience(), levels);
			sendPacket(player, packet);
		} catch (Throwable t) {
		}
	}

	public static void teleportPlayer(Player player, Location location) {
		try {
			player.teleportAsync(location);
		} catch (Throwable t) {
			player.teleport(location);
		}
	}
}
