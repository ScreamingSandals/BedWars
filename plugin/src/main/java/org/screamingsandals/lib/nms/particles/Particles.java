package org.screamingsandals.lib.nms.particles;

import static org.screamingsandals.lib.nms.utils.ClassStorage.*;
import static org.screamingsandals.lib.nms.utils.ClassStorage.NMS.*;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Particles {
	public static void sendParticles(List<Player> viewers, String particleName, Location loc, int count, double offsetX,
		double offsetY, double offsetZ, double extra) {
		for (Player player : viewers) {
			try {
				player.spawnParticle(Particle.valueOf(particleName.toUpperCase()), loc.getX(), loc.getY(), loc.getZ(),
					count, offsetX, offsetY, offsetZ, extra);
			} catch (Throwable t) {
				try {
					Object selectedParticle = null;
					particleName = particleName.toUpperCase();
					for (Object obj : EnumParticle.getEnumConstants()) {
						if (particleName.equalsIgnoreCase((String) getMethod(obj, "b").invoke())) {
							selectedParticle = obj;
							break;
						}
					}
					Object packet = PacketPlayOutWorldParticles
						.getConstructor(EnumParticle, boolean.class, float.class, float.class, float.class, float.class,
							float.class, float.class, float.class, int.class, int[].class)
						.newInstance(selectedParticle, true, loc.getX(), loc.getY(), loc.getZ(), offsetX, offsetY,
							offsetZ, extra, count, new int[] {});
					sendPacket(player, packet);
				} catch (Throwable ignored) {

				}
			}
		}
	}
}
